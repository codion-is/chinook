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
package is.codion.framework.demos.chinook.model;

import is.codion.common.version.Version;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.swing.framework.model.SwingEntityApplicationModel;
import is.codion.swing.framework.model.SwingEntityModel;

import static is.codion.framework.demos.chinook.domain.api.Chinook.*;

public final class ChinookAppModel extends SwingEntityApplicationModel {

  public static final Version VERSION = Version.parsePropertiesFile(ChinookAppModel.class, "/version.properties");

  public ChinookAppModel(EntityConnectionProvider connectionProvider) {
    super(connectionProvider, VERSION);
    addEntityModel(initializeArtistModel(connectionProvider));
    addEntityModel(initializePlaylistModel(connectionProvider));
    addEntityModel(initializeCustomerModel(connectionProvider));
  }

  private static SwingEntityModel initializeArtistModel(EntityConnectionProvider connectionProvider) {
    SwingEntityModel artistModel = new SwingEntityModel(Artist.TYPE, connectionProvider);
    SwingEntityModel albumModel = new SwingEntityModel(Album.TYPE, connectionProvider);
    SwingEntityModel trackModel = new SwingEntityModel(new TrackTableModel(connectionProvider));
    trackModel.editModel().initializeComboBoxModels(Track.MEDIATYPE_FK, Track.GENRE_FK);

    albumModel.addDetailModel(trackModel);
    artistModel.addDetailModel(albumModel);

    artistModel.tableModel().refresh();

    return artistModel;
  }

  private static SwingEntityModel initializePlaylistModel(EntityConnectionProvider connectionProvider) {
    SwingEntityModel playlistModel = new SwingEntityModel(new PlaylistTableModel(connectionProvider));
    SwingEntityModel playlistTrackModel = new SwingEntityModel(PlaylistTrack.TYPE, connectionProvider);
    playlistTrackModel.editModel().initializeComboBoxModels(PlaylistTrack.PLAYLIST_FK);

    playlistModel.addDetailModel(playlistTrackModel);

    playlistModel.tableModel().refresh();

    return playlistModel;
  }

  private static SwingEntityModel initializeCustomerModel(EntityConnectionProvider connectionProvider) {
    SwingEntityModel customerModel = new SwingEntityModel(Customer.TYPE, connectionProvider);
    customerModel.editModel().initializeComboBoxModels(Customer.SUPPORTREP_FK);
    SwingEntityModel invoiceModel = new InvoiceModel(connectionProvider);
    customerModel.addDetailModel(invoiceModel);

    customerModel.tableModel().refresh();

    return customerModel;
  }
}
