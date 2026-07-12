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
 * Copyright (c) 2026, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.model.common;

import is.codion.demos.chinook.domain.api.Chinook.Playlist;
import is.codion.demos.chinook.domain.api.Chinook.PlaylistTrack;
import is.codion.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.condition.Condition;
import is.codion.framework.model.EntityEditModel;
import is.codion.framework.model.EntityEditor;
import is.codion.framework.model.EntityEditor.EditorValue;

import static is.codion.framework.domain.entity.condition.Condition.all;

public interface PlaylistTrackEditConfig<R extends EntityEditor<R>> extends EntityEditModel<R> {

	default void configure() {
		// So that the track editor value is cleared after a track is added
		editor().value(PlaylistTrack.TRACK_FK).persist().set(false);
		// Set the search model condition, so the search results
		// won't contain tracks already in the selected playlist
		editor().searchModels().get(PlaylistTrack.TRACK_FK).condition().set(this::trackCondition);
	}

	private Condition trackCondition() {
		EditorValue<Entity> playlist = editor().value(PlaylistTrack.PLAYLIST_FK);
		if (playlist.isNull()) {
			return all(Track.TYPE);
		}

		// Use a custom subquery based condition, see domain model implementation
		return Track.NOT_IN_PLAYLIST.get(Playlist.ID, playlist.getOrThrow().get(Playlist.ID));
	}
}
