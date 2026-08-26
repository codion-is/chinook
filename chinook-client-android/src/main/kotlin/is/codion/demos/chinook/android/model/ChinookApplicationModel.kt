package `is`.codion.demos.chinook.android.model

import `is`.codion.android.framework.model.AndroidEntityApplicationModel
import `is`.codion.android.framework.model.AndroidEntityModel
import `is`.codion.demos.chinook.domain.api.Chinook.PlaylistTrack
import `is`.codion.framework.db.EntityConnection

/**
 * The application model — the Android mirror of the Swing `ChinookAppModel`: three roots, each with its own
 * detail tree, and a refresh of every root table once the tree stands.
 *
 *   Album → Track,  Playlist → PlaylistTrack,  Customer → Invoice → InvoiceLine
 *
 * The roots are constructor properties so [ChinookApplicationView] can reach them by name and type rather than
 * looking them up by entity type. They have to exist before `super(...)` runs, hence the private constructor
 * taking them and the secondary one that builds them.
 */
class ChinookApplicationModel private constructor(
    connection: EntityConnection,
    val albumModel: AlbumModel,
    val playlistModel: AndroidEntityModel,
    val customerModel: AndroidEntityModel,
) : AndroidEntityApplicationModel(connection, listOf(albumModel, playlistModel, customerModel)) {

    constructor(connection: EntityConnection) : this(
        connection,
        AlbumModel(connection),
        playlistModel(connection),
        customerModel(connection),
    )

    init {
        // The connection stands, so the local database - if that is what we connected to - is seeded. Recording
        // it here rather than at connect means a kill in between costs a needless re-seed, which is the safe way
        // for that to be wrong.
        ChinookConnection.seeded()
        // The three root tables; details refresh on master selection. As ChinookAppModel does in Swing.
        models().get().forEach { it.tableModel().items().refresh() }
    }

    private companion object {

        private fun playlistModel(connection: EntityConnection) =
            AndroidEntityModel(PlaylistEditModel(connection)).apply {
                // PlaylistTrack references both Playlist and Track, so pin the foreign key (mirrors PlaylistModel).
                detail().add(AndroidEntityModel(PlaylistTrackEditModel(connection)), PlaylistTrack.PLAYLIST_FK)
            }

        private fun customerModel(connection: EntityConnection) =
            AndroidEntityModel(CustomerEditModel(connection)).apply {
                detail().add(InvoiceModel(connection)) // Invoice.CUSTOMER_FK auto-detected
            }
    }
}
