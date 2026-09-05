package `is`.codion.demos.chinook.android.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import `is`.codion.android.common.ui.control.Control
import `is`.codion.android.common.ui.observeAsState
import `is`.codion.android.framework.model.AndroidEntityEditModel
import `is`.codion.android.framework.model.AndroidEntityTableModel
import `is`.codion.common.reactive.value.Value
import `is`.codion.demos.chinook.domain.api.Chinook.Customer
import `is`.codion.demos.chinook.domain.api.Chinook.Invoice
import `is`.codion.framework.db.EntityConnection
import `is`.codion.framework.domain.entity.Entity
import `is`.codion.framework.domain.entity.attribute.Column
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// A pending report request — the table model to fill the report against, the viewer title and cache file name, and the
// fetch that produces the PDF bytes from a connection — set on the main thread by a report control (below) and observed
// by [ReportLauncher], which does the fetch and hand-off. A module-level Value, the established non-Compose→Compose
// bridge (observeAsState): a control is built on a background thread and can hold neither Compose state nor an Android
// Context, so it only flips this holder. The control captures the selected ids (an in-memory read) into [fetch] at tap
// time; the connection is resolved later, on the IO dispatcher, because connection() reconnects (network I/O) if it
// went stale (a server restart while the app sits open), which would crash if done on the main thread.
private class ReportRequest(
	val connection: () -> EntityConnection,
	val title: String,
	val fileName: String,
	val fetch: (EntityConnection) -> ByteArray,
)

private val reportRequest: Value<ReportRequest> = Value.nullable()

/**
 * A custom table action that fills a `byte[]` PDF report over the selected rows and opens it in the device's PDF
 * viewer — the Android counterpart of a Swing table PRINT control. Enabled only while rows are selected; on tap it
 * reads the selected [idColumn] values (an in-memory read) and hands a fetch closure to [ReportLauncher], which
 * resolves the connection off the main thread. The report is filled server-side and returned as PDF bytes, so this
 * client needs no JasperReports dependency. See [customerReportControl] / [invoiceReportControl].
 */
private fun reportControl(
	tableModel: AndroidEntityTableModel,
	caption: String,
	fileName: String,
	idColumn: Column<Long>,
	fetch: (EntityConnection, Collection<Long>) -> ByteArray,
): Control {
	val selection = tableModel.selection()
	return Control.builder()
		.caption(caption)
		.enabled(selection.present())
		.command {
			val ids = Entity.values<Long>(idColumn, selection.items().get())
			reportRequest.set(ReportRequest(tableModel::connection, caption.removeSuffix("…"), fileName) { connection ->
				fetch(connection, ids)
			})
		}
		.build()
}

// The three report actions below are offered ONLY over HTTP (ChinookConnection.HTTP), which is why the call sites
// register them conditionally rather than always. The reports are filled server-side and returned as PDF bytes, so an
// HTTP client needs neither JasperReports nor the .jasper files - which is what makes them viable on a phone at all.
// The on-device H2 connection has no server to fill them: it would have to do it in-process, and this client carries
// neither the library nor the reports. Better an action that is absent offline than one that is present and fails.

/** The "Customer report…" table action — a customer report over the selected customers (Customer.REPORT_PDF). */
internal fun customerReportControl(tableModel: AndroidEntityTableModel): Control =
	reportControl(tableModel, "Customer report…", "customer_report.pdf", Customer.ID) { connection, ids ->
		connection.report(Customer.REPORT_PDF, mapOf<String, Any>("CUSTOMER_IDS" to ids))
	}

/** The "Invoice…" table action — a printable invoice per selected invoice (Invoice.REPORT_PDF). */
internal fun invoiceReportControl(tableModel: AndroidEntityTableModel): Control =
	reportControl(tableModel, "Invoice…", "invoice.pdf", Invoice.ID) { connection, ids ->
		connection.report(Invoice.REPORT_PDF, mapOf<String, Any>("INVOICE_IDS" to ids))
	}

/**
 * The "Invoice…" **form** action — the same printable invoice, for the record the form is on, reached from the edit
 * view's actions overflow (`EntityEditView.Config.control`) rather than the table's.
 *
 * Where the table action gates on the selection, this gates on the editor's `entity().exists()`, since
 * an invoice that has not been inserted yet has no id to fill a report against and nothing worth printing. That
 * difference — selection for a table, editor state for a form — is the whole of what distinguishes the two.
 */
internal fun invoiceReportControl(editModel: AndroidEntityEditModel): Control {
	val entity = editModel.editor().entity()
	return Control.builder()
		.caption("Invoice…")
		.enabled(entity.exists())
		.command {
			// Read on the main thread, as the table action reads its selection: an in-memory get, no round trip.
			val id = entity.get().get(Invoice.ID)
			reportRequest.set(ReportRequest(editModel::connection, "Invoice", "invoice.pdf") { connection ->
				connection.report(Invoice.REPORT_PDF, mapOf<String, Any>("INVOICE_IDS" to listOf(id)))
			})
		}
		.build()
}

/**
 * Fills the requested report off the main thread, writes the PDF to a shareable cache file and opens it in the
 * device's installed PDF viewer (an ACTION_VIEW intent over a [FileProvider] content URI — a raw file URI is refused
 * across app boundaries since API 24). Shows a spinner while fetching and a message if it fails or no viewer is
 * installed. Rendered once at the app root (see MainActivity); dormant until a report control posts a request.
 */
@Composable
internal fun ReportLauncher() {
	val request by reportRequest.observeAsState()
	val current = request ?: return
	val context = LocalContext.current
	var error by remember(current) { mutableStateOf<String?>(null) }
	LaunchedEffect(current) {
		error = null
		try {
			//connection() is resolved here, on IO, not at tap time: it reconnects (network I/O) if the connection
			//went stale, which would be a NetworkOnMainThreadException if done on the main thread.
			val pdf = withContext(Dispatchers.IO) { current.fetch(current.connection()) }
			val uri = withContext(Dispatchers.IO) { writeReport(context, current.fileName, pdf) }
			context.startActivity(
				Intent(Intent.ACTION_VIEW).apply {
					setDataAndType(uri, "application/pdf")
					addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
				},
			)
			reportRequest.clear()
		} catch (e: ActivityNotFoundException) {
			error = "No app is installed to view PDF files"
		} catch (e: Exception) {
			error = e.message ?: "${current.title} failed"
		}
	}
	val message = error
	if (message == null) {
		//fetching + preparing the PDF, before the external viewer opens (cleared when the intent fires)
		AlertDialog(
			onDismissRequest = { reportRequest.clear() },
			confirmButton = {},
			title = { Text(current.title) },
			text = {
				Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
					CircularProgressIndicator(Modifier.size(24.dp))
					Text("Preparing…")
				}
			},
		)
	} else {
		AlertDialog(
			onDismissRequest = { reportRequest.clear() },
			confirmButton = { TextButton(onClick = { reportRequest.clear() }) { Text("OK") } },
			title = { Text(current.title) },
			text = { Text(message) },
		)
	}
}

/** Writes the PDF [bytes] to a shareable cache file named [fileName] and returns a [FileProvider] content URI for it. */
private fun writeReport(context: Context, fileName: String, bytes: ByteArray): Uri {
	val directory = File(context.cacheDir, "reports").apply { mkdirs() }
	val file = File(directory, fileName)
	file.writeBytes(bytes)

	return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
