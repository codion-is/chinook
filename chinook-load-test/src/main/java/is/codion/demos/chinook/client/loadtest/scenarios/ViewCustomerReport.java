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
 * Copyright (c) 2004 - 2025, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.client.loadtest.scenarios;

import is.codion.demos.chinook.domain.api.Chinook.Customer;
import is.codion.framework.db.EntityConnection;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.tools.loadtest.LoadTest.Scenario.Performer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static is.codion.demos.chinook.client.loadtest.scenarios.LoadTestUtil.randomCustomerId;

public final class ViewCustomerReport implements Performer<EntityConnectionProvider> {

	@Override
	public void perform(EntityConnectionProvider connectionProvider) throws Exception {
		EntityConnection connection = connectionProvider.connection();
		Map<String, Object> reportParameters = new HashMap<>();
		reportParameters.put("CUSTOMER_IDS", List.of(randomCustomerId()));
		connection.report(Customer.REPORT, reportParameters);
	}
}
