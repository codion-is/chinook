package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Album;
import is.codion.framework.demos.chinook.domain.api.Chinook.Artist;
import is.codion.framework.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.demos.chinook.model.ChinookApplicationModel;
import is.codion.framework.demos.chinook.model.TrackTableModel;
import is.codion.swing.common.tools.loadtest.ScenarioException;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.tools.loadtest.EntityLoadTestModel;

import java.math.BigDecimal;

import static is.codion.swing.framework.tools.loadtest.EntityLoadTestModel.selectRandomRows;

public final class RaisePrices extends EntityLoadTestModel.AbstractEntityUsageScenario<ChinookApplicationModel> {

  @Override
  protected void perform(final ChinookApplicationModel application) throws ScenarioException {
    try {
      final SwingEntityModel artistModel = application.getEntityModel(Artist.TYPE);
      artistModel.getTableModel().refresh();
      selectRandomRows(artistModel.getTableModel(), 2);
      final SwingEntityModel albumModel = artistModel.getDetailModel(Album.TYPE);
      selectRandomRows(albumModel.getTableModel(), 0.5);
      final TrackTableModel trackTableModel =
              (TrackTableModel) albumModel.getDetailModel(Track.TYPE).getTableModel();
      selectRandomRows(trackTableModel, 4);
      trackTableModel.raisePriceOfSelected(BigDecimal.valueOf(0.01));
    }
    catch (final Exception e) {
      throw new ScenarioException(e);
    }
  }
}
