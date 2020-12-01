package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Genre;
import is.codion.framework.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.demos.chinook.model.ChinookApplicationModel;
import is.codion.swing.common.tools.loadtest.ScenarioException;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.tools.loadtest.EntityLoadTestModel;

import static is.codion.swing.framework.tools.loadtest.EntityLoadTestModel.selectRandomRow;
import static is.codion.swing.framework.tools.loadtest.EntityLoadTestModel.selectRandomRows;

public final class ViewGenre extends EntityLoadTestModel.AbstractEntityUsageScenario<ChinookApplicationModel> {

  @Override
  protected void perform(final ChinookApplicationModel application) throws ScenarioException {
    try {
      final SwingEntityModel genreModel = application.getEntityModel(Genre.TYPE);
      genreModel.getTableModel().refresh();
      selectRandomRow(genreModel.getTableModel());
      final SwingEntityModel trackModel = genreModel.getDetailModel(Track.TYPE);
      selectRandomRows(trackModel.getTableModel(), 2);
      genreModel.getConnectionProvider().getConnection().selectDependencies(trackModel.getTableModel().getSelectionModel().getSelectedItems());
    }
    catch (final Exception e) {
      throw new ScenarioException(e);
    }
  }

  @Override
  public int getDefaultWeight() {
    return 10;
  }
}
