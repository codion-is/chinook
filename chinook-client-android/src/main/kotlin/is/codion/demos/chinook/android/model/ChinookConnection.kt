package `is`.codion.demos.chinook.android.model

import android.content.Context
import `is`.codion.android.dbms.h2.DeviceDatabase
import `is`.codion.framework.db.EntityConnection.CLIENT_CONNECTION_TYPE
import `is`.codion.framework.db.http.HttpEntityConnection

/**
 * Which database this demo talks to, and how — the app's connection policy, in one place.
 *
 * [configure] is handed to `EntityApplication.Builder.configure`, so it runs with a Context in hand and before
 * the connection is built; [seeded] is called from [ChinookApplicationModel] once the connection stands. The
 * Activity is not involved in either.
 */
object ChinookConnection {

    /**
     * Flip this when testing: true = a server over HTTP, false = the on-device H2 database (offline).
     *
     * Read directly rather than through [CLIENT_CONNECTION_TYPE], because the application builder needs the
     * answer before [configure] has run and set that property.
     */
    const val HTTP = false

    // The public Render cloud server terminates TLS at the edge (443) and forwards to the server's HTTP 8088.
    // private const val HOSTNAME = "chinook-server-latest.onrender.com"
    // private const val PORT = 443
    // A local LAN dev server needs android:usesCleartextTraffic in the manifest (it's set) and the hostname
    // pointing at the dev machine's IP.
    private const val HOSTNAME = "192.168.1.18"
    private const val PORT = 8088

    // First launch seeds ~12k rows from the bundled chinook schema+data; later launches just open the file.
    private val database = DeviceDatabase("chinook", "classpath:create_schema.sql")

    /** Configures the connection this app will build. Run once, with a Context, before connecting. */
    fun configure(context: Context) {
        if (HTTP) {
            CLIENT_CONNECTION_TYPE.set("http")
            HttpEntityConnection.HOSTNAME.set(HOSTNAME)
            HttpEntityConnection.SECURE.set(false)
            HttpEntityConnection.PORT.set(PORT)
        } else {
            database.configure(context.noBackupFilesDir)
        }
    }

    /** Records a completed seed, once the connection stands. A no-op when connected to a server. */
    fun seeded() = database.seeded()
}
