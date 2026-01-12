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
 * Copyright (c) 2004 - 2026, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.ui;

import is.codion.demos.chinook.domain.api.Chinook.PlaylistTrack;
import is.codion.swing.common.ui.layout.Layouts;
import is.codion.swing.framework.model.SwingEntityEditModel;
import is.codion.swing.framework.ui.EntityEditPanel;

import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

import static is.codion.swing.common.ui.component.Components.borderLayoutPanel;
import static is.codion.swing.common.ui.layout.Layouts.borderLayout;

final class PlaylistTrackEditPanel extends EntityEditPanel {

	PlaylistTrackEditPanel(SwingEntityEditModel editModel) {
		super(editModel, config -> config
						// Skip confirmation when deleting
						.confirmDelete(false));
	}

	@Override
	protected void initializeUI() {
		createSearchField(PlaylistTrack.TRACK_FK)
						.selector(new TrackSelector())
						.transferFocusOnEnter(false)
						.columns(25);

		setLayout(borderLayout());
		add(borderLayoutPanel()
						.west(component(PlaylistTrack.TRACK_FK).label())
						.center(component(PlaylistTrack.TRACK_FK).get())
						.border(new EmptyBorder(Layouts.GAP.get(), Layouts.GAP.get(), 0, Layouts.GAP.get())), BorderLayout.CENTER);
	}
}