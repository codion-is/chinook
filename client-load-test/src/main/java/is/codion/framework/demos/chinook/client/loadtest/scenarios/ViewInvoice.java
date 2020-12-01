package is.codion.framework.demos.chinook.client.loadtest.scenarios;

import is.codion.framework.demos.chinook.domain.api.Chinook.Customer;
import is.codion.framework.demos.chinook.domain.api.Chinook.Invoice;
import is.codion.framework.demos.chinook.model.ChinookApplicationModel;
import is.codion.swing.common.tools.loadtest.ScenarioException;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.tools.loadtest.EntityLoadTestModel;

import static is.codion.swing.framework.tools.loadtest.EntityLoadTestModel.selectRandomRow;

public final class ViewInvoice extends EntityLoadTestModel.AbstractEntityUsageScenario<ChinookApplicationModel> {

  @Override
  protected void perform(final ChinookApplicationModel application) throws ScenarioException {
    try {
      final SwingEntityModel customerModel = application.getEntityModel(Customer.TYPE);
      customerModel.getTableModel().refresh();
      selectRandomRow(customerModel.getTableModel());
      final SwingEntityModel invoiceModel = customerModel.getDetailModel(Invoice.TYPE);
      selectRandomRow(invoiceModel.getTableModel());
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
