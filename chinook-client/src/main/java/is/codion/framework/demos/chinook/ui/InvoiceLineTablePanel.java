/*
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.chinook.ui;

import is.codion.framework.demos.chinook.domain.api.Chinook.InvoiceLine;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.ui.EntityTablePanel;

import javax.swing.JTable;
import java.awt.Dimension;

public final class InvoiceLineTablePanel extends EntityTablePanel {

  public InvoiceLineTablePanel(final SwingEntityTableModel tableModel) {
    super(tableModel, new ChinookComponentValues(InvoiceLine.TRACK_FK));
    setIncludeSouthPanel(false);
    setIncludeConditionPanel(false);
    getTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    setPreferredSize(new Dimension(360, 40));
    getTable().getModel().getColumnModel().hideColumn(InvoiceLine.INVOICE_FK);
  }
}