/*
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.chinook.ui;

import is.codion.framework.demos.chinook.domain.api.Chinook.InvoiceLine;
import is.codion.swing.common.ui.component.Components;
import is.codion.swing.framework.model.SwingEntityEditModel;
import is.codion.swing.framework.ui.EntityEditPanel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import java.awt.BorderLayout;

import static is.codion.swing.common.ui.component.text.TextComponents.preferredTextFieldHeight;
import static is.codion.swing.common.ui.layout.Layouts.borderLayout;
import static is.codion.swing.common.ui.layout.Layouts.flexibleGridLayout;

public final class InvoiceLineEditPanel extends EntityEditPanel {

  private final JTextField tableSearchField;

  public InvoiceLineEditPanel(SwingEntityEditModel editModel, JTextField tableSearchField) {
    super(editModel);
    this.tableSearchField = tableSearchField;
    editModel.setPersistValue(InvoiceLine.TRACK_FK, false);
  }

  @Override
  protected void initializeUI() {
    setInitialFocusAttribute(InvoiceLine.TRACK_FK);

    createForeignKeySearchField(InvoiceLine.TRACK_FK)
            .selectionProviderFactory(TrackSelectionProvider::new)
            .columns(15);
    createTextField(InvoiceLine.QUANTITY)
            .selectAllOnFocusGained(true)
            .columns(2)
            .action(getControl(ControlCode.INSERT));

    JToolBar updateToolBar = Components.toolBar()
            .floatable(false)
            .action(getControl(ControlCode.UPDATE))
            .preferredHeight(preferredTextFieldHeight())
            .build();

    JPanel eastPanel = Components.panel(flexibleGridLayout(1, 2))
            .add(createInputPanel(new JLabel(" "), updateToolBar))
            .add(createInputPanel(new JLabel(" "), tableSearchField))
            .build();

    setLayout(borderLayout());
    addInputPanel(InvoiceLine.TRACK_FK, BorderLayout.WEST);
    addInputPanel(InvoiceLine.QUANTITY, BorderLayout.CENTER);
    add(eastPanel, BorderLayout.EAST);
  }
}