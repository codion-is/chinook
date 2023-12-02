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

import is.codion.framework.demos.chinook.model.EmployeeTableModel;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.ui.EntityTablePanel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import static is.codion.swing.common.ui.component.Components.splitPane;
import static is.codion.swing.framework.ui.EntityTree.entityTree;

public final class EmployeeTablePanel extends EntityTablePanel {

  public EmployeeTablePanel(SwingEntityTableModel tableModel) {
    super(tableModel);
  }

  @Override
  protected void layoutPanel(JComponent tableComponent, JPanel southPanel) {
    super.layoutPanel(splitPane()
            .orientation(JSplitPane.HORIZONTAL_SPLIT)
            .continuousLayout(true)
            .oneTouchExpandable(true)
            .resizeWeight(0.65)
            .leftComponent(tableComponent)
            .rightComponent(new JScrollPane(entityTree(((EmployeeTableModel) tableModel()).treeModel())))
            .build(), southPanel);
  }
}
