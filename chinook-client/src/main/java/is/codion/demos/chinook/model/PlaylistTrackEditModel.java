/*
 * This file is part of Codion Chinook Demo.
 *
 * Codion Chinook Demo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codion Chinook Demo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codion Chinook Demo.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2024, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.model;

import is.codion.demos.chinook.domain.api.Chinook.Playlist;
import is.codion.demos.chinook.domain.api.Chinook.PlaylistTrack;
import is.codion.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.condition.Condition;
import is.codion.swing.framework.model.SwingEntityEditModel;

import java.util.List;

public final class PlaylistTrackEditModel extends SwingEntityEditModel {

	public PlaylistTrackEditModel(EntityConnectionProvider connectionProvider) {
		super(PlaylistTrack.TYPE, connectionProvider);
		value(PlaylistTrack.TRACK_FK).persist().set(false);
		// Set the search model condition, so the search results
		// won't contain tracks already in the currently selected playlist
		value(PlaylistTrack.PLAYLIST_FK).addConsumer(this::excludePlaylistTracks);
	}

	private void excludePlaylistTracks(Entity playlist) {
		foreignKeySearchModel(PlaylistTrack.TRACK_FK).condition().set(() -> playlist == null ? null :
						Condition.custom(Track.NOT_IN_PLAYLIST,
										List.of(Playlist.ID),
										List.of(playlist.get(Playlist.ID))));
	}
}
