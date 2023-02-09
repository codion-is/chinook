/*
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Playlist;
import is.codion.framework.demos.chinook.domain.api.Chinook.Playlist.RandomPlaylistParameters;
import is.codion.framework.demos.chinook.domain.api.Chinook.PlaylistTrack;
import is.codion.framework.demos.chinook.model.ChinookAppModel;
import is.codion.framework.demos.chinook.model.PlaylistTableModel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.tools.loadtest.AbstractEntityUsageScenario;

import java.util.Random;

public final class RandomPlaylist extends AbstractEntityUsageScenario<ChinookAppModel> {

  private static final Random RANDOM = new Random();
  private static final String PLAYLIST_NAME = "Random playlist";

  @Override
  protected void perform(ChinookAppModel application) throws Exception {
    SwingEntityModel playlistModel = application.entityModel(Playlist.TYPE);
    PlaylistTableModel playlistTableModel = (PlaylistTableModel) playlistModel.tableModel();
    playlistTableModel.refresh();
    playlistTableModel.createRandomPlaylist(new RandomPlaylistParameters(PLAYLIST_NAME + " " + System.currentTimeMillis(),
            RANDOM.nextInt(100) + 25));
    SwingEntityTableModel playlistTrackTableModel = playlistModel.detailModel(PlaylistTrack.TYPE).tableModel();
    playlistTrackTableModel.selectionModel().selectAll();
    playlistTrackTableModel.deleteSelected();
    playlistTableModel.deleteSelected();
  }
}
