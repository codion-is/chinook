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
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.client.loadtest;

import is.codion.common.user.User;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.demos.chinook.client.loadtest.scenarios.InsertDeleteAlbum;
import is.codion.framework.demos.chinook.client.loadtest.scenarios.LogoutLogin;
import is.codion.framework.demos.chinook.client.loadtest.scenarios.RaisePrices;
import is.codion.framework.demos.chinook.client.loadtest.scenarios.RandomPlaylist;
import is.codion.framework.demos.chinook.client.loadtest.scenarios.UpdateTotals;
import is.codion.framework.demos.chinook.client.loadtest.scenarios.ViewAlbum;
import is.codion.framework.demos.chinook.client.loadtest.scenarios.ViewCustomerReport;
import is.codion.framework.demos.chinook.client.loadtest.scenarios.ViewGenre;
import is.codion.framework.demos.chinook.client.loadtest.scenarios.ViewInvoice;
import is.codion.framework.demos.chinook.domain.api.Chinook;
import is.codion.framework.demos.chinook.model.ChinookAppModel;
import is.codion.framework.demos.chinook.ui.ChinookAppPanel;
import is.codion.swing.common.model.tools.loadtest.LoadTestModel;
import is.codion.swing.common.ui.tools.loadtest.LoadTestPanel;

import java.util.function.Function;

import static java.util.Arrays.asList;

public final class ChinookLoadTest {

  private static final User UNIT_TEST_USER =
          User.parse(System.getProperty("codion.test.user", "scott:tiger"));

  public static void main(String[] args) {
    LoadTestModel<EntityConnectionProvider> testModel =
            LoadTestModel.builder(new ConnectionProviderFactory(), EntityConnectionProvider::close)
                    .usageScenarios(asList(
                            new ViewGenre(), new ViewCustomerReport(), new ViewInvoice(),
                            new ViewAlbum(), new UpdateTotals(), new InsertDeleteAlbum(),
                            new LogoutLogin(), new RaisePrices(), new RandomPlaylist()))
            .user(UNIT_TEST_USER)
            .build();
    new LoadTestPanel<>(testModel).run();
  }

  private static final class ConnectionProviderFactory implements Function<User, EntityConnectionProvider> {
    @Override
    public EntityConnectionProvider apply(User user) {
      EntityConnectionProvider connectionProvider = EntityConnectionProvider.builder()
              .domainType(Chinook.DOMAIN)
              .clientTypeId(ChinookAppPanel.class.getName())
              .clientVersion(ChinookAppModel.VERSION)
              .user(user)
              .build();
      connectionProvider.connection();

      return connectionProvider;
    }
  }
}
