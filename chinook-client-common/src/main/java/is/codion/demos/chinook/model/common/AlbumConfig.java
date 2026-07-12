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

import is.codion.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.model.EntityEditModel;
import is.codion.framework.model.EntityEditor;
import is.codion.framework.model.EntityModel;
import is.codion.framework.model.EntityTableModel;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

public interface AlbumConfig<M extends EntityModel<M, E, T, R>, E extends EntityEditModel<R>,
				T extends EntityTableModel<E, R>, R extends EntityEditor<R>> extends EntityModel<M, E, T, R> {

	default void configure() {
		R trackEditor = detail().get(Track.TYPE).editor();
		trackEditor.comboBoxModels().initialize(Track.MEDIATYPE_FK, Track.GENRE_FK);
		// We refresh albums when tracks are modified, to display the updated rating
		trackEditor.events().after().insert().addConsumer(this::tracksInsertedOrDeleted);
		trackEditor.events().after().delete().addConsumer(this::tracksInsertedOrDeleted);
		trackEditor.events().after().update().addConsumer(this::tracksUpdated);
	}

	private void tracksInsertedOrDeleted(Collection<Entity> tracks) {
		tableModel().refresh(Entity.keys(Track.ALBUM_FK, tracks));
	}

	private void tracksUpdated(Map<Entity, Entity> tracks) {
		tableModel().refresh(tracks.keySet().stream()
						// We only need to refresh albums for tracks which rating was modified
						.filter(track -> track.modified(Track.RATING))
						.map(track -> track.key(Track.ALBUM_FK))
						.collect(toSet()));
	}
}
