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
 * Copyright (c) 2023 - 2026, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.db.EntityConnection;
import is.codion.tools.loadtest.Scenario.Performer;

import static is.codion.demos.chinook.client.loadtest.scenarios.LoadTestUtil.RANDOM;

public final class LogoutLogin implements Performer<EntityConnection> {

	@Override
	public void perform(EntityConnection connection) {
		try {
			connection.close();
			Thread.sleep(RANDOM.nextInt(1500));
			//an operation re-establishes the connection, which is what this scenario measures
			connection.user();
		}
		catch (InterruptedException ignored) {/*ignored*/}
	}
}
