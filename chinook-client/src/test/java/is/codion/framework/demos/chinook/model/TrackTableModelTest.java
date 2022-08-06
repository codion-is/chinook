/*
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.chinook.model;

import is.codion.common.db.exception.DatabaseException;
import is.codion.common.model.table.ColumnConditionModel;
import is.codion.common.user.User;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.demos.chinook.domain.api.Chinook.Album;
import is.codion.framework.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.domain.entity.Entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TrackTableModelTest {

  @Test
  public void raisePriceOfSelected() throws DatabaseException {
    try (EntityConnectionProvider connectionProvider = createConnectionProvider()) {
      Entity masterOfPuppets = connectionProvider.connection()
              .selectSingle(Album.TITLE, "Master Of Puppets");

      TrackTableModel trackTableModel = new TrackTableModel(connectionProvider);
      ColumnConditionModel<?, Entity> albumConditionModel =
              trackTableModel.tableConditionModel().conditionModel(Track.ALBUM_FK);

      albumConditionModel.setEqualValue(masterOfPuppets);

      trackTableModel.refresh();
      assertEquals(8, trackTableModel.getRowCount());

      trackTableModel.selectionModel().selectAll();
      trackTableModel.raisePriceOfSelected(BigDecimal.ONE);

      trackTableModel.items().forEach(track ->
              assertEquals(BigDecimal.valueOf(1.99), track.get(Track.UNITPRICE)));
    }
  }

  private EntityConnectionProvider createConnectionProvider() {
    return EntityConnectionProvider.builder()
            .domainClassName("is.codion.framework.demos.chinook.domain.ChinookImpl")
            .user(User.parse("scott:tiger"))
            .build();
  }
}
