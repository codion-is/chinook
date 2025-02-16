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
 * Copyright (c) 2004 - 2025, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.ui;

import is.codion.demos.chinook.domain.api.Chinook.Playlist;
import is.codion.swing.common.ui.layout.Layouts;
import is.codion.swing.framework.model.SwingEntityEditModel;
import is.codion.swing.framework.ui.EntityEditPanel;

import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

import static is.codion.swing.common.ui.component.Components.borderLayoutPanel;
import static is.codion.swing.common.ui.layout.Layouts.borderLayout;

final class PlaylistEditPanel extends EntityEditPanel {

	PlaylistEditPanel(SwingEntityEditModel editModel) {
		super(editModel, config -> config
						// Skip confirmation when updating
						.updateConfirmer(Confirmer.NONE));
	}

	@Override
	protected void initializeUI() {
		focus().initial().set(Playlist.NAME);

		setLayout(borderLayout());
		add(borderLayoutPanel()
						.westComponent(createLabel(Playlist.NAME).build())
						.centerComponent(createTextField(Playlist.NAME)
										.transferFocusOnEnter(false)
										.columns(20)
										.build())
						.border(new EmptyBorder(Layouts.GAP.get(), Layouts.GAP.get(), 0, Layouts.GAP.get()))
						.build(), BorderLayout.CENTER);
	}
}