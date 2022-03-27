/*
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.chinook.ui;

import is.codion.common.model.CancelException;
import is.codion.common.model.FilteredModel;
import is.codion.common.model.UserPreferences;
import is.codion.common.model.table.ColumnConditionModel;
import is.codion.common.user.User;
import is.codion.common.version.Version;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.demos.chinook.model.ChinookApplicationModel;
import is.codion.framework.demos.chinook.model.EmployeeTableModel;
import is.codion.framework.model.EntityEditModel;
import is.codion.swing.common.ui.Utilities;
import is.codion.swing.common.ui.combobox.Completion;
import is.codion.swing.common.ui.control.Control;
import is.codion.swing.common.ui.control.Controls;
import is.codion.swing.common.ui.laf.LookAndFeelProvider;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.ui.EntityApplicationPanel;
import is.codion.swing.framework.ui.EntityPanel;
import is.codion.swing.framework.ui.EntityTableCellRenderer;
import is.codion.swing.framework.ui.EntityTablePanel;
import is.codion.swing.framework.ui.ReferentialIntegrityErrorHandling;

import org.pushingpixels.radiance.theming.api.RadianceLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteAquaLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteChalkLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteElectricLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteGlassLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteGoldLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteSiennaLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceGraphiteSunsetLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceMagellanLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceNightShadeLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceRavenLookAndFeel;
import org.pushingpixels.radiance.theming.api.skin.RadianceTwilightLookAndFeel;
import org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceFieldOfWheatLookAndFeel;
import org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceFindingNemoLookAndFeel;
import org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceHarvestLookAndFeel;
import org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceMagmaLookAndFeel;
import org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceMangoLookAndFeel;
import org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceStreetlightsLookAndFeel;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static is.codion.framework.demos.chinook.domain.api.Chinook.*;
import static is.codion.swing.common.ui.laf.LookAndFeelProvider.addLookAndFeelProvider;
import static javax.swing.JOptionPane.showMessageDialog;

public final class ChinookAppPanel extends EntityApplicationPanel<ChinookApplicationModel> {

  private static final String LANGUAGE_PREFERENCES_KEY = ChinookAppPanel.class.getSimpleName() + ".language";
  private static final Locale LOCALE_IS = new Locale("is", "IS");
  private static final Locale LOCALE_EN = new Locale("en", "EN");
  private static final String LANGUAGE_IS = "is";
  private static final String LANGUAGE_EN = "en";

  private static final String SELECT_LANGUAGE = "select_language";

  /* Non-static so this is not initialized before main(), which sets the locale */
  private final ResourceBundle bundle = ResourceBundle.getBundle(ChinookAppPanel.class.getName());

  public ChinookAppPanel() {
    super("Chinook");
  }

  @Override
  protected List<EntityPanel.Builder> initializeSupportEntityPanelBuilders(ChinookApplicationModel applicationModel) {
    EntityPanel.Builder trackBuilder =
            EntityPanel.builder(SwingEntityModel.builder(Track.TYPE))
                    .tablePanelClass(TrackTablePanel.class);

    EntityPanel.Builder customerBuilder =
            EntityPanel.builder(SwingEntityModel.builder(Customer.TYPE))
                    .editPanelClass(CustomerEditPanel.class)
                    .tablePanelClass(CustomerTablePanel.class);

    EntityPanel.Builder genreBuilder =
            EntityPanel.builder(SwingEntityModel.builder(Genre.TYPE)
                    .detailModelBuilder(SwingEntityModel.builder(Track.TYPE)))
                    .editPanelClass(GenreEditPanel.class)
                    .detailPanelBuilder(trackBuilder)
                    .detailPanelState(EntityPanel.PanelState.HIDDEN);

    EntityPanel.Builder mediaTypeBuilder =
            EntityPanel.builder(SwingEntityModel.builder(MediaType.TYPE)
                    .detailModelBuilder(SwingEntityModel.builder(Track.TYPE)))
                    .editPanelClass(MediaTypeEditPanel.class)
                    .detailPanelBuilder(trackBuilder)
                    .detailPanelState(EntityPanel.PanelState.HIDDEN);

    EntityPanel.Builder employeeBuilder =
            EntityPanel.builder(SwingEntityModel.builder(Employee.TYPE)
                    .detailModelBuilder(SwingEntityModel.builder(Customer.TYPE))
                    .tableModelClass(EmployeeTableModel.class))
                    .editPanelClass(EmployeeEditPanel.class)
                    .tablePanelClass(EmployeeTablePanel.class)
                    .detailPanelBuilder(customerBuilder)
                    .detailPanelState(EntityPanel.PanelState.HIDDEN)
                    .panelInitializer(panel -> panel.setPreferredSize(new Dimension(1000, 500)));

    return List.of(genreBuilder, mediaTypeBuilder, employeeBuilder);
  }

  @Override
  protected List<EntityPanel> initializeEntityPanels(ChinookApplicationModel applicationModel) {
    return List.of(
            new CustomerPanel(applicationModel.getEntityModel(Customer.TYPE)),
            new ArtistPanel(applicationModel.getEntityModel(Artist.TYPE)),
            new PlaylistPanel(applicationModel.getEntityModel(Playlist.TYPE))
    );
  }

  @Override
  protected ChinookApplicationModel initializeApplicationModel(EntityConnectionProvider connectionProvider) throws CancelException {
    return new ChinookApplicationModel(connectionProvider);
  }

  @Override
  protected Version getClientVersion() {
    return Version.version(0, 1, 0);
  }

  @Override
  protected Controls getViewControls() {
    return super.getViewControls()
            .addSeparator()
            .add(Control.builder(this::selectLanguage)
                    .caption(bundle.getString(SELECT_LANGUAGE))
                    .build());
  }

  private void selectLanguage() {
    String language = UserPreferences.getUserPreference(LANGUAGE_PREFERENCES_KEY, Locale.getDefault().getLanguage());
    JRadioButton enButton = new JRadioButton("English");
    JRadioButton isButton = new JRadioButton("\u00cdslenska", language.equals(LANGUAGE_IS));
    ButtonGroup langButtonGroup = new ButtonGroup();
    langButtonGroup.add(enButton);
    langButtonGroup.add(isButton);
    JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    buttonPanel.add(enButton);
    buttonPanel.add(isButton);
    enButton.setSelected(language.equals(LANGUAGE_EN));
    isButton.setSelected(language.equals(LANGUAGE_IS));
    showMessageDialog(this, buttonPanel, "Language/Tungum\u00e1l", JOptionPane.QUESTION_MESSAGE);
    String newLanguage = isButton.isSelected() ? LANGUAGE_IS : LANGUAGE_EN;
    if (!language.equals(newLanguage)) {
      UserPreferences.putUserPreference(LANGUAGE_PREFERENCES_KEY, newLanguage);
      showMessageDialog(this,
              """
                      Language has been changed, restart the application to apply the changes.

                      Tungum\u00e1li hefur veri\u00f0 breytt, endurr\u00e6stu kerfi\u00f0 til að virkja breytingarnar.
                      """);
    }
  }

  private static void addRadianceLookAndFeels() {
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceTwilightLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceNightShadeLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceMagellanLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceGraphiteLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceGraphiteChalkLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceGraphiteAquaLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceGraphiteElectricLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceGraphiteGoldLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceGraphiteSiennaLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceGraphiteSunsetLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceGraphiteGlassLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceRavenLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceFieldOfWheatLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceFindingNemoLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceHarvestLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceMagmaLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceMangoLookAndFeel.class));
    addLookAndFeelProvider(new RadianceLookAndFeelProvider(RadianceStreetlightsLookAndFeel.class));
  }

  public static void main(String[] args) throws CancelException {
    String language = UserPreferences.getUserPreference(LANGUAGE_PREFERENCES_KEY, Locale.getDefault().getLanguage());
    Locale.setDefault(LANGUAGE_IS.equals(language) ? LOCALE_IS : LOCALE_EN);
    Utilities.CHANGE_LOOK_AND_FEEL_DURING_SELECTION.set(true);
    addRadianceLookAndFeels();
    Completion.COMBO_BOX_COMPLETION_MODE.set(Completion.Mode.AUTOCOMPLETE);
    FilteredModel.ASYNC_REFRESH.set(true);
    EntityEditModel.POST_EDIT_EVENTS.set(true);
    EntityPanel.TOOLBAR_BUTTONS.set(true);
    EntityTablePanel.TABLE_AUTO_RESIZE_MODE.set(JTable.AUTO_RESIZE_ALL_COLUMNS);
    EntityTablePanel.COLUMN_SELECTION.set(EntityTablePanel.ColumnSelection.MENU);
    EntityTableCellRenderer.NUMERICAL_HORIZONTAL_ALIGNMENT.set(SwingConstants.CENTER);
    EntityTableCellRenderer.TEMPORAL_HORIZONTAL_ALIGNMENT.set(SwingConstants.CENTER);
    ReferentialIntegrityErrorHandling.REFERENTIAL_INTEGRITY_ERROR_HANDLING.set(ReferentialIntegrityErrorHandling.DEPENDENCIES);
    ColumnConditionModel.AUTOMATIC_WILDCARD.set(ColumnConditionModel.AutomaticWildcard.POSTFIX);
    ColumnConditionModel.CASE_SENSITIVE.set(false);
    EntityConnectionProvider.CLIENT_DOMAIN_CLASS.set("is.codion.framework.demos.chinook.domain.ChinookImpl");
    SwingUtilities.invokeLater(() -> new ChinookAppPanel().starter()
            .frameSize(new Dimension(1280, 720))
            .defaultLoginUser(User.parse("scott:tiger"))
            .start());
  }

  private record RadianceLookAndFeelProvider(Class<? extends RadianceLookAndFeel> lookAndFeelClass)
          implements LookAndFeelProvider {

    @Override
    public String getClassName() {
      return lookAndFeelClass.getName();
    }

    @Override
    public void enable() {
      try {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        UIManager.setLookAndFeel(getClassName());
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
