package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Album;
import is.codion.framework.demos.chinook.domain.api.Chinook.Artist;
import is.codion.framework.demos.chinook.model.ChinookApplicationModel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.tools.loadtest.AbstractEntityUsageScenario;

import static is.codion.swing.framework.tools.loadtest.EntityLoadTestModel.selectRandomRow;

public final class ViewAlbum extends AbstractEntityUsageScenario<ChinookApplicationModel> {

  @Override
  protected void perform(final ChinookApplicationModel application) throws Exception {
    final SwingEntityModel artistModel = application.getEntityModel(Artist.TYPE);
    artistModel.getTableModel().refresh();
    selectRandomRow(artistModel.getTableModel());
    final SwingEntityModel albumModel = artistModel.getDetailModel(Album.TYPE);
    selectRandomRow(albumModel.getTableModel());
  }

  @Override
  public int getDefaultWeight() {
    return 10;
  }
}
