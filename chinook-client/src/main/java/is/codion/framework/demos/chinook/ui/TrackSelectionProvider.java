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

import is.codion.framework.model.EntitySearchModel;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.ui.EntitySearchField;

import java.awt.Dimension;

import static is.codion.framework.demos.chinook.domain.api.Chinook.Track;
import static javax.swing.SortOrder.ASCENDING;

final class TrackSelectionProvider extends EntitySearchField.TableSelectionProvider {

  TrackSelectionProvider(EntitySearchModel trackSearchModel) {
    super(trackSearchModel);
    SwingEntityTableModel tableModel = (SwingEntityTableModel) table().getModel();
    tableModel.columnModel().setVisibleColumns(Track.ARTIST_DENORM, Track.ALBUM_FK, Track.NAME);
    tableModel.sortModel().setSortOrder(Track.ARTIST_DENORM, ASCENDING);
    tableModel.sortModel().addSortOrder(Track.ALBUM_FK, ASCENDING);
    tableModel.sortModel().addSortOrder(Track.NAME, ASCENDING);
    setPreferredSize(new Dimension(500, 300));
  }
}
