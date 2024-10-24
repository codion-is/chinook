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
 * Copyright (c) 2023 - 2024, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.ui;

import is.codion.framework.demos.chinook.domain.api.Chinook.Album;
import is.codion.plugin.imagepanel.NavigableImagePanel;
import is.codion.swing.common.ui.Utilities;
import is.codion.swing.common.ui.Windows;
import is.codion.swing.common.ui.component.Components;
import is.codion.swing.common.ui.component.value.AbstractComponentValue;
import is.codion.swing.common.ui.component.value.ComponentValue;
import is.codion.swing.common.ui.control.Control;
import is.codion.swing.common.ui.dialog.Dialogs;
import is.codion.swing.framework.model.SwingEntityEditModel;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.ui.EntityTableCellRenderer;
import is.codion.swing.framework.ui.EntityTablePanel;
import is.codion.swing.framework.ui.component.EntityComponentFactory;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static is.codion.framework.demos.chinook.ui.TrackTablePanel.RATINGS;

public final class AlbumTablePanel extends EntityTablePanel {

	private final NavigableImagePanel imagePanel;

	public AlbumTablePanel(SwingEntityTableModel tableModel) {
		super(tableModel, config -> config
						.editComponentFactory(Album.TAGS, new TagEditComponentFactory())
						.cellRenderer(Album.RATING, EntityTableCellRenderer.builder(Album.RATING, tableModel)
										.string(RATINGS::get)
										.toolTipData(true)
										.build()));
		imagePanel = new NavigableImagePanel();
		imagePanel.setPreferredSize(Windows.screenSizeRatio(0.5));
		table().doubleClickAction().set(viewCoverControl());
	}

	private Control viewCoverControl() {
		return Control.builder()
						.command(this::viewSelectedCover)
						.enabled(tableModel().selection().single())
						.build();
	}

	private void viewSelectedCover() {
		tableModel().selection().item().optional()
						.filter(album -> album.isNotNull(Album.COVER))
						.ifPresent(album -> displayImage(album.get(Album.TITLE), album.get(Album.COVER)));
	}

	private void displayImage(String title, byte[] imageBytes) {
		imagePanel.setImage(readImage(imageBytes));
		if (imagePanel.isShowing()) {
			Utilities.parentDialog(imagePanel).toFront();
		}
		else {
			Dialogs.componentDialog(imagePanel)
							.owner(Utilities.parentWindow(this))
							.title(title)
							.modal(false)
							.onClosed(dialog -> imagePanel.setImage(null))
							.show();
		}
	}

	private static BufferedImage readImage(byte[] bytes) {
		try {
			return ImageIO.read(new ByteArrayInputStream(bytes));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final class TagEditComponentFactory
					implements EntityComponentFactory<List<String>, AlbumTagPanel> {

		@Override
		public ComponentValue<List<String>, AlbumTagPanel> componentValue(SwingEntityEditModel editModel,
																																			List<String> value) {
			return new TagComponentValue(value);
		}
	}

	private static final class TagComponentValue extends AbstractComponentValue<List<String>, AlbumTagPanel> {

		private TagComponentValue(List<String> tags) {
			super(new AlbumTagPanel(Components.list(new DefaultListModel<String>())
							.items()
							.value(tags)
							.buildValue()));
		}

		@Override
		protected List<String> getComponentValue() {
			return component().tagsValue().get();
		}

		@Override
		protected void setComponentValue(List<String> value) {
			component().tagsValue().set(value);
		}
	}
}
