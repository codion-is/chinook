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
package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Album;
import is.codion.framework.demos.chinook.domain.api.Chinook.Artist;
import is.codion.framework.demos.chinook.domain.api.Chinook.Genre;
import is.codion.framework.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.demos.chinook.model.ChinookAppModel;
import is.codion.framework.domain.entity.Entity;
import is.codion.swing.framework.model.EntityComboBoxModel;
import is.codion.swing.framework.model.SwingEntityEditModel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.model.tools.loadtest.AbstractEntityUsageScenario;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static is.codion.framework.db.condition.Condition.where;
import static is.codion.framework.db.criteria.Criteria.column;
import static is.codion.swing.framework.model.tools.loadtest.EntityLoadTestModel.selectRandomItem;
import static is.codion.swing.framework.model.tools.loadtest.EntityLoadTestModel.selectRandomRow;
import static java.util.Arrays.asList;

public final class InsertDeleteAlbum extends AbstractEntityUsageScenario<ChinookAppModel> {

  private static final Random RANDOM = new Random();
  private static final Collection<String> GENRES =
          asList("Classical", "Easy Listening", "Jazz", "Latin", "Reggae", "Soundtrack");

  @Override
  protected void perform(ChinookAppModel application) throws Exception {
    SwingEntityModel artistModel = application.entityModel(Artist.TYPE);
    artistModel.tableModel().refresh();
    selectRandomRow(artistModel.tableModel());
    Entity artist = artistModel.tableModel().selectionModel().getSelectedItem();
    SwingEntityModel albumModel = artistModel.detailModel(Album.TYPE);
    SwingEntityEditModel albumEditModel = albumModel.editModel();
    albumEditModel.setEntity(application.entities().builder(Album.TYPE)
            .with(Album.ARTIST_FK, artist)
            .with(Album.TITLE, "Title")
            .build());
    Entity insertedAlbum = albumEditModel.insert();
    SwingEntityEditModel trackEditModel = albumModel.detailModel(Track.TYPE).editModel();
    List<Entity> genres = trackEditModel.connectionProvider().connection()
            .select(where(column(Genre.NAME).in(GENRES)));
    EntityComboBoxModel mediaTypeComboBoxModel =
            trackEditModel.foreignKeyComboBoxModel(Track.MEDIATYPE_FK);
    selectRandomItem(mediaTypeComboBoxModel);
    for (int i = 0; i < 10; i++) {
      trackEditModel.put(Track.ALBUM_FK, insertedAlbum);
      trackEditModel.put(Track.NAME, "Track " + i);
      trackEditModel.put(Track.BYTES, 10000000);
      trackEditModel.put(Track.COMPOSER, "Composer");
      trackEditModel.put(Track.MILLISECONDS, 1000000);
      trackEditModel.put(Track.UNITPRICE, BigDecimal.valueOf(2));
      trackEditModel.put(Track.GENRE_FK, genres.get(RANDOM.nextInt(genres.size())));
      trackEditModel.put(Track.MEDIATYPE_FK, mediaTypeComboBoxModel.selectedValue());
      trackEditModel.insert();
    }

    SwingEntityTableModel trackTableModel = albumModel.detailModel(Track.TYPE).tableModel();
    trackTableModel.selectionModel().selectAll();
    trackTableModel.deleteSelected();
    albumEditModel.delete();
  }

  @Override
  public int defaultWeight() {
    return 3;
  }
}
