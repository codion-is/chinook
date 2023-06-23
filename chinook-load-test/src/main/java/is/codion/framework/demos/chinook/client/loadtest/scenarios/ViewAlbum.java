package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Album;
import is.codion.framework.demos.chinook.domain.api.Chinook.Artist;
import is.codion.framework.demos.chinook.model.ChinookAppModel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.model.tools.loadtest.AbstractEntityUsageScenario;

import static is.codion.swing.framework.model.tools.loadtest.EntityLoadTestModel.selectRandomRow;

public final class ViewAlbum extends AbstractEntityUsageScenario<ChinookAppModel> {

  @Override
  protected void perform(ChinookAppModel application) throws Exception {
    SwingEntityModel artistModel = application.entityModel(Artist.TYPE);
    artistModel.tableModel().refresh();
    selectRandomRow(artistModel.tableModel());
    SwingEntityModel albumModel = artistModel.detailModel(Album.TYPE);
    selectRandomRow(albumModel.tableModel());
  }

  @Override
  public int defaultWeight() {
    return 10;
  }
}
