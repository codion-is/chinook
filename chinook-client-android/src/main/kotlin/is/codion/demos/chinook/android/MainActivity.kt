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
import `is`.codion.android.common.ui.CodionTheme
import `is`.codion.android.framework.ui.EntityApplication
import `is`.codion.common.utilities.user.User
import `is`.codion.demos.chinook.android.model.ChinookConnection
import `is`.codion.demos.chinook.android.ui.ChinookApplicationView
import `is`.codion.demos.chinook.android.ui.ReportLauncher
import `is`.codion.demos.chinook.domain.api.Chinook.DOMAIN

// The application is ChinookApplicationView / ChinookApplicationModel, mirroring the Swing ChinookAppPanel /
// ChinookAppModel; which database it talks to is ChinookConnection. What is left here is the Activity's own
// business: edge-to-edge, the theme, and setContent.

// The front door. ChinookConnection::configure runs when Content first composes - a Context in hand, before the
// connection is built - so nothing about the database needs an Activity. Holds no live state and captures no
// Context, so it is safe as a top-level value.
private val chinookApplication = EntityApplication.builder(::ChinookApplicationView)
    .domain(DOMAIN)
    .configure(ChinookConnection::configure)
    // Not read back from CLIENT_CONNECTION_TYPE: configure() is what sets that, and it has not run yet.
    .user(if (ChinookConnection.HTTP) null else User.user("sa"))
    .credentialStorage(true)
    .build()

// FragmentActivity (not ComponentActivity) so the credential vault's BiometricPrompt has a host (see credentialStorage).
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
