/*
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.chinook.ui;

import is.codion.framework.demos.chinook.domain.api.Chinook.Invoice;
import is.codion.framework.demos.chinook.domain.api.Chinook.InvoiceLine;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.ui.EntityPanel;

public final class CustomerPanel extends EntityPanel {

  public CustomerPanel(final SwingEntityModel customerModel) {
    super(customerModel, new CustomerEditPanel(customerModel.getEditModel()), new CustomerTablePanel(customerModel.getTableModel()));

    final SwingEntityModel invoiceModel = customerModel.getDetailModel(Invoice.TYPE);
    final EntityPanel invoicePanel = new EntityPanel(invoiceModel, new InvoiceEditPanel(invoiceModel.getEditModel()));
    invoicePanel.setIncludeDetailPanelTabPane(false);
    invoicePanel.setShowDetailPanelControls(false);

    final SwingEntityModel invoiceLineModel = invoiceModel.getDetailModel(InvoiceLine.TYPE);
    final InvoiceLineTablePanel invoiceLineTablePanel = new InvoiceLineTablePanel(invoiceLineModel.getTableModel());
    final InvoiceLineEditPanel invoiceLineEditPanel = new InvoiceLineEditPanel(invoiceLineModel.getEditModel(),
            invoiceLineTablePanel.getTable().getSearchField());

    final EntityPanel invoiceLinePanel = new EntityPanel(invoiceLineModel, invoiceLineEditPanel, invoiceLineTablePanel);
    invoiceLinePanel.setIncludeControlPanel(false);
    invoiceLinePanel.initializePanel();
    ((InvoiceEditPanel) invoicePanel.getEditPanel()).setInvoiceLinePanel(invoiceLinePanel);

    invoicePanel.addDetailPanel(invoiceLinePanel);

    addDetailPanel(invoicePanel);
  }
}