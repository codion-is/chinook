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
 * Copyright (c) 2004 - 2020, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.ui;

import is.codion.common.db.exception.DatabaseException;
import is.codion.framework.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.demos.chinook.model.TrackTableModel;
import is.codion.framework.demos.chinook.ui.MinutesSecondsPanelValue.MinutesSecondsPanel;
import is.codion.framework.domain.entity.Attribute;
import is.codion.swing.common.ui.component.Components;
import is.codion.swing.common.ui.component.text.NumberField;
import is.codion.swing.common.ui.component.value.ComponentValue;
import is.codion.swing.common.ui.control.Control;
import is.codion.swing.common.ui.control.Controls;
import is.codion.swing.common.ui.dialog.Dialogs;
import is.codion.swing.framework.model.SwingEntityEditModel;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.ui.DefaultEntityComponentFactory;
import is.codion.swing.framework.ui.EntityTablePanel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public final class TrackTablePanel extends EntityTablePanel {

  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(TrackTablePanel.class.getName());

  public TrackTablePanel(SwingEntityTableModel tableModel) {
    super(tableModel);
    setUpdateSelectedComponentFactory(Track.MILLISECONDS, new MinutesSecondsComponentFactory(false));
    setTableCellEditorComponentFactory(Track.MILLISECONDS, new MinutesSecondsComponentFactory(true));
  }

  @Override
  protected Controls createPopupMenuControls(List<Controls> additionalPopupMenuControls) {
    return super.createPopupMenuControls(additionalPopupMenuControls)
            .addAt(0, Control.builder(this::raisePriceOfSelected)
                    .name(BUNDLE.getString("raise_price") + "...")
                    .enabledState(tableModel().selectionModel().selectionNotEmptyObserver())
                    .build())
            .addSeparatorAt(1);
  }

  private void raisePriceOfSelected() throws DatabaseException {
    TrackTableModel tableModel = tableModel();
    tableModel.raisePriceOfSelected(getAmountFromUser());
  }

  private BigDecimal getAmountFromUser() {
    ComponentValue<BigDecimal, NumberField<BigDecimal>> amountValue =
            Components.bigDecimalField()
                    .buildValue();

    return Dialogs.inputDialog(amountValue)
            .owner(this)
            .title(BUNDLE.getString("amount"))
            .validInputPredicate(Objects::nonNull)
            .show();
  }

  private static final class MinutesSecondsComponentFactory
          extends DefaultEntityComponentFactory<Integer, Attribute<Integer>, MinutesSecondsPanel> {

    private final boolean horizontal;

    private MinutesSecondsComponentFactory(boolean horizontal) {
      this.horizontal = horizontal;
    }

    @Override
    public ComponentValue<Integer, MinutesSecondsPanel> createComponentValue(Attribute<Integer> attribute,
                                                                             SwingEntityEditModel editModel,
                                                                             Integer initialValue) {
      MinutesSecondsPanelValue minutesSecondsPanelValue = new MinutesSecondsPanelValue(horizontal);
      minutesSecondsPanelValue.set(initialValue);

      return minutesSecondsPanelValue;
    }
  }
}
