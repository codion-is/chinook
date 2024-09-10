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
 * Copyright (c) 2023 - 2024, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.common.model.loadtest.LoadTest.Scenario.Performer;
import is.codion.framework.db.EntityConnectionProvider;

import static is.codion.framework.demos.chinook.client.loadtest.scenarios.LoadTestUtil.RANDOM;

public final class LogoutLogin implements Performer<EntityConnectionProvider> {

	@Override
	public void perform(EntityConnectionProvider connectionProvider) {
		try {
			connectionProvider.close();
			Thread.sleep(RANDOM.nextInt(1500));
			connectionProvider.connection();
		}
		catch (InterruptedException ignored) {/*ignored*/}
	}
}
