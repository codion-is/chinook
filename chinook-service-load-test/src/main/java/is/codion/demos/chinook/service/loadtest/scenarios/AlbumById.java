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
 * Copyright (c) 2024 - 2025, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.service.loadtest.scenarios;

import is.codion.tools.loadtest.LoadTest.Scenario.Performer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Random;

import static java.net.http.HttpResponse.BodyHandlers.ofString;

public final class AlbumById implements Performer<HttpClient> {

	private static final Random RANDOM = new Random();

	private final String baseUrl;

	public AlbumById(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public void perform(HttpClient client) throws Exception {
		if (client.send(HttpRequest.newBuilder()
						.uri(URI.create(baseUrl + "/albums/id/" + randomAlbumId()))
						.build(), ofString()).statusCode() != 200) {
			throw new Exception(toString());
		}
	}

	@Override
	public String toString() {
		return AlbumById.class.getSimpleName();
	}

	private static int randomAlbumId() {
		return RANDOM.nextInt(347) + 1;
	}
}
