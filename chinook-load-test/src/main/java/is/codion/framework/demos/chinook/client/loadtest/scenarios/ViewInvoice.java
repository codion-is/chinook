package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Customer;
import is.codion.framework.demos.chinook.domain.api.Chinook.Invoice;
import is.codion.framework.demos.chinook.model.ChinookAppModel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.model.tools.loadtest.AbstractEntityUsageScenario;

import static is.codion.swing.framework.model.tools.loadtest.EntityLoadTestModel.selectRandomRow;

public final class ViewInvoice extends AbstractEntityUsageScenario<ChinookAppModel> {

  @Override
  protected void perform(ChinookAppModel application) throws Exception {
    SwingEntityModel customerModel = application.entityModel(Customer.TYPE);
    customerModel.tableModel().refresh();
    selectRandomRow(customerModel.tableModel());
    SwingEntityModel invoiceModel = customerModel.detailModel(Invoice.TYPE);
    selectRandomRow(invoiceModel.tableModel());
  }

  @Override
  public int defaultWeight() {
    return 10;
  }
}
