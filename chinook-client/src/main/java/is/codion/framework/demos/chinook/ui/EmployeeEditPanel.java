/*
 * This file is part of Codion Chinook Demo.
 *
 * Codion Chinook Demo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codion Chinook Demo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codion Chinook Demo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.ui;

import is.codion.framework.demos.chinook.model.EmployeeEditModel;
import is.codion.framework.domain.entity.Entity;
import is.codion.swing.common.model.worker.ProgressWorker.Task;
import is.codion.swing.common.ui.control.Control.Command;
import is.codion.swing.common.ui.dialog.ProgressWorkerDialogBuilder;
import is.codion.swing.framework.model.SwingEntityEditModel;
import is.codion.swing.framework.ui.EntityEditPanel;

import javax.swing.JPanel;
import java.util.ResourceBundle;

import static is.codion.framework.demos.chinook.domain.api.Chinook.Employee;
import static is.codion.swing.common.ui.component.Components.flexibleGridLayoutPanel;
import static is.codion.swing.common.ui.component.Components.gridLayoutPanel;
import static is.codion.swing.common.ui.dialog.Dialogs.progressWorkerDialog;
import static is.codion.swing.common.ui.layout.Layouts.flexibleGridLayout;

public final class EmployeeEditPanel extends EntityEditPanel {

  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(EmployeeEditPanel.class.getName());

  static final String INSERTING = BUNDLE.getString("inserting");
  static final String DELETING = BUNDLE.getString("deleting");
  static final String UPDATING = BUNDLE.getString("updating");

  public EmployeeEditPanel(SwingEntityEditModel editModel) {
    super(editModel);
    defaultTextFieldColumns().set(12);
  }

  @Override
  protected void setupControls() {
    control(EditControl.INSERT).map(insert -> insert.copy(new InsertCommand()).build());
    control(EditControl.UPDATE).map(update -> update.copy(new UpdateCommand()).build());
    control(EditControl.DELETE).map(delete -> delete.copy(new DeleteCommand()).build());
  }

  @Override
  protected void initializeUI() {
    initialFocusAttribute().set(Employee.FIRSTNAME);

    createTextField(Employee.FIRSTNAME)
            .columns(6);
    createTextField(Employee.LASTNAME)
            .columns(6);
    createTemporalFieldPanel(Employee.BIRTHDATE)
            .columns(6);
    createTemporalFieldPanel(Employee.HIREDATE)
            .columns(6);
    createTextField(Employee.TITLE)
            .columns(8);
    createTextField(Employee.ADDRESS);
    createTextField(Employee.CITY)
            .columns(8);
    createTextField(Employee.POSTALCODE)
            .columns(4);
    createTextField(Employee.STATE)
            .columns(4)
            .upperCase(true);
    createTextField(Employee.COUNTRY)
            .columns(8);
    createTextField(Employee.PHONE);
    createTextField(Employee.FAX);
    createTextField(Employee.EMAIL);
    createForeignKeyComboBox(Employee.REPORTSTO_FK)
            .preferredWidth(120);

    JPanel firstLastNamePanel = gridLayoutPanel(1, 2)
            .add(createInputPanel(Employee.FIRSTNAME))
            .add(createInputPanel(Employee.LASTNAME))
            .build();

    JPanel birthHireDatePanel = gridLayoutPanel(1, 2)
            .add(createInputPanel(Employee.BIRTHDATE))
            .add(createInputPanel(Employee.HIREDATE))
            .build();

    JPanel cityPostalCodePanel = flexibleGridLayoutPanel(1, 2)
            .add(createInputPanel(Employee.CITY))
            .add(createInputPanel(Employee.POSTALCODE))
            .build();

    JPanel stateCountryPanel = flexibleGridLayoutPanel(1, 2)
            .add(createInputPanel(Employee.STATE))
            .add(createInputPanel(Employee.COUNTRY))
            .build();

    setLayout(flexibleGridLayout(4, 3));
    add(firstLastNamePanel);
    add(birthHireDatePanel);
    addInputPanel(Employee.TITLE);
    addInputPanel(Employee.ADDRESS);
    add(cityPostalCodePanel);
    add(stateCountryPanel);
    addInputPanel(Employee.PHONE);
    addInputPanel(Employee.FAX);
    addInputPanel(Employee.EMAIL);
    addInputPanel(Employee.REPORTSTO_FK);
  }

  private final class InsertCommand implements Command {

    @Override
    public void execute() {
      EmployeeEditModel editModel = editModel();
      EmployeeEditModel.Insert insert = editModel.createInsert();
      createWorker(insert::execute, INSERTING)
              .onResult(entity -> {
                insert.onResult(entity);
                requestAfterInsertFocus();
              })
              .execute();
    }
  }

  private final class UpdateCommand implements Command {

    @Override
    public void execute() {
      if (confirmUpdate()) {
        EmployeeEditModel editModel = editModel();
        EmployeeEditModel.Update update = editModel.createUpdate();
        createWorker(update::execute, UPDATING)
                .onResult(entity -> {
                  update.onResult(entity);
                  requestAfterUpdateFocus();
                })
                .execute();
      }
    }
  }

  private final class DeleteCommand implements Command {

    @Override
    public void execute() {
      if (confirmDelete()) {
        EmployeeEditModel editModel = editModel();
        EmployeeEditModel.Delete delete = editModel.createDelete();
        createWorker(delete::execute, DELETING)
                .onResult(entity -> {
                  delete.onResult(entity);
                  requestInitialFocus();
                })
                .execute();
      }
    }
  }

  private ProgressWorkerDialogBuilder<Entity, ?> createWorker(Task<Entity> task, String dialogTitle) {
    return progressWorkerDialog(task)
            .title(dialogTitle)
            .owner(EmployeeEditPanel.this)
            .onException(this::onException);
  }
}