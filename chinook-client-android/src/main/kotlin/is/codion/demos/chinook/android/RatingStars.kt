package `is`.codion.demos.chinook.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect

/**
 * A read-only table-cell renderer for track/album ratins, registered via the framework's per-attribute renderer seam
 * (`EntityTableView.Config.renderer`). The framework hands it the cell value (`entity.get(attribute)`); it draws a
 * compact star meter rather than the default number. Each of the five stars spans two rating points, so a 1–10
 * rating shows at half-star resolution — the same granularity as the ten-star editor, but narrow enough for the
 * numeric column. Renders nothing when null, e.g. an album whose average rating is undefined (no rated tracks yet).
 * The same seam drives both the Album and Track tables.
 */
@Composable
fun RatingStarsCell(value: Any?) {
    val rating = value as? Int ?: return
    Row {
        (1..5).forEach { position ->
            // How much of this star is filled: full for points already passed, half for the odd point, else empty.
            val fill = (rating - (position - 1) * 2).coerceIn(0, 2) / 2f // 0f, 0.5f or 1f
            HalfStar(fill)
        }
    }
}

/** A star filled [fraction] of the way across (0f, 0.5f or 1f): a filled glyph clipped over an outline one. */
@Composable
private fun HalfStar(fraction: Float) {
    Box {
        Text("☆", style = MaterialTheme.typography.bodyMedium)
        if (fraction > 0f) {
            Text(
                text = "★",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.drawWithContent {
                    clipRect(right = size.width * fraction) { this@drawWithContent.drawContent() }
                },
            )
        }
    }
}
