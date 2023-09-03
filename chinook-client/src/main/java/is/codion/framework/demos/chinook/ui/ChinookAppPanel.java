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

import is.codion.common.model.CancelException;
import is.codion.common.model.UserPreferences;
import is.codion.common.user.User;
import is.codion.framework.demos.chinook.model.ChinookAppModel;
import is.codion.framework.demos.chinook.model.EmployeeTableModel;
import is.codion.framework.model.EntityEditModel;
import is.codion.swing.common.ui.component.combobox.Completion;
import is.codion.swing.common.ui.component.table.FilteredTable;
import is.codion.swing.common.ui.component.table.FilteredTableCellRenderer;
import is.codion.swing.common.ui.control.Control;
import is.codion.swing.common.ui.control.Controls;
import is.codion.swing.common.ui.laf.LookAndFeelComboBox;
import is.codion.swing.common.ui.laf.LookAndFeelProvider;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.ui.EntityApplicationPanel;
import is.codion.swing.framework.ui.EntityPanel;
import is.codion.swing.framework.ui.EntityTablePanel;
import is.codion.swing.framework.ui.ReferentialIntegrityErrorHandling;
import is.codion.swing.framework.ui.TabbedPanelLayout;

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static is.codion.framework.demos.chinook.domain.api.Chinook.*;
import static is.codion.swing.common.ui.component.Components.gridLayoutPanel;
import static is.codion.swing.common.ui.component.Components.radioButton;
import static javax.swing.JOptionPane.showMessageDialog;

public final class ChinookAppPanel extends EntityApplicationPanel<ChinookAppModel> {

  private static final String DEFAULT_FLAT_LOOK_AND_FEEL = "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme";
  private static final String LANGUAGE_PREFERENCES_KEY = ChinookAppPanel.class.getSimpleName() + ".language";
  private static final Locale LOCALE_IS = new Locale("is", "IS");
  private static final Locale LOCALE_EN = new Locale("en", "EN");
  private static final String LANGUAGE_IS = "is";
  private static final String LANGUAGE_EN = "en";

  private static final String SELECT_LANGUAGE = "select_language";

  /* Non-static so this is not initialized before main(), which sets the locale */
  private final ResourceBundle bundle = ResourceBundle.getBundle(ChinookAppPanel.class.getName());

  public ChinookAppPanel(ChinookAppModel appModel) {
    super(appModel);
  }

  @Override
  protected List<EntityPanel> createEntityPanels() {
    return List.of(
            new CustomerPanel(applicationModel().entityModel(Customer.TYPE)),
            new ArtistPanel(applicationModel().entityModel(Artist.TYPE)),
            new PlaylistPanel(applicationModel().entityModel(Playlist.TYPE))
    );
  }

  @Override
  protected List<EntityPanel.Builder> createSupportEntityPanelBuilders() {
    EntityPanel.Builder trackPanelBuilder =
            EntityPanel.builder(Track.TYPE)
                    .tablePanelClass(TrackTablePanel.class);

    SwingEntityModel.Builder genreModelBuilder =
            SwingEntityModel.builder(Genre.TYPE)
                    .detailModelBuilder(SwingEntityModel.builder(Track.TYPE));

    EntityPanel.Builder genrePanelBuilder =
            EntityPanel.builder(genreModelBuilder)
                    .editPanelClass(GenreEditPanel.class)
                    .detailPanelBuilder(trackPanelBuilder)
                    .panelLayout(TabbedPanelLayout.detailPanelState(EntityPanel.PanelState.HIDDEN));

    SwingEntityModel.Builder mediaTypeModelBuilder =
            SwingEntityModel.builder(MediaType.TYPE)
                    .detailModelBuilder(SwingEntityModel.builder(Track.TYPE));

    EntityPanel.Builder mediaTypePanelBuilder =
            EntityPanel.builder(mediaTypeModelBuilder)
                    .editPanelClass(MediaTypeEditPanel.class)
                    .detailPanelBuilder(trackPanelBuilder)
                    .panelLayout(TabbedPanelLayout.detailPanelState(EntityPanel.PanelState.HIDDEN));

    EntityPanel.Builder customerPanelBuilder =
            EntityPanel.builder(Customer.TYPE)
                    .editPanelClass(CustomerEditPanel.class)
                    .tablePanelClass(CustomerTablePanel.class);

    SwingEntityModel.Builder employeeModelBuilder =
            SwingEntityModel.builder(Employee.TYPE)
                    .detailModelBuilder(SwingEntityModel.builder(Customer.TYPE))
                    .tableModelClass(EmployeeTableModel.class);

    EntityPanel.Builder employeePanelBuilder =
            EntityPanel.builder(employeeModelBuilder)
                    .editPanelClass(EmployeeEditPanel.class)
                    .tablePanelClass(EmployeeTablePanel.class)
                    .detailPanelBuilder(customerPanelBuilder)
                    .panelLayout(TabbedPanelLayout.builder()
                            .detailPanelState(EntityPanel.PanelState.HIDDEN)
                            .build())
                    .preferredSize(new Dimension(1000, 500));

    return List.of(genrePanelBuilder, mediaTypePanelBuilder, employeePanelBuilder);
  }

  @Override
  protected Controls createViewMenuControls() {
    return super.createViewMenuControls()
            .addAt(2, Control.builder(this::selectLanguage)
                    .name(bundle.getString(SELECT_LANGUAGE))
                    .build());
  }

  private void selectLanguage() {
    String currentLanguage = UserPreferences.getUserPreference(LANGUAGE_PREFERENCES_KEY, Locale.getDefault().getLanguage());
    JPanel languagePanel = gridLayoutPanel(2, 1).build();
    ButtonGroup buttonGroup = new ButtonGroup();
    radioButton()
            .text(bundle.getString("english"))
            .selected(currentLanguage.equals(LANGUAGE_EN))
            .buttonGroup(buttonGroup)
            .build(languagePanel::add);
    JRadioButton isButton = radioButton()
            .text(bundle.getString("icelandic"))
            .selected(currentLanguage.equals(LANGUAGE_IS))
            .buttonGroup(buttonGroup)
            .build(languagePanel::add);
    showMessageDialog(this, languagePanel, bundle.getString("language"), JOptionPane.QUESTION_MESSAGE);
    String selectedLanguage = isButton.isSelected() ? LANGUAGE_IS : LANGUAGE_EN;
    if (!currentLanguage.equals(selectedLanguage)) {
      UserPreferences.setUserPreference(LANGUAGE_PREFERENCES_KEY, selectedLanguage);
      showMessageDialog(this, bundle.getString("language_has_been_changed"));
    }
  }

  public static void main(String[] args) throws CancelException {
    String language = UserPreferences.getUserPreference(LANGUAGE_PREFERENCES_KEY, Locale.getDefault().getLanguage());
    Locale.setDefault(LANGUAGE_IS.equals(language) ? LOCALE_IS : LOCALE_EN);
    Arrays.stream(FlatAllIJThemes.INFOS).forEach(LookAndFeelProvider::addLookAndFeelProvider);
    LookAndFeelComboBox.CHANGE_ON_SELECTION.set(true);
    Completion.COMBO_BOX_COMPLETION_MODE.set(Completion.Mode.AUTOCOMPLETE);
    EntityEditModel.POST_EDIT_EVENTS.set(true);
    EntityPanel.TOOLBAR_CONTROLS.set(true);
    EntityTablePanel.COLUMN_SELECTION.set(EntityTablePanel.ColumnSelection.MENU);
    FilteredTable.AUTO_RESIZE_MODE.set(JTable.AUTO_RESIZE_ALL_COLUMNS);
    FilteredTableCellRenderer.NUMERICAL_HORIZONTAL_ALIGNMENT.set(SwingConstants.CENTER);
    FilteredTableCellRenderer.TEMPORAL_HORIZONTAL_ALIGNMENT.set(SwingConstants.CENTER);
    ReferentialIntegrityErrorHandling.REFERENTIAL_INTEGRITY_ERROR_HANDLING.set(ReferentialIntegrityErrorHandling.DISPLAY_DEPENDENCIES);
    EntityApplicationPanel.builder(ChinookAppModel.class, ChinookAppPanel.class)
            .applicationName("Chinook")
            .domainType(DOMAIN)
            .applicationVersion(ChinookAppModel.VERSION)
            .defaultLookAndFeelClassName(DEFAULT_FLAT_LOOK_AND_FEEL)
            .defaultLoginUser(User.parse("scott:tiger"))
            .displayStartupDialog(false)
            .start();
  }
}
