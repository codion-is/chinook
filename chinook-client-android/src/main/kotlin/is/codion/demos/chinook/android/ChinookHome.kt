package `is`.codion.demos.chinook.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `is`.codion.android.framework.model.AndroidEntityApplicationModel
import `is`.codion.android.framework.ui.CodionLogo
import `is`.codion.android.framework.ui.EntityApplicationView
import `is`.codion.android.framework.ui.EntityView
import `is`.codion.android.framework.ui.observeAsState

// A per-card accent — one colour per root, cycled. Saturated 600–800 tones so white avatar text reads on both the
// light and dark CodionTheme. Only the avatar is coloured; text stays theme-driven, so nothing fights the surface.
private val CARD_ACCENTS = listOf(
    Color(0xFF00897B), // teal
    Color(0xFF5E35B1), // deep purple
    Color(0xFFEF6C00), // orange
)

/**
 * A custom full-screen landing shown in place of the CRUD app (wired via [EntityApplicationView.Config.main]). It owns
 * its own way into the data: one card per top-level [EntityView][EntityApplicationView.entityViews], tapped to
 * [activate][EntityApplicationView.activate] that root. Each card shows a live record count read straight off the
 * already-loaded table model — the main view reaching the live application models, no extra query. System back from a
 * root returns here.
 */
@Composable
fun ChinookHome(application: EntityApplicationView<AndroidEntityApplicationModel>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))
        Header(CodionLogo)
        Spacer(Modifier.height(40.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            application.entityViews.forEachIndexed { index, view ->
                RootCard(
                    view = view,
                    accent = CARD_ACCENTS[index % CARD_ACCENTS.size],
                    onOpen = { application.activate(view) },
                )
            }
        }
    }
}

@Composable
private fun Header(logo: ImageVector) {
    Icon(
        imageVector = logo,
        contentDescription = null,
        // Preserve the logo's aspect ratio (it is taller than wide) rather than forcing a square, matching the framework's
        // own Logo() rendering; tinted to the theme primary like the login/lock screens.
        modifier = Modifier.height(72.dp).aspectRatio(logo.viewportWidth / logo.viewportHeight),
        tint = MaterialTheme.colorScheme.primary,
    )
    Spacer(Modifier.height(20.dp))
    Text("Chinook", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
    Text(
        text = "Browse the music store",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** One root as a tappable card: a coloured initial-avatar, the entity caption, and its live record count. */
@Composable
private fun RootCard(view: EntityView, accent: Color, onOpen: () -> Unit) {
    val caption = view.model.editModel().entityDefinition().caption()
    // The loaded (post-filter) rows, observed — populates as the startup refresh completes, so the count is live.
    val rows by view.model.tableModel().items().included().observeAsState()
    ElevatedCard(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(accent),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = caption.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(caption, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Tap to browse",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (rows?.size ?: 0).toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "records",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
