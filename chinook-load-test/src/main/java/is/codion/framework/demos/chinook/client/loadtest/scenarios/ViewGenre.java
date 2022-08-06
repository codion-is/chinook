package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Genre;
import is.codion.framework.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.demos.chinook.model.ChinookApplicationModel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.tools.loadtest.AbstractEntityUsageScenario;

import static is.codion.swing.framework.tools.loadtest.EntityLoadTestModel.selectRandomRow;
import static is.codion.swing.framework.tools.loadtest.EntityLoadTestModel.selectRandomRows;

public final class ViewGenre extends AbstractEntityUsageScenario<ChinookApplicationModel> {

  @Override
  protected void perform(ChinookApplicationModel application) throws Exception {
    SwingEntityModel genreModel = application.entityModel(Genre.TYPE);
    genreModel.tableModel().refresh();
    selectRandomRow(genreModel.tableModel());
    SwingEntityModel trackModel = genreModel.detailModel(Track.TYPE);
    selectRandomRows(trackModel.tableModel(), 2);
    genreModel.connectionProvider().connection().selectDependencies(trackModel.tableModel().selectionModel().getSelectedItems());
  }

  @Override
  public int defaultWeight() {
    return 10;
  }
}
