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
import is.codion.framework.domain.entity.attribute.Attribute;
import is.codion.swing.common.ui.component.table.FilterTableCellEditorFactory;
import is.codion.swing.common.ui.component.table.FilterTableColumn;
import is.codion.swing.common.ui.component.text.NumberField;
import is.codion.swing.common.ui.component.value.ComponentValue;
import is.codion.swing.common.ui.control.Control;
import is.codion.swing.common.ui.dialog.Dialogs;
import is.codion.swing.framework.model.SwingEntityEditModel;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.ui.EntityTablePanel;
import is.codion.swing.framework.ui.component.DefaultEntityComponentFactory;
import is.codion.swing.framework.ui.component.EntityComponents;

import javax.swing.JSpinner;
import javax.swing.table.TableCellEditor;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.ResourceBundle;

import static is.codion.swing.common.ui.component.Components.bigDecimalField;
import static is.codion.swing.common.ui.component.table.FilterTableCellEditor.filterTableCellEditor;
import static is.codion.swing.framework.ui.component.EntityComponents.entityComponents;
import static java.util.ResourceBundle.getBundle;

public final class TrackTablePanel extends EntityTablePanel {

	private static final ResourceBundle BUNDLE = getBundle(TrackTablePanel.class.getName());

	public TrackTablePanel(SwingEntityTableModel tableModel) {
		super(tableModel, config -> config
						.editComponentFactory(Track.RATING, new RatingComponentFactory())
						.editComponentFactory(Track.MILLISECONDS, new MinutesSecondsComponentFactory(false))
						.configureTable(tableBuilder -> tableBuilder
										.cellRendererFactory(new RatingCellRendererFactory(tableModel, Track.RATING))
										.cellEditorFactory(new TrackCellEditorFactory()))
						.includeLimitMenu(true));
		configurePopupMenu(config -> config.clear()
						.control(Control.builder()
										.command(this::raisePriceOfSelected)
										.name(BUNDLE.getString("raise_price") + "...")
										.enabled(tableModel().selectionModel().selectionNotEmpty()))
						.separator()
						.defaults());
	}

	private void raisePriceOfSelected() throws DatabaseException {
		TrackTableModel tableModel = tableModel();
		tableModel.raisePriceOfSelected(getAmountFromUser());
	}

	private BigDecimal getAmountFromUser() {
		ComponentValue<BigDecimal, NumberField<BigDecimal>> amountValue =
						bigDecimalField()
										.nullable(false)
										.minimumValue(0)
										.buildValue();

		return Dialogs.inputDialog(amountValue)
						.owner(this)
						.title(BUNDLE.getString("amount"))
						.validator(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
						.show();
	}

	private static final class RatingComponentFactory
					extends DefaultEntityComponentFactory<Integer, Attribute<Integer>, JSpinner> {

		@Override
		public ComponentValue<Integer, JSpinner> componentValue(Attribute<Integer> attribute,
																														SwingEntityEditModel editModel,
																														Integer initialValue) {
			EntityComponents inputComponents = entityComponents(editModel.entityDefinition());

			return inputComponents.integerSpinner(attribute)
							.initialValue(initialValue)
							.buildValue();
		}
	}

	private static final class MinutesSecondsComponentFactory
					extends DefaultEntityComponentFactory<Integer, Attribute<Integer>, MinutesSecondsPanel> {

		private final boolean horizontal;

		private MinutesSecondsComponentFactory(boolean horizontal) {
			this.horizontal = horizontal;
		}

		@Override
		public ComponentValue<Integer, MinutesSecondsPanel> componentValue(Attribute<Integer> attribute,
																																			 SwingEntityEditModel editModel,
																																			 Integer initialValue) {
			MinutesSecondsPanelValue minutesSecondsPanelValue = new MinutesSecondsPanelValue(horizontal);
			minutesSecondsPanelValue.set(initialValue);

			return minutesSecondsPanelValue;
		}
	}

	private static final class TrackCellEditorFactory
					implements FilterTableCellEditorFactory<Attribute<?>> {

		@Override
		public Optional<TableCellEditor> tableCellEditor(FilterTableColumn<Attribute<?>> column) {
			if (column.identifier().equals(Track.MILLISECONDS)) {
				return Optional.of(filterTableCellEditor(() -> new MinutesSecondsPanelValue(true)));
			}

			return Optional.empty();
		}
	}
}
