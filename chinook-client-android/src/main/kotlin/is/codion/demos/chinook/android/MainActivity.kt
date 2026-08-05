package `is`.codion.demos.chinook.android

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import `is`.codion.android.framework.model.AndroidEntityApplicationModel
import `is`.codion.android.framework.model.AndroidEntityModel
import `is`.codion.android.framework.ui.*
import `is`.codion.common.db.database.Database
import `is`.codion.common.utilities.user.User
import `is`.codion.demos.chinook.domain.api.Chinook.*
import `is`.codion.framework.db.EntityConnection
import `is`.codion.framework.db.EntityConnection.CLIENT_CONNECTION_TYPE
import `is`.codion.framework.db.http.HttpEntityConnection
import java.io.File

// ---- Codion wiring -----------------------------------------------------------------------------

// Flip this when testing: true = the public Render cloud server (HTTPS on 443; Render terminates TLS at the edge
// and forwards to the server's HTTP port 8088), false = a local LAN dev server (plain HTTP). Local mode needs
// android:usesCleartextTraffic in the manifest (it's set) and LOCAL_HOSTNAME pointing at the dev machine's IP.
private const val USE_HTTP = true

//private const val HTTP_HOSTNAME = "chinook-server-latest.onrender.com"
//private const val HTTP_PORT = 443
//private const val HTTP_HOSTNAME = "10.167.2.63" // the dev machine on the LAN — adjust to your network
private const val HTTP_HOSTNAME = "192.168.1.18" // the dev machine on the LAN — adjust to your network
private const val HTTP_PORT = 8088

// The on-device H2 file database name — H2 manages "<name>.mv.db" under the app's no-backup storage.
private const val DATABASE_NAME = "chinook"

// The "DB fully seeded" marker, written only after a confirmed connect+seed (buildApplicationView) and checked by
// the onCreate partial-seed guard. Set in onCreate, before the connection is established.
private lateinit var seededMarker: File

// Turns the connection into the application — Album (master) with its Tracks (detail). Run off the UI thread
// by EntityApplication's ViewModel; no Activity/Context capture (the ViewModel outlives it).
private fun buildApplicationView(connection: EntityConnection): EntityApplicationView<AndroidEntityApplicationModel> {
    // First launch: the connection is established when built, so by the time we get here the seed (H2's INIT_SCRIPTS)
    // has run synchronously on the ViewModel's background thread, before any async table refresh. Mark the DB fully
    // seeded — a mid-seed process kill must not leave a half-populated file the next launch mistakes for ready (see
    // the partial-seed guard in onCreate). A no-op once already seeded.
    if (!USE_HTTP) {
        if (!seededMarker.exists()) {
            seededMarker.createNewFile()
        }
    }
    EntityEditView.Config.MODIFIED_WARNING.set(true);
    // Models — mirror ChinookAppModel: three roots, each with its own detail tree.
    //   Album → Track,  Playlist → PlaylistTrack,  Customer → Invoice → InvoiceLine.
    val albumModel = AlbumModel(connection)
    val trackModel = albumModel.detail().get(Track.TYPE);

    val playlistModel = AndroidEntityModel(PlaylistEditModel(connection))
    val playlistTrackModel = AndroidEntityModel(PlaylistTrackEditModel(connection))
    // PlaylistTrack references both Playlist and Track, so pin the foreign key (mirrors PlaylistModel).
    playlistModel.detail().add(playlistTrackModel, PlaylistTrack.PLAYLIST_FK)

    val customerModel = AndroidEntityModel(CustomerEditModel(connection))
    val invoiceModel = InvoiceModel(connection)
    customerModel.detail().add(invoiceModel) // Invoice.CUSTOMER_FK auto-detected

    val applicationModel = AndroidEntityApplicationModel(connection, listOf(albumModel, playlistModel, customerModel))

    // Views — mirror ChinookAppPanel: one EntityView per model, wired into the same master/detail tree. The navigator
    // renders one at a time, moved through via the top-level root tabs + breadcrumb + drill buttons + sibling tabs.
    // Album.RATING is a read-only average (a subquery over its tracks), so it has no edit-form override; the table
    // shows it as a compact star meter via the table view's per-attribute renderer seam.
    val albumView = EntityView(albumModel, table = EntityTableView(albumModel.tableModel()) {
        renderer(Album.RATING) { value, definition -> RatingStarsCell(value, definition) }
    })
    // Track.RATING (1–10): tappable stars in the edit form (component seam) and a star meter in the table (renderer
    // seam) — the editor writes the value, the renderer only displays it. Same RatingStars* pair, two seams.
    val trackView = EntityView(
        trackModel,
        edit = EntityEditView(trackModel.editModel()) {
            // Reduce the kitchen-sink Track form to the essentials, in this order (the mobile projection lever). The
            // omitted required columns stay covered by the model: ALBUM_FK is supplied by the master (Album→Track detail
            // link), RATING (default 5) and PLAY_COUNT (default 0) carry default values — so the form still inserts.
            attributes(Track.NAME, Track.RATING, Track.MEDIATYPE_FK, Track.MILLISECONDS, Track.UNITPRICE)
            component(Track.RATING) { value, definition -> RatingStars(value, definition) }
        },
        table = EntityTableView(trackModel.tableModel()) {
            // Reduce the many Track columns to a focused mobile set, in this order.
            attributes(Track.NAME, Track.ALBUM_FK, Track.RATING, Track.UNITPRICE)
            renderer(Track.RATING) { value, definition -> RatingStarsCell(value, definition) }
        },
    )
    albumView.detail().add(trackView)

    val playlistView = EntityView(playlistModel)
    val playlistTrackView = EntityView(playlistTrackModel)
    playlistView.detail().add(playlistTrackView)

    // In landscape, show the Customer edit form beside the table (edit left, table right); portrait stays one-at-a-time.
    // Edit the customer's Preferences (a one-to-one detail) inline on the customer form — persisted in the same
    // transaction. The detail editor is registered by the shared CustomerEditConfig (via EntityEditor.create); here we
    // just project which of its fields appear, mirroring the Swing CustomerEditPanel's preferences section.
    val customerView = EntityView(
        customerModel,
        edit = EntityEditView(customerModel.editModel()) {
            detail(Preferences.CUSTOMER_FK) {
                attributes(Preferences.NEWSLETTER, Preferences.PREFERRED_GENRE_FK)
            }
        },
        // A custom table action (Config.control) — "Customer report…", enabled while customers are selected, filling a
        // PDF server-side and opening it in the device's PDF viewer (ReportLauncher). The Android counterpart of
        // the Swing CustomerTablePanel PRINT control, reached from the action bar's actions overflow.
        table = EntityTableView(customerModel.tableModel()) {
            control(customerReportControl(customerModel.tableModel()))
        },
    ) { splitInLandscape = true }
    // A custom table action (Config.control) on the invoice table — "Invoice…", printing the selected invoices
    // (Invoice.REPORT_PDF, filled server-side) and opening them in the device's PDF viewer, one page per invoice.
    val invoiceView = EntityView(
        invoiceModel,
        table = EntityTableView(invoiceModel.tableModel()) {
            control(invoiceReportControl(invoiceModel.tableModel()))
        },
    )
    val invoiceLineView = EntityView(invoiceModel.detail().get(InvoiceLine.TYPE))
    customerView.detail().add(invoiceView)
    invoiceView.detail().add(invoiceLineView)

    // Load the three root tables; details refresh on master selection.
    albumModel.tableModel().items().refresh()
    playlistModel.tableModel().items().refresh()
    customerModel.tableModel().items().refresh()

    // A custom full-screen landing shown in place of the CRUD app (demoing Config.main): a card per root, tapped to
    // navigate in. The three roots don't relate, so treat them as independent (Config.independentRoots): no root tabs,
    // and system back from any root returns to ChinookHome — you pick the next root from home, not sideways.
    return EntityApplicationView(applicationModel, listOf(customerView, albumView, playlistView)) {
        main { application -> ChinookHome(application) }
        independentRoots = true
    }
}

// The front door
private val chinookApplication = EntityApplication.builder(::buildApplicationView)
    .domain(DOMAIN)
    .user(if (CLIENT_CONNECTION_TYPE.getOrThrow().equals("http", ignoreCase = true)) null else User.user("sa"))
    .credentialStorage(true)
    .build()

// ---- UI ----------------------------------------------------------------------------------------

// FragmentActivity (not ComponentActivity) so the credential vault's BiometricPrompt has a host (see credentialStorage).
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Connection config — read at connect time (in the ViewModel), so setting it here, before the first render,
        // is in time. To use the server instead, flip CLIENT_CONNECTION_TYPE to "http" + the HttpEntityConnection.*
        // properties (db-http on the classpath).
        if (USE_HTTP) {
            CLIENT_CONNECTION_TYPE.set("http")
            HttpEntityConnection.HOSTNAME.set(HTTP_HOSTNAME)
            HttpEntityConnection.SECURE.set(false)
            HttpEntityConnection.PORT.set(HTTP_PORT)
        } else {
            CLIENT_CONNECTION_TYPE.set(EntityConnection.CONNECTION_TYPE_LOCAL)
            // Persistent H2 file DB under the app's no-backup storage (kept out of Android auto-backup, so a reinstall /
            // new device re-seeds fresh instead of silently restoring a stale DB). H2 runs INIT_SCRIPTS only when the
            // .mv.db is absent, then reuses the file as-is — first launch seeds from the bundled chinook schema+data
            // (create_schema.sql), later launches just open it. (Swap to "jdbc:h2:mem:chinook" for a throwaway in-memory
            // DB, re-seeded every launch.)
            val databaseFile = File(noBackupFilesDir, "$DATABASE_NAME.mv.db")
            seededMarker = File(noBackupFilesDir, "$DATABASE_NAME.seeded")
            // Partial-seed guard: the .mv.db appears the instant H2 opens, before the ~12k-row script finishes, so a
            // process kill mid-first-seed leaves a half-populated file the absent-file check would accept as ready. The
            // marker is written only after a confirmed connect+seed; a DB present without it ⇒ discard it and re-seed.
            if (databaseFile.exists() && !seededMarker.exists()) {
                databaseFile.delete()
            }
            Database.URL.set("jdbc:h2:file:${File(noBackupFilesDir, DATABASE_NAME).absolutePath}")
            Database.LEGACY_JDBC.set(true) //android jdbc support
            Database.INIT_SCRIPTS.set("classpath:create_schema.sql")
        }
        // auto() lets the system pick status-bar icon colour to match the active light/dark theme (CodionTheme
        // follows the system setting), with transparent scrims.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))
        setContent {
            CodionTheme {
                Box(Modifier.fillMaxSize().systemBarsPadding()) {
                    chinookApplication.Content(Modifier.fillMaxSize())
                    // The report launcher overlay — dormant until a report table action (customer report, invoice) posts
                    // a request, then fills the PDF off the main thread and opens it in the device's PDF viewer.
                    ReportLauncher()
                }
            }
        }
    }
}
