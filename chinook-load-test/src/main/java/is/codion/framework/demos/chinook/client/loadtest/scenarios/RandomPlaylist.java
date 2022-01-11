/*
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Playlist;
import is.codion.framework.demos.chinook.domain.api.Chinook.Playlist.RandomPlaylistParameters;
import is.codion.framework.demos.chinook.domain.api.Chinook.PlaylistTrack;
import is.codion.framework.demos.chinook.model.ChinookApplicationModel;
import is.codion.framework.demos.chinook.model.PlaylistTableModel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.tools.loadtest.AbstractEntityUsageScenario;

import java.util.Random;

public final class RandomPlaylist extends AbstractEntityUsageScenario<ChinookApplicationModel> {

  private static final Random RANDOM = new Random();
  private static final String PLAYLIST_NAME = "Random playlist";

  @Override
  protected void perform(final ChinookApplicationModel application) throws Exception {
    final SwingEntityModel playlistModel = application.getEntityModel(Playlist.TYPE);
    final PlaylistTableModel playlistTableModel = (PlaylistTableModel) playlistModel.getTableModel();
    playlistTableModel.refresh();
    playlistTableModel.createRandomPlaylist(new RandomPlaylistParameters(PLAYLIST_NAME + " " + System.currentTimeMillis(),
            RANDOM.nextInt(100) + 25));
    final SwingEntityTableModel playlistTrackTableModel = playlistModel.getDetailModel(PlaylistTrack.TYPE).getTableModel();
    playlistTrackTableModel.getSelectionModel().selectAll();
    playlistTrackTableModel.deleteSelected();
    playlistTableModel.deleteSelected();
  }
}