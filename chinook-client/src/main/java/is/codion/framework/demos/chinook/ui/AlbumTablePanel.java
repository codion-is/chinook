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
 * Copyright (c) 2023, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.ui;

import is.codion.framework.demos.chinook.domain.api.Chinook.Album;
import is.codion.framework.domain.entity.Entity;
import is.codion.plugin.imagepanel.NavigableImagePanel;
import is.codion.swing.common.ui.Utilities;
import is.codion.swing.common.ui.Windows;
import is.codion.swing.common.ui.control.Control;
import is.codion.swing.common.ui.dialog.Dialogs;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.ui.EntityTablePanel;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class AlbumTablePanel extends EntityTablePanel {

  private final NavigableImagePanel imagePanel;

  public AlbumTablePanel(SwingEntityTableModel tableModel) {
    super(tableModel);
    imagePanel = new NavigableImagePanel();
    imagePanel.setPreferredSize(Windows.screenSizeRatio(0.5));
    table().setDoubleClickAction(viewCoverControl());
  }

  private Control viewCoverControl() {
    return Control.builder(this::viewSelectedCover)
            .enabledState(tableModel().selectionModel().singleSelectionObserver())
            .build();
  }

  private void viewSelectedCover() throws IOException {
    Entity selectedAlbum = tableModel().selectionModel().getSelectedItem();
    if (selectedAlbum != null && selectedAlbum.isNotNull(Album.COVER)) {
      displayImage(selectedAlbum.get(Album.TITLE), selectedAlbum.get(Album.COVER));
    }
  }

  private void displayImage(String title, byte[] imageBytes) throws IOException {
    imagePanel.setImage(ImageIO.read(new ByteArrayInputStream(imageBytes)));
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
}
