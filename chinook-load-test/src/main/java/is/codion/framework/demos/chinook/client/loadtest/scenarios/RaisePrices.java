package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Album;
import is.codion.framework.demos.chinook.domain.api.Chinook.Artist;
import is.codion.framework.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.demos.chinook.model.ChinookAppModel;
import is.codion.framework.demos.chinook.model.TrackTableModel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.tools.loadtest.AbstractEntityUsageScenario;

import java.math.BigDecimal;

import static is.codion.swing.framework.tools.loadtest.EntityLoadTestModel.selectRandomRows;

public final class RaisePrices extends AbstractEntityUsageScenario<ChinookAppModel> {

  @Override
  protected void perform(ChinookAppModel application) throws Exception {
    SwingEntityModel artistModel = application.entityModel(Artist.TYPE);
    artistModel.tableModel().refresh();
    selectRandomRows(artistModel.tableModel(), 2);
    SwingEntityModel albumModel = artistModel.detailModel(Album.TYPE);
    selectRandomRows(albumModel.tableModel(), 0.5);
    TrackTableModel trackTableModel =
            (TrackTableModel) albumModel.detailModel(Track.TYPE).tableModel();
    selectRandomRows(trackTableModel, 4);
    trackTableModel.raisePriceOfSelected(BigDecimal.valueOf(0.01));
  }
}
