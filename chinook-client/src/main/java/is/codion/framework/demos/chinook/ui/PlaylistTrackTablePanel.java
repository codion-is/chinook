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
 * Copyright (c) 2004 - 2023, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.ui;

import is.codion.framework.demos.chinook.domain.api.Chinook.PlaylistTrack;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.ui.EntityEditPanel.Confirmer;
import is.codion.swing.framework.ui.EntityTablePanel;
import is.codion.swing.framework.ui.component.EntitySearchField;

public final class PlaylistTrackTablePanel extends EntityTablePanel {

	public PlaylistTrackTablePanel(SwingEntityTableModel tableModel) {
		super(tableModel, new PlaylistTrackEditPanel(tableModel.editModel()), config -> config
						.editComponentFactory(PlaylistTrack.TRACK_FK, new TrackComponentFactory())
						// Skip confirmation when deleting
						.deleteConfirmer(Confirmer.NONE)
						.includeEditControl(false));
		table().columnModel()
						.setVisibleColumns(PlaylistTrack.TRACK_FK, PlaylistTrack.ARTIST, PlaylistTrack.ALBUM);
		configureTrackConditionPanel();
	}

	private void configureTrackConditionPanel() {
		conditionPanel().conditionPanel(PlaylistTrack.TRACK_FK)
						.map(conditionPanel -> (EntitySearchField) conditionPanel.equalField())
						.ifPresent(equalField -> equalField.selectorFactory().set(new TrackSelectorFactory()));
	}
}
