/*
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Customer;
import is.codion.framework.demos.chinook.model.ChinookAppModel;
import is.codion.framework.domain.entity.Entity;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.model.tools.loadtest.AbstractEntityUsageScenario;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static is.codion.swing.framework.model.tools.loadtest.EntityLoadTestModel.selectRandomRow;

public final class ViewCustomerReport extends AbstractEntityUsageScenario<ChinookAppModel> {

  @Override
  protected void perform(ChinookAppModel application) throws Exception {
    SwingEntityTableModel customerModel = application.entityModel(Customer.TYPE).tableModel();
    customerModel.refresh();
    selectRandomRow(customerModel);

    Collection<Long> customerIDs =
            Entity.distinct(Customer.ID, customerModel.selectionModel().getSelectedItems());
    Map<String, Object> reportParameters = new HashMap<>();
    reportParameters.put("CUSTOMER_IDS", customerIDs);
    customerModel.connectionProvider().connection().fillReport(Customer.REPORT, reportParameters);
  }

  @Override
  public int defaultWeight() {
    return 2;
  }
}
