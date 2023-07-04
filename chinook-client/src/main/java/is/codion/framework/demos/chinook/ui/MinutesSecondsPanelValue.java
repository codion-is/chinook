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

import is.codion.swing.common.ui.component.text.NumberField;
import is.codion.swing.common.ui.component.value.AbstractComponentValue;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ResourceBundle;

import static is.codion.framework.demos.chinook.domain.api.Chinook.*;
import static is.codion.swing.common.ui.component.Components.integerField;
import static is.codion.swing.common.ui.component.Components.panel;
import static is.codion.swing.common.ui.layout.Layouts.borderLayout;
import static is.codion.swing.common.ui.layout.Layouts.gridLayout;

final class MinutesSecondsPanelValue extends AbstractComponentValue<Integer, MinutesSecondsPanelValue.MinutesSecondsPanel> {

  MinutesSecondsPanelValue() {
    this(false);
  }

  MinutesSecondsPanelValue(boolean horizontal) {
    super(new MinutesSecondsPanel(horizontal));
    component().minutesField.addValueListener(minutes -> notifyValueChange());
    component().secondsField.addValueListener(seconds -> notifyValueChange());
  }

  @Override
  protected Integer getComponentValue() {
    return milliseconds(component().minutesField.getNumber(), component().secondsField.getNumber());
  }

  @Override
  protected void setComponentValue(Integer milliseconds) {
    component().minutesField.setNumber(minutes(milliseconds));
    component().secondsField.setNumber(seconds(milliseconds));
  }

  static final class MinutesSecondsPanel extends JPanel {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MinutesSecondsPanel.class.getName());

    private final NumberField<Integer> minutesField = integerField()
            .transferFocusOnEnter(true)
            .selectAllOnFocusGained(true)
            .columns(2)
            .build();
    private final NumberField<Integer> secondsField = integerField()
            .valueRange(0, 59)
            .transferFocusOnEnter(true)
            .selectAllOnFocusGained(true)
            .columns(2)
            .build();

    private MinutesSecondsPanel(boolean horizontal) {
      super(borderLayout());
      if (horizontal) {
        panel(gridLayout(1, 4))
                .add(new JLabel(BUNDLE.getString("min")))
                .add(minutesField)
                .add(new JLabel(BUNDLE.getString("sec")))
                .add(secondsField)
                .build(panel -> add(panel, BorderLayout.CENTER));
      }
      else {
        panel(gridLayout(1, 2))
                .add(new JLabel(BUNDLE.getString("min")))
                .add(new JLabel(BUNDLE.getString("sec")))
                .build(panel -> add(panel, BorderLayout.NORTH));
        panel(gridLayout(1, 2))
                .add(minutesField)
                .add(secondsField)
                .build(panel -> add(panel, BorderLayout.CENTER));
      }
    }
  }
}
