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

import is.codion.common.model.CancelException;
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
import is.codion.framework.demos.chinook.domain.api.Chinook.Album;
import is.codion.framework.demos.chinook.domain.api.Chinook.Artist;
import is.codion.framework.demos.chinook.domain.api.Chinook.Customer;
import is.codion.framework.demos.chinook.domain.api.Chinook.Invoice;
import is.codion.framework.demos.chinook.domain.api.Chinook.Playlist;
import is.codion.framework.demos.chinook.domain.api.Chinook.PlaylistTrack;
import is.codion.framework.demos.chinook.model.ChinookAppModel;
import is.codion.framework.demos.chinook.ui.ChinookAppPanel;
import is.codion.swing.common.ui.tools.loadtest.LoadTestPanel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.model.tools.loadtest.EntityLoadTestModel;

import java.util.List;

import static is.codion.framework.demos.chinook.domain.api.Chinook.Genre;
import static is.codion.framework.demos.chinook.domain.api.Chinook.Track;

public final class ChinookLoadTest extends EntityLoadTestModel<ChinookAppModel> {

  private static final User UNIT_TEST_USER =
          User.parse(System.getProperty("codion.test.user", "scott:tiger"));

  public ChinookLoadTest() {
    super(UNIT_TEST_USER, List.of(new ViewGenre(), new ViewCustomerReport(), new ViewInvoice(), new ViewAlbum(),
            new UpdateTotals(), new InsertDeleteAlbum(), new LogoutLogin(), new RaisePrices(), new RandomPlaylist()));
  }

  @Override
  protected ChinookAppModel createApplication() throws CancelException {
    ChinookAppModel applicationModel = new ChinookAppModel(EntityConnectionProvider.builder()
            .domainClassName("is.codion.framework.demos.chinook.domain.impl.ChinookImpl")
            .clientTypeId(ChinookAppPanel.class.getName())
            .clientVersion(ChinookAppModel.VERSION)
            .user(getUser())
            .build());

    SwingEntityModel customerModel = applicationModel.entityModel(Customer.TYPE);
    SwingEntityModel invoiceModel = customerModel.detailModel(Invoice.TYPE);
    customerModel.detailModelLink(invoiceModel).setActive(true);

    SwingEntityModel artistModel = applicationModel.entityModel(Artist.TYPE);
    SwingEntityModel albumModel = artistModel.detailModel(Album.TYPE);
    SwingEntityModel trackModel = albumModel.detailModel(Track.TYPE);

    artistModel.detailModelLink(albumModel).setActive(true);
    albumModel.detailModelLink(trackModel).setActive(true);

    SwingEntityModel playlistModel = applicationModel.entityModel(Playlist.TYPE);
    SwingEntityModel playlistTrackModel = playlistModel.detailModel(PlaylistTrack.TYPE);
    playlistModel.detailModelLink(playlistTrackModel).setActive(true);

    /* Add a Genre model used in the ViewGenre scenario */
    SwingEntityModel genreModel = new SwingEntityModel(Genre.TYPE, applicationModel.connectionProvider());
    SwingEntityModel genreTrackModel = new SwingEntityModel(Track.TYPE, applicationModel.connectionProvider());
    genreModel.addDetailModel(genreTrackModel).setActive(true);

    applicationModel.addEntityModel(genreModel);

    return applicationModel;
  }

  public static void main(String[] args) {
    new LoadTestPanel<>(new ChinookLoadTest()).run();
  }
}
