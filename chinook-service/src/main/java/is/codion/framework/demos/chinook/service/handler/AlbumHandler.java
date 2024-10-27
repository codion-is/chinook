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
 * Copyright (c) 2024, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.service.handler;

import is.codion.framework.demos.chinook.domain.api.Chinook.Album;
import is.codion.framework.demos.chinook.domain.api.Chinook.Artist;
import is.codion.framework.demos.chinook.service.connection.ConnectionSupplier;
import is.codion.framework.domain.entity.Entity;

import io.javalin.http.Context;
import org.eclipse.jetty.http.HttpStatus;

import static io.javalin.http.HttpStatus.OK;
import static is.codion.framework.db.EntityConnection.Select.all;
import static java.lang.Long.parseLong;
import static java.util.stream.StreamSupport.stream;

public final class AlbumHandler extends AbstractHandler {

	public AlbumHandler(ConnectionSupplier connection) {
		super(connection);
	}

	public void albums(Context context) {
		try (var connection = connection();
				 var iterator = connection.iterator(all(Album.TYPE).build())) {
			context.status(HttpStatus.OK_200)
							.writeJsonStream(stream(iterator.spliterator(), false)
											.map(Album::dto));
		}
		catch (Exception exception) {
			handleException(context, exception);
		}
	}

	public void byTitle(Context context) {
		try (var connection = connection()) {
			context.status(HttpStatus.OK_200)
							.result(mapper().writeValueAsString(connection.select(
															Album.TITLE.equalToIgnoreCase(context.pathParam("title")))
											.stream()
											.map(Album::dto)
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
			context.status(HttpStatus.OK_200)
							.result(mapper().writeValueAsString(
											connection.select(Album.ARTIST_ID.in(artistIds))
															.stream()
															.map(Album::dto)
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
											Album.dto(connection.selectSingle(
															Album.ID.equalTo(parseLong(context.pathParam("id")))))));
		}
		catch (Exception exception) {
			handleException(context, exception);
		}
	}

	public void insert(Context context) {
		try (var connection = connection()) {
			Album.Dto albumDto = context.bodyStreamAsClass(Album.Dto.class);
			Entity album = connection.insertSelect(albumDto.entity(entities()));
			context.status(OK)
							.result(mapper().writeValueAsString(Album.dto(album)));
		}
		catch (Exception e) {
			handleException(context, e);
		}
	}
}
