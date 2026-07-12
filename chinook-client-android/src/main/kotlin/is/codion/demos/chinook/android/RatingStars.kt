package `is`.codion.demos.chinook.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import `is`.codion.android.framework.ui.observeAsState
import `is`.codion.common.reactive.value.Value
import `is`.codion.framework.domain.entity.attribute.AttributeDefinition

/**
 * A custom edit component for `Track.RATING` (1–10), plugged into the framework's per-attribute component seam
 * (`EntityEditView.Config` / `EntityTableView.Config` `component(attribute) { … }`). It renders a row of tappable
 * stars bound to the supplied [value] — so the *same* component drives both the edit form (where the value is
 * `editor.value(RATING)`) and the bulk Edit… dialog (where it's a standalone value). That dual use is the whole
 * point of the Value-driven component design: the app writes one component, the framework reuses it everywhere.
 *
 * The framework knows nothing about stars or ratings — it just hands the component a [Value] and an
 * [AttributeDefinition]; the component reads the current value through the same `observeAsState` adapter the
 * built-in fields use, and writes back with `value.set(...)` (which routes through validation / modified-tracking).
 */
@Composable
fun RatingStars(value: Value<Any>, definition: AttributeDefinition<*>) {
    val current by value.observeAsState()
    val rating = (current as? Int) ?: 0
    Column {
        Text(definition.caption(), style = MaterialTheme.typography.bodySmall)
        Row {
            (1..10).forEach { star ->
                Text(
                    text = if (star <= rating) "★" else "☆",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .clickable { value.set(star) }
                        .padding(horizontal = 2.dp),
                )
            }
        }
    }
}

/**
 * The read-only table-cell counterpart to [RatingStars], registered via the framework's per-attribute renderer seam
 * (`EntityTableView.Config.renderer`). The framework hands it the cell value (`entity.get(attribute)`); it draws a
 * compact star meter rather than the default number. Each of the five stars spans two rating points, so a 1–10
 * rating shows at half-star resolution — the same granularity as the ten-star editor, but narrow enough for the
 * numeric column. Renders nothing when null, e.g. an album whose average rating is undefined (no rated tracks yet).
 * The same seam drives both the Album and Track tables.
 */
@Composable
fun RatingStarsCell(value: Any?, definition: AttributeDefinition<*>) {
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
