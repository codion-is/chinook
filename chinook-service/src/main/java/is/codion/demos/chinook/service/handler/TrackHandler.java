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
package is.codion.demos.chinook.service.handler;

import is.codion.demos.chinook.domain.api.Chinook.Album;
import is.codion.demos.chinook.domain.api.Chinook.Artist;
import is.codion.demos.chinook.domain.api.Chinook.Track;
import is.codion.demos.chinook.service.connection.ConnectionSupplier;
import is.codion.framework.domain.entity.Entity;

import io.javalin.http.Context;
import org.eclipse.jetty.http.HttpStatus;

import static io.javalin.http.HttpStatus.OK;
import static is.codion.framework.db.EntityConnection.Select.all;
import static java.lang.Long.parseLong;
import static java.util.stream.StreamSupport.stream;

public final class TrackHandler extends AbstractHandler {

	public TrackHandler(ConnectionSupplier connection) {
		super(connection);
	}

	public void tracks(Context context) {
		try (var connection = connection();
				 var iterator = connection.iterator(all(Track.TYPE).build())) {
			context.status(HttpStatus.OK_200)
							.writeJsonStream(stream(iterator.spliterator(), false)
											.map(Track::dto));
		}
		catch (Exception exception) {
			handleException(context, exception);
		}
	}

	public void byName(Context context) {
		try (var connection = connection()) {
			context.status(HttpStatus.OK_200)
							.result(mapper().writeValueAsString(connection.select(
															Track.NAME.equalToIgnoreCase(context.pathParam("name")))
											.stream()
											.map(Track::dto)
											.toList()));
		}
		catch (Exception exception) {
			handleException(context, exception);
		}
	}

	public void byArtistName(Context context) {
		try (var connection = connection()) {
			var artistIds = connection.select(Artist.ID,
							Artist.NAME.equalToIgnoreCase(context.pathParam("name")));
			var albumIds = connection.select(Album.ID,
							Album.ARTIST_ID.in(artistIds));
			context.status(HttpStatus.OK_200)
							.result(mapper().writeValueAsString(
											connection.select(Track.ALBUM_ID.in(albumIds))
															.stream()
															.map(Track::dto)
															.toList()));
		}
		catch (Exception exception) {
			handleException(context, exception);
		}
	}

	public void byId(Context context) {
		try (var connection = connection()) {
			context.status(HttpStatus.OK_200)
							.result(mapper().writeValueAsString(
											Track.dto(connection.selectSingle(
															Track.ID.equalTo(parseLong(context.pathParam("id")))))));
		}
		catch (Exception exception) {
			handleException(context, exception);
		}
	}

	public void insert(Context context) {
		try (var connection = connection()) {
			Track.Dto trackDto = context.bodyStreamAsClass(Track.Dto.class);
			Entity track = connection.insertSelect(trackDto.entity(entities()));
			context.status(OK)
							.result(mapper().writeValueAsString(Track.dto(track)));
		}
		catch (Exception e) {
			handleException(context, e);
		}
	}
}
