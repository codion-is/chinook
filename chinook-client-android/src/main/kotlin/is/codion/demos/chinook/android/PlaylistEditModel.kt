package `is`.codion.demos.chinook.android

import `is`.codion.android.framework.model.AndroidEntityEditModel
import `is`.codion.android.framework.model.AndroidEntityEditor
import `is`.codion.demos.chinook.domain.api.Chinook.Playlist
import `is`.codion.demos.chinook.model.common.PlaylistEditConfig
import `is`.codion.framework.db.EntityConnectionProvider

class PlaylistEditModel(connectionProvider: EntityConnectionProvider) :
    AndroidEntityEditModel(Playlist.TYPE, connectionProvider),
    PlaylistEditConfig<AndroidEntityEditor> {

    init {
        configure()
    }
}