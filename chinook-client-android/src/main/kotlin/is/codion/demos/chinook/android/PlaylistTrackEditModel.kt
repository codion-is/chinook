package `is`.codion.demos.chinook.android

import `is`.codion.android.framework.model.AndroidEntityEditModel
import `is`.codion.android.framework.model.AndroidEntityEditor
import `is`.codion.demos.chinook.domain.api.Chinook.PlaylistTrack
import `is`.codion.demos.chinook.model.common.PlaylistTrackEditConfig
import `is`.codion.framework.db.EntityConnectionProvider

class PlaylistTrackEditModel(connectionProvider: EntityConnectionProvider) :
    AndroidEntityEditModel(PlaylistTrack.TYPE, connectionProvider),
    PlaylistTrackEditConfig<AndroidEntityEditor> {

    init {
        configure()
    }
}