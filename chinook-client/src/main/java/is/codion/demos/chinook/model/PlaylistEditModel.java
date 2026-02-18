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
 * Copyright (c) 2024 - 2026, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.model;

import is.codion.demos.chinook.domain.api.Chinook.Playlist;
import is.codion.demos.chinook.domain.api.Chinook.PlaylistTrack;
import is.codion.framework.db.EntityConnection;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.model.EntityPersistence;
import is.codion.swing.framework.model.SwingEntityEditModel;

import java.util.Collection;

import static is.codion.framework.db.EntityConnection.transaction;
import static is.codion.framework.domain.entity.Entity.primaryKeys;

public final class PlaylistEditModel extends SwingEntityEditModel {

	public PlaylistEditModel(EntityConnectionProvider connectionProvider) {
		super(Playlist.TYPE, connectionProvider);
		persistence().set(new PlaylistPersistence());
	}

	private static final class PlaylistPersistence implements EntityPersistence {

		@Override
		public void delete(Collection<Entity> playlists, EntityConnection connection) {
			// We delete all playlist tracks along
			// with the playlist, within a transaction
			transaction(connection, () -> {
				connection.delete(PlaylistTrack.PLAYLIST_FK.in(playlists));
				connection.delete(primaryKeys(playlists));
			});
		}
	}
}
