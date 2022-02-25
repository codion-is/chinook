/*
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Customer;
import is.codion.framework.demos.chinook.model.ChinookApplicationModel;
import is.codion.framework.domain.entity.Entity;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.tools.loadtest.AbstractEntityUsageScenario;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static is.codion.swing.framework.tools.loadtest.EntityLoadTestModel.selectRandomRow;

public final class ViewCustomerReport extends AbstractEntityUsageScenario<ChinookApplicationModel> {

  @Override
  protected void perform(ChinookApplicationModel application) throws Exception {
    SwingEntityTableModel customerModel = application.getEntityModel(Customer.TYPE).getTableModel();
    customerModel.refresh();
    selectRandomRow(customerModel);

    Collection<Long> customerIDs =
            Entity.getDistinct(Customer.ID, customerModel.getSelectionModel().getSelectedItems());
    Map<String, Object> reportParameters = new HashMap<>();
    reportParameters.put("CUSTOMER_IDS", customerIDs);
    customerModel.getConnectionProvider().getConnection().fillReport(Customer.REPORT, reportParameters);
  }

  @Override
  public int getDefaultWeight() {
    return 2;
  }
}
