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

import is.codion.common.db.exception.DatabaseException;
import is.codion.common.state.State;
import is.codion.common.state.StateObserver;
import is.codion.common.value.Value;
import is.codion.common.value.ValueSet;
import is.codion.framework.db.EntityConnection.Select;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.demos.chinook.domain.api.Chinook.Genre;
import is.codion.framework.demos.chinook.domain.api.Chinook.Playlist.RandomPlaylistParameters;
import is.codion.framework.domain.entity.Entity;
import is.codion.swing.common.ui.component.Components;
import is.codion.swing.common.ui.component.text.NumberField;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.util.ResourceBundle;

import static is.codion.common.NullOrEmpty.nullOrEmpty;
import static is.codion.framework.domain.entity.OrderBy.ascending;
import static is.codion.swing.common.ui.component.Components.*;
import static is.codion.swing.common.ui.layout.Layouts.borderLayout;

final class RandomPlaylistParametersPanel extends JPanel {

  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(RandomPlaylistParametersPanel.class.getName());

  private final RandomPlaylistParametersModel model = new RandomPlaylistParametersModel();

  private final JTextField playlistNameField;
  private final NumberField<Integer> noOfTracksField;
  private final JList<Entity> genreList;

  RandomPlaylistParametersPanel(EntityConnectionProvider connectionProvider) {
    super(borderLayout());
    this.playlistNameField = textField(model.playlistNameValue)
            .transferFocusOnEnter(true)
            .selectAllOnFocusGained(true)
            .maximumLength(120)
            .columns(10)
            .build();
    this.noOfTracksField = integerField(model.noOfTracksValue)
            .valueRange(1, 5000)
            .transferFocusOnEnter(true)
            .selectAllOnFocusGained(true)
            .columns(3)
            .build();
    this.genreList = Components.list(createGenreListModel(connectionProvider), model.genresValue)
            .selectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
            .visibleRowCount(5)
            .build();
    gridLayoutPanel(1, 2)
            .add(new JLabel(BUNDLE.getString("playlist_name")))
            .add(new JLabel(BUNDLE.getString("no_of_tracks")))
            .build(panel -> add(panel, BorderLayout.NORTH));
    gridLayoutPanel(1, 2)
            .add(playlistNameField)
            .add(noOfTracksField)
            .build(panel -> add(panel, BorderLayout.CENTER));
    borderLayoutPanel()
            .northComponent(new JLabel(BUNDLE.getString("genres")))
            .centerComponent(new JScrollPane(genreList))
            .build(panel -> add(panel, BorderLayout.SOUTH));
  }

  StateObserver parametersValidState() {
    return model.parametersValidState.observer();
  }

  RandomPlaylistParameters get() {
    return new RandomPlaylistParameters(model.playlistNameValue.get(), model.noOfTracksValue.get(), model.genresValue.get());
  }

  private static DefaultListModel<Entity> createGenreListModel(EntityConnectionProvider connectionProvider) {
    DefaultListModel<Entity> listModel = new DefaultListModel<>();
    try {
      connectionProvider.connection().select(Select.all(Genre.TYPE)
                      .orderBy(ascending(Genre.NAME))
                      .build())
              .forEach(listModel::addElement);

      return listModel;
    }
    catch (DatabaseException e) {
      throw new RuntimeException(e);
    }
  }

  private static final class RandomPlaylistParametersModel {

    private final Value<String> playlistNameValue = Value.value();
    private final Value<Integer> noOfTracksValue = Value.value();
    private final ValueSet<Entity> genresValue = ValueSet.valueSet();
    private final State parametersValidState = State.state();

    private RandomPlaylistParametersModel() {
      playlistNameValue.addListener(this::validate);
      noOfTracksValue.addListener(this::validate);
      genresValue.addListener(this::validate);
      validate();
    }

    private void validate() {
      parametersValidState.set(isValid());
    }

    private boolean isValid() {
      if (nullOrEmpty(playlistNameValue.get())) {
        return false;
      }
      if (noOfTracksValue.isNull()) {
        return false;
      }
      if (genresValue.isEmpty()) {
        return false;
      }

      return true;
    }
  }
}
