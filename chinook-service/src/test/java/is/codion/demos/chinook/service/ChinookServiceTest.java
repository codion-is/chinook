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
package is.codion.demos.chinook.service;

import is.codion.demos.chinook.domain.api.Chinook.Album;
import is.codion.demos.chinook.domain.api.Chinook.Artist;
import is.codion.demos.chinook.domain.api.Chinook.Genre;
import is.codion.demos.chinook.domain.api.Chinook.MediaType;
import is.codion.demos.chinook.domain.api.Chinook.Track;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;

import static io.javalin.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static io.javalin.http.HttpStatus.OK;
import static is.codion.demos.chinook.service.ChinookService.PORT;
import static java.net.URLEncoder.encode;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChinookServiceTest {

	private static final ExecutorService EXECUTOR = newSingleThreadExecutor();
	private static final String BASE_URL = "http://localhost:" + PORT.get();
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static ChinookService SERVICE;

	@BeforeAll
	static void setUp() {
		SERVICE = new ChinookService();
		EXECUTOR.submit(SERVICE::start);
	}

	@AfterAll
	static void tearDown() {
		SERVICE.stop();
		EXECUTOR.shutdownNow();
	}

	@Test
	public void get() throws Exception {
		try (HttpClient client = newHttpClient()) {
			assertGet("/artists/id/42", OK, client);
			assertGet("/artists/id/-42", INTERNAL_SERVER_ERROR, client);
			assertGet("/artists", OK, client);
			assertGet("/artists/name/metallica", OK, client);
			assertGet("/albums/id/42", OK, client);
			assertGet("/albums/id/-42", INTERNAL_SERVER_ERROR, client);
			assertGet("/albums", OK, client);
			assertGet("/albums/title/" + encode("master of puppets"), OK, client);
			assertGet("/albums/artist/name/metallica", OK, client);
			assertGet("/tracks/id/42", OK, client);
			assertGet("/tracks/id/-42", INTERNAL_SERVER_ERROR, client);
			assertGet("/tracks", OK, client);
			assertGet("/tracks/name/orion", OK, client);
			assertGet("/tracks/artist/name/metallica", OK, client);
		}
	}

	@Test
	public void post() throws Exception {
		try (HttpClient client = newHttpClient()) {
			String payload = OBJECT_MAPPER.writeValueAsString(new MediaType.Dto(null, "New mediatype"));
			MediaType.Dto mediaType = OBJECT_MAPPER.readerFor(MediaType.Dto.class)
							.readValue(assertPost("/mediatypes", OK, client, payload));
			assertEquals("New mediatype", mediaType.name());

			payload = OBJECT_MAPPER.writeValueAsString(new Genre.Dto(null, "New genre"));
			Genre.Dto genre = OBJECT_MAPPER.readerFor(Genre.Dto.class)
							.readValue(assertPost("/genres", OK, client, payload));
			assertEquals("New genre", genre.name());

			payload = OBJECT_MAPPER.writeValueAsString(new Artist.Dto(null, "New artist"));
			Artist.Dto artist = OBJECT_MAPPER.readerFor(Artist.Dto.class)
							.readValue(assertPost("/artists", OK, client, payload));
			assertEquals("New artist", artist.name());

			payload = OBJECT_MAPPER.writeValueAsString(new Album.Dto(null, "New album", artist));
			Album.Dto album  = OBJECT_MAPPER.readerFor(Album.Dto.class)
							.readValue(assertPost("/albums", OK, client, payload));
			assertEquals("New album", album.title());

			payload = OBJECT_MAPPER.writeValueAsString(new Track.Dto(null, "New track", album, genre,
							mediaType, 10_000_000, 7, BigDecimal.ONE));
			Track.Dto track = OBJECT_MAPPER.readerFor(Track.Dto.class)
							.readValue(assertPost("/tracks", OK, client, payload));
			assertEquals("New track", track.name());
			assertEquals(album, track.album());
			assertEquals(genre, track.genre());
			assertEquals(mediaType, track.mediaType());
			assertEquals(10_000_000, track.milliseconds());
			assertEquals(7, track.rating());
			assertEquals(BigDecimal.ONE, track.unitPrice());
		}
	}

	private void assertGet(String url, HttpStatus status, HttpClient client) throws Exception {
		assertEquals(status.getCode(),
						client.send(HttpRequest.newBuilder()
														.uri(URI.create(BASE_URL + url))
														.GET()
														.build(), ofString())
										.statusCode());
	}

	private String assertPost(String url, HttpStatus status, HttpClient client,
													String payload) throws Exception {
		HttpResponse<String> response = client.send(HttpRequest.newBuilder()
						.uri(URI.create(BASE_URL + url))
						.POST(ofString(payload))
						.build(), ofString());
		assertEquals(status.getCode(), response.statusCode());

		return response.body();
	}
}
