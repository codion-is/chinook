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
 * along with Codion Chinook Demo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.domain;

import is.codion.framework.db.EntityConnection;
import is.codion.framework.demos.chinook.domain.api.Chinook.Playlist.RandomPlaylistParameters;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.attribute.Attribute;
import is.codion.framework.domain.entity.attribute.ForeignKey;
import is.codion.framework.domain.entity.test.DefaultEntityFactory;
import is.codion.framework.domain.entity.test.EntityTestUnit;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static is.codion.framework.demos.chinook.domain.api.Chinook.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChinookTest extends EntityTestUnit {

	private static final ChinookImpl DOMAIN = new ChinookImpl();

	public ChinookTest() {
		super(DOMAIN, new ChinookEntityFactory());
	}

	@Test
	void album() throws Exception {
		test(Album.TYPE);
	}

	@Test
	void artist() throws Exception {
		test(Artist.TYPE);
	}

	@Test
	void customer() throws Exception {
		test(Customer.TYPE);
	}

	@Test
	void employee() throws Exception {
		test(Employee.TYPE);
	}

	@Test
	void genre() throws Exception {
		test(Genre.TYPE);
	}

	@Test
	void invoce() throws Exception {
		test(Invoice.TYPE);
	}

	@Test
	void invoiceLine() throws Exception {
		test(InvoiceLine.TYPE);
	}

	@Test
	void mediaType() throws Exception {
		test(MediaType.TYPE);
	}

	@Test
	void playlist() throws Exception {
		test(Playlist.TYPE);
	}

	@Test
	void playlistTrack() throws Exception {
		test(PlaylistTrack.TYPE);
	}

	@Test
	void track() throws Exception {
		test(Track.TYPE);
	}

	@Test
	void randomPlaylist() throws Exception {
		EntityConnection connection = connection();
		connection.startTransaction();
		try {
			Entity genre = connection.selectSingle(Genre.NAME.equalTo("Metal"));
			int noOfTracks = 10;
			String playlistName = "MetalPlaylistTest";
			RandomPlaylistParameters parameters = new RandomPlaylistParameters(playlistName, noOfTracks, singleton(genre));
			Entity playlist = connection.execute(Playlist.RANDOM_PLAYLIST, parameters);
			assertEquals(playlistName, playlist.get(Playlist.NAME));
			List<Entity> playlistTracks = connection.select(PlaylistTrack.PLAYLIST_FK.equalTo(playlist));
			assertEquals(noOfTracks, playlistTracks.size());
			playlistTracks.stream()
							.map(playlistTrack -> playlistTrack.get(PlaylistTrack.TRACK_FK))
							.forEach(track -> assertEquals(genre, track.get(Track.GENRE_FK)));
		}
		finally {
			connection.rollbackTransaction();
		}
	}

	private static final class ChinookEntityFactory extends DefaultEntityFactory {

		private ChinookEntityFactory() {
			super(DOMAIN.entities());
		}

		@Override
		public void modify(Entity entity, Map<ForeignKey, Entity> foreignKeyEntities) {
			super.modify(entity, foreignKeyEntities);
			if (entity.entityType().equals(Album.TYPE)) {
				entity.put(Album.TAGS, asList("tag_one", "tag_two", "tag_three"));
			}
		}

		@Override
		protected <T> T createValue(Attribute<T> attribute, Map<ForeignKey, Entity> referenceEntities) {
			if (attribute.equals(Album.TAGS)) {
				return (T) asList("tag_one", "tag_two");
			}

			return super.createValue(attribute, referenceEntities);
		}
	}
}
