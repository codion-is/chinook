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
 * Copyright (c) 2004 - 2024, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.ui;

import is.codion.demos.chinook.domain.api.Chinook.PlaylistTrack;
import is.codion.framework.domain.entity.Entity;
import is.codion.swing.common.ui.component.table.ColumnConditionPanel;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.ui.EntityEditPanel.Confirmer;
import is.codion.swing.framework.ui.EntityTablePanel;
import is.codion.swing.framework.ui.component.EntitySearchField;

import java.util.Optional;
import java.util.stream.Stream;

public final class PlaylistTrackTablePanel extends EntityTablePanel {

	public PlaylistTrackTablePanel(SwingEntityTableModel tableModel) {
		super(tableModel, new PlaylistTrackEditPanel(tableModel.editModel()), config -> config
						.editComponentFactory(PlaylistTrack.TRACK_FK, new TrackComponentFactory(PlaylistTrack.TRACK_FK))
						// Skip confirmation when deleting
						.deleteConfirmer(Confirmer.NONE)
						.includeEditControl(false));
		table().columnModel()
						.visible().set(PlaylistTrack.TRACK_FK, PlaylistTrack.ARTIST, PlaylistTrack.ALBUM);
		configureTrackConditionPanel();
	}

	private void configureTrackConditionPanel() {
		ColumnConditionPanel<Entity> conditionPanel = conditions().get(PlaylistTrack.TRACK_FK);
		Stream.of(conditionPanel.fields().equal(), conditionPanel.fields().in())
						.flatMap(Optional::stream)
						.map(EntitySearchField.class::cast)
						.forEach(field -> field.selectorFactory().set(new TrackSelectorFactory()));
	}
}