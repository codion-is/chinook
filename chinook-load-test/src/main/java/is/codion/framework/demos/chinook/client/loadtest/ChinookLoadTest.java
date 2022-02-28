/*
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson. All Rights Reserved.
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
import is.codion.framework.demos.chinook.model.ChinookApplicationModel;
import is.codion.framework.demos.chinook.ui.ChinookAppPanel;
import is.codion.swing.common.tools.ui.loadtest.LoadTestPanel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.tools.loadtest.EntityLoadTestModel;

import javax.swing.SwingUtilities;
import java.util.List;

import static is.codion.framework.demos.chinook.domain.api.Chinook.Genre;
import static is.codion.framework.demos.chinook.domain.api.Chinook.Track;

public final class ChinookLoadTest extends EntityLoadTestModel<ChinookApplicationModel> {

  private static final User UNIT_TEST_USER =
          User.parse(System.getProperty("codion.test.user", "scott:tiger"));

  public ChinookLoadTest() {
    super(UNIT_TEST_USER, List.of(new ViewGenre(), new ViewCustomerReport(), new ViewInvoice(), new ViewAlbum(),
            new UpdateTotals(), new InsertDeleteAlbum(), new LogoutLogin(), new RaisePrices(), new RandomPlaylist()));
  }

  @Override
  protected ChinookApplicationModel initializeApplication() throws CancelException {
    ChinookApplicationModel applicationModel = new ChinookApplicationModel(
            EntityConnectionProvider.connectionProvider().setDomainClassName("is.codion.framework.demos.chinook.domain.impl.ChinookImpl")
                    .setClientTypeId(ChinookAppPanel.class.getName()).setUser(getUser()));

    SwingEntityModel customerModel = applicationModel.getEntityModel(Customer.TYPE);
    SwingEntityModel invoiceModel = customerModel.getDetailModel(Invoice.TYPE);
    customerModel.addLinkedDetailModel(invoiceModel);

    SwingEntityModel artistModel = applicationModel.getEntityModel(Artist.TYPE);
    SwingEntityModel albumModel = artistModel.getDetailModel(Album.TYPE);
    SwingEntityModel trackModel = albumModel.getDetailModel(Track.TYPE);

    artistModel.addLinkedDetailModel(albumModel);
    albumModel.addLinkedDetailModel(trackModel);

    SwingEntityModel playlistModel = applicationModel.getEntityModel(Playlist.TYPE);
    SwingEntityModel playlistTrackModel = playlistModel.getDetailModel(PlaylistTrack.TYPE);
    playlistModel.addLinkedDetailModel(playlistTrackModel);

    /* Add a Genre model used in the ViewGenre scenario */
    SwingEntityModel genreModel = new SwingEntityModel(Genre.TYPE, applicationModel.getConnectionProvider());
    SwingEntityModel genreTrackModel = new SwingEntityModel(Track.TYPE, applicationModel.getConnectionProvider());
    genreModel.addDetailModel(genreTrackModel);
    genreModel.addLinkedDetailModel(genreTrackModel);

    applicationModel.addEntityModel(genreModel);

    return applicationModel;
  }

  public static void main(String[] args) throws Exception {
    SwingUtilities.invokeLater(new Runner());
  }

  private static final class Runner implements Runnable {
    @Override
    public void run() {
      try {
        new LoadTestPanel<>(new ChinookLoadTest()).showFrame();
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
