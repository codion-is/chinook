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
package is.codion.framework.demos.chinook.service;

import is.codion.common.db.exception.DatabaseException;
import is.codion.common.property.PropertyValue;
import is.codion.framework.demos.chinook.service.connection.ConnectionSupplier;
import is.codion.framework.demos.chinook.service.handler.AlbumHandler;
import is.codion.framework.demos.chinook.service.handler.ArtistHandler;
import is.codion.framework.demos.chinook.service.handler.GenreHandler;
import is.codion.framework.demos.chinook.service.handler.MediaTypeHandler;
import is.codion.framework.demos.chinook.service.handler.TrackHandler;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

import static is.codion.common.Configuration.integerValue;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

final class ChinookService {

	private static final Logger LOG = LoggerFactory.getLogger(ChinookService.class);

	static final PropertyValue<Integer> PORT =
					integerValue("chinook.service.port", 8089);

	private final Javalin javalin = Javalin.create();
	private final ConnectionSupplier connectionSupplier = new ConnectionSupplier();

	private final ArtistHandler artists = new ArtistHandler(connectionSupplier);
	private final AlbumHandler albums = new AlbumHandler(connectionSupplier);
	private final TrackHandler tracks = new TrackHandler(connectionSupplier);
	private final MediaTypeHandler mediaType = new MediaTypeHandler(connectionSupplier);
	private final GenreHandler genre = new GenreHandler(connectionSupplier);

	ChinookService() throws DatabaseException {
		javalin.get("/artists", artists::artists);
		javalin.get("/artists/id/{id}", artists::byId);
		javalin.get("/artists/name/{name}", artists::byName);
		javalin.post("/artists", artists::post);
		javalin.get("/albums", albums::albums);
		javalin.get("/albums/id/{id}", albums::byId);
		javalin.get("/albums/title/{title}", albums::byTitle);
		javalin.get("/albums/artist/name/{name}", albums::byArtistName);
		javalin.post("/albums", albums::post);
		javalin.get("/tracks", tracks::tracks);
		javalin.get("/tracks/id/{id}", tracks::byId);
		javalin.get("/tracks/name/{name}", tracks::byName);
		javalin.get("/tracks/artist/name/{name}", tracks::byArtistName);
		javalin.post("/tracks", tracks::post);
		javalin.post("/mediatypes", mediaType::post);
		javalin.post("/genres", genre::post);
	}

	void start() {
		LOG.info("Chinook service starting on port: {}", PORT.get());
		try {
			javalin.start(PORT.get());
		}
		catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}
	}

	void stop() {
		javalin.stop();
	}

	public static void main(String[] args) throws Exception {
		try (ExecutorService service = newSingleThreadExecutor()) {
			service.submit(new ChinookService()::start).get();
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
