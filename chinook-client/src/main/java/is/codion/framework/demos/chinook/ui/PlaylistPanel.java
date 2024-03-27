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
package is.codion.framework.demos.chinook.ui;

import is.codion.framework.demos.chinook.domain.api.Chinook.PlaylistTrack;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.ui.EntityPanel;
import is.codion.swing.framework.ui.TabbedDetailLayout;

public final class PlaylistPanel extends EntityPanel {

	public PlaylistPanel(SwingEntityModel playlistModel) {
		super(playlistModel,
						new PlaylistEditPanel(playlistModel.editModel()),
						new PlaylistTablePanel(playlistModel.tableModel()),
						config -> config.detailLayout(TabbedDetailLayout.builder()
										.splitPaneResizeWeight(0.25)
										.build()));

		SwingEntityModel playlistTrackModel = playlistModel.detailModel(PlaylistTrack.TYPE);
		EntityPanel playlistTrackPanel = new EntityPanel(playlistTrackModel,
						new PlaylistTrackEditPanel(playlistTrackModel.editModel()),
						new PlaylistTrackTablePanel(playlistTrackModel.tableModel()));

		addDetailPanel(playlistTrackPanel);
	}
}
