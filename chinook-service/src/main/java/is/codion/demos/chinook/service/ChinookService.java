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
package is.codion.demos.chinook.service;

import is.codion.common.utilities.property.PropertyValue;
import is.codion.demos.chinook.service.connection.ConnectionSupplier;
import is.codion.demos.chinook.service.handler.AlbumHandler;
import is.codion.demos.chinook.service.handler.ArtistHandler;
import is.codion.demos.chinook.service.handler.GenreHandler;
import is.codion.demos.chinook.service.handler.MediaTypeHandler;
import is.codion.demos.chinook.service.handler.TrackHandler;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

import static is.codion.common.utilities.Configuration.integerValue;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

final class ChinookService {

	private static final Logger LOG = LoggerFactory.getLogger(ChinookService.class);

	static final PropertyValue<Integer> PORT =
					integerValue("chinook.service.port", 8089);

	private final Javalin javalin;
	private final ConnectionSupplier connectionSupplier = new ConnectionSupplier();

	private final ArtistHandler artists = new ArtistHandler(connectionSupplier);
	private final AlbumHandler albums = new AlbumHandler(connectionSupplier);
	private final TrackHandler tracks = new TrackHandler(connectionSupplier);
	private final MediaTypeHandler mediaType = new MediaTypeHandler(connectionSupplier);
	private final GenreHandler genre = new GenreHandler(connectionSupplier);

	ChinookService() {
		javalin = Javalin.create(config -> {
			config.concurrency.useVirtualThreads = true;
			config.routes.get("/artists", artists::artists);
			config.routes.get("/artists/id/{id}", artists::byId);
			config.routes.get("/artists/name/{name}", artists::byName);
			config.routes.post("/artists", artists::insert);
			config.routes.get("/albums", albums::albums);
			config.routes.get("/albums/id/{id}", albums::byId);
			config.routes.get("/albums/title/{title}", albums::byTitle);
			config.routes.get("/albums/artist/name/{name}", albums::byArtistName);
			config.routes.post("/albums", albums::insert);
			config.routes.get("/tracks", tracks::tracks);
			config.routes.get("/tracks/id/{id}", tracks::byId);
			config.routes.get("/tracks/name/{name}", tracks::byName);
			config.routes.get("/tracks/artist/name/{name}", tracks::byArtistName);
			config.routes.post("/tracks", tracks::insert);
			config.routes.post("/mediatypes", mediaType::insert);
			config.routes.post("/genres", genre::insert);
		});
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
