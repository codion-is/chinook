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
package is.codion.demos.chinook.service.loadtest;

import is.codion.common.property.PropertyValue;
import is.codion.demos.chinook.service.loadtest.scenarios.AlbumById;
import is.codion.demos.chinook.service.loadtest.scenarios.Albums;
import is.codion.demos.chinook.service.loadtest.scenarios.ArtistById;
import is.codion.demos.chinook.service.loadtest.scenarios.Artists;
import is.codion.demos.chinook.service.loadtest.scenarios.NewArtist;
import is.codion.demos.chinook.service.loadtest.scenarios.TrackById;
import is.codion.demos.chinook.service.loadtest.scenarios.Tracks;
import is.codion.tools.loadtest.LoadTest;
import is.codion.tools.loadtest.Scenario;
import is.codion.tools.loadtest.model.LoadTestModel;

import java.net.http.HttpClient;
import java.util.List;

import static is.codion.common.Configuration.integerValue;
import static is.codion.common.user.User.user;
import static is.codion.tools.loadtest.Scenario.scenario;
import static is.codion.tools.loadtest.ui.LoadTestPanel.loadTestPanel;
import static java.net.http.HttpClient.newHttpClient;

public final class ChinookServiceLoadTest {

	private static final PropertyValue<Integer> PORT =
					integerValue("chinook.service.port", 8089);

	private static final String BASE_URL = "http://localhost:" + PORT.get();

	private static final List<Scenario<HttpClient>> SCENARIOS = List.of(
					scenario(new Artists(BASE_URL)),
					scenario(new ArtistById(BASE_URL)),
					scenario(new Albums(BASE_URL)),
					scenario(new AlbumById(BASE_URL)),
					scenario(new Tracks(BASE_URL)),
					scenario(new TrackById(BASE_URL)),
					scenario(new NewArtist(BASE_URL))
	);

	public static void main(String[] args) {
		LoadTest<HttpClient> loadTest =
						LoadTest.builder()
										.createApplication(_ -> newHttpClient())
										.closeApplication(HttpClient::close)
										.scenarios(SCENARIOS)
										.user(user("n/a"))
										.build();
		loadTestPanel(LoadTestModel.loadTestModel(loadTest)).run();
	}
}
