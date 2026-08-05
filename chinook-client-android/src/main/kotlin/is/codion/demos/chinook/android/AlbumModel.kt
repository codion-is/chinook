package `is`.codion.demos.chinook.android

import `is`.codion.android.framework.model.AndroidEntityEditModel
import `is`.codion.android.framework.model.AndroidEntityEditor
import `is`.codion.android.framework.model.AndroidEntityModel
import `is`.codion.android.framework.model.AndroidEntityTableModel
import `is`.codion.demos.chinook.domain.api.Chinook.Album
import `is`.codion.demos.chinook.domain.api.Chinook.Track
import `is`.codion.demos.chinook.model.common.AlbumConfig
import `is`.codion.framework.db.EntityConnection

class AlbumModel(connection: EntityConnection) :
    AndroidEntityModel(Album.TYPE, connection),
    AlbumConfig<AndroidEntityModel, AndroidEntityEditModel, AndroidEntityTableModel, AndroidEntityEditor> {

    init {
        detail().add(AndroidEntityModel(Track.TYPE, connection))
        configure()
    }
}