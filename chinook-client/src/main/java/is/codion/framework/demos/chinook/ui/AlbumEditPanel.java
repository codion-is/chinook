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

import is.codion.framework.demos.chinook.domain.api.Chinook.Artist;
import is.codion.swing.framework.model.SwingEntityEditModel;
import is.codion.swing.framework.ui.EntityEditPanel;

import javax.swing.JPanel;
import java.awt.BorderLayout;

import static is.codion.framework.demos.chinook.domain.api.Chinook.Album;
import static is.codion.swing.common.ui.component.Components.borderLayoutPanel;
import static is.codion.swing.common.ui.component.Components.gridLayoutPanel;
import static is.codion.swing.common.ui.layout.Layouts.borderLayout;

public final class AlbumEditPanel extends EntityEditPanel {

  public AlbumEditPanel(SwingEntityEditModel editModel) {
    super(editModel);
    defaultTextFieldColumns().set(15);
  }

  @Override
  protected void initializeUI() {
    initialFocusAttribute().set(Album.ARTIST_FK);

    createForeignKeySearchFieldPanel(Album.ARTIST_FK, this::createArtistEditPanel)
            .add(true)
            .edit(true);
    createTextField(Album.TITLE);
    component(Album.COVER).set(new CoverArtPanel(editModel().value(Album.COVER)));

    JPanel centerPanel = borderLayoutPanel()
            .westComponent(borderLayoutPanel()
                    .northComponent(gridLayoutPanel(2, 1)
                            .add(createInputPanel(Album.ARTIST_FK))
                            .add(createInputPanel(Album.TITLE))
                            .build())
                    .build())
            .centerComponent(createInputPanel(Album.COVER))
            .build();

    setLayout(borderLayout());
    add(centerPanel, BorderLayout.CENTER);
  }

  private EntityEditPanel createArtistEditPanel() {
    return new ArtistEditPanel(new SwingEntityEditModel(Artist.TYPE, editModel().connectionProvider()));
  }
}
