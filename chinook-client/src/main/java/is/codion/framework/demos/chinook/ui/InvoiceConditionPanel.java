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
 * Copyright (c) 2024, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.ui;

import is.codion.common.Operator;
import is.codion.common.item.Item;
import is.codion.common.model.condition.ColumnConditions;
import is.codion.common.model.condition.ConditionModel;
import is.codion.framework.demos.chinook.domain.api.Chinook.Invoice;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.EntityDefinition;
import is.codion.framework.domain.entity.attribute.Attribute;
import is.codion.framework.model.ForeignKeyConditionModel;
import is.codion.swing.common.ui.component.Components;
import is.codion.swing.common.ui.component.table.ColumnConditionPanel;
import is.codion.swing.common.ui.component.table.ColumnConditionPanel.ConditionState;
import is.codion.swing.common.ui.component.table.ColumnConditionsPanel;
import is.codion.swing.common.ui.component.table.FilterColumnConditionPanel;
import is.codion.swing.common.ui.component.table.FilterColumnConditionPanel.FieldFactory;
import is.codion.swing.common.ui.component.table.FilterColumnConditionsPanel;
import is.codion.swing.common.ui.component.table.FilterTableColumnModel;
import is.codion.swing.common.ui.component.text.NumberField;
import is.codion.swing.common.ui.component.value.ComponentValue;
import is.codion.swing.common.ui.control.Controls;
import is.codion.swing.common.ui.key.KeyEvents;
import is.codion.swing.framework.ui.EntityConditionFieldFactory;
import is.codion.swing.framework.ui.component.EntitySearchField;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static is.codion.swing.common.ui.component.Components.borderLayoutPanel;
import static is.codion.swing.common.ui.component.Components.flexibleGridLayoutPanel;
import static is.codion.swing.common.ui.component.table.ColumnConditionPanel.ConditionState.ADVANCED;
import static is.codion.swing.common.ui.component.table.FilterColumnConditionsPanel.filterColumnConditionsPanel;
import static is.codion.swing.common.ui.control.Control.command;
import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.util.Arrays.asList;
import static java.util.ResourceBundle.getBundle;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.border.TitledBorder.CENTER;
import static javax.swing.border.TitledBorder.DEFAULT_POSITION;

final class InvoiceConditionPanel extends ColumnConditionsPanel<Attribute<?>> {

	private static final ResourceBundle BUNDLE = getBundle(InvoiceConditionPanel.class.getName());

	private final FilterTableColumnModel<Attribute<?>> columnModel;
	private final FilterColumnConditionsPanel<Attribute<?>> advancedConditionPanel;
	private final SimpleConditionPanel simpleConditionPanel;

	InvoiceConditionPanel(EntityDefinition invoiceDefinition,
												ColumnConditions<Attribute<?>> ColumnConditions,
												FilterTableColumnModel<Attribute<?>> columnModel,
												Consumer<ColumnConditionsPanel<Attribute<?>>> onPanelInitialized,
												Runnable onDateChanged) {
		super(ColumnConditions);
		setLayout(new BorderLayout());
		this.columnModel = columnModel;
		this.simpleConditionPanel = new SimpleConditionPanel(ColumnConditions, invoiceDefinition, onDateChanged);
		this.advancedConditionPanel = filterColumnConditionsPanel(ColumnConditions,
						createConditionPanels(new EntityConditionFieldFactory(invoiceDefinition)),
						columnModel, onPanelInitialized);
		state().link(advancedConditionPanel.state());
	}

	@Override
	public Collection<ColumnConditionPanel<Attribute<?>, ?>> conditionPanels() {
		Collection<ColumnConditionPanel<Attribute<?>, ?>> conditionPanels =
						new ArrayList<>(advancedConditionPanel.conditionPanels());
		conditionPanels.addAll(simpleConditionPanel.conditionPanels());

		return conditionPanels;
	}

	@Override
	public Collection<ColumnConditionPanel<Attribute<?>, ?>> selectableConditionPanels() {
		return state().isEqualTo(ADVANCED) ? advancedConditionPanel.selectableConditionPanels() : simpleConditionPanel.conditionPanels();
	}

	@Override
	public <T extends ColumnConditionPanel<Attribute<?>, ?>> T conditionPanel(Attribute<?> attribute) {
		if (state().isNotEqualTo(ADVANCED)) {
			return (T) simpleConditionPanel.conditionPanels().stream()
							.filter(panel -> panel.identifier().equals(attribute))
							.findFirst()
							.orElseThrow(IllegalArgumentException::new);
		}

		return (T) advancedConditionPanel.conditionPanels().stream()
						.filter(panel -> panel.identifier().equals(attribute))
						.findFirst()
						.orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public Controls controls() {
		return advancedConditionPanel.controls();
	}

	@Override
	protected void onStateChanged(ConditionState conditionState) {
		removeAll();
		switch (conditionState) {
			case SIMPLE:
				add(simpleConditionPanel, BorderLayout.CENTER);
				simpleConditionPanel.activate();
				break;
			case ADVANCED:
				add(advancedConditionPanel, BorderLayout.CENTER);
				if (simpleConditionPanel.customerConditionPanel.hasInputFocus()) {
					advancedConditionPanel.conditionPanel(Invoice.CUSTOMER_FK).requestInputFocus();
				}
				else if (simpleConditionPanel.dateConditionPanel.hasInputFocus()) {
					advancedConditionPanel.conditionPanel(Invoice.DATE).requestInputFocus();
				}
				break;
			default:
				break;
		}
		revalidate();
	}

	private Collection<ColumnConditionPanel<Attribute<?>, ?>> createConditionPanels(FieldFactory<Attribute<?>> fieldFactory) {
		Collection<ColumnConditionPanel<Attribute<?>, ?>> conditionPanels = new ArrayList<>();
		conditions().get().entrySet().stream()
						.filter(entry -> columnModel.containsColumn(entry.getKey()))
						.filter(entry -> fieldFactory.supportsType(entry.getValue().valueClass()))
						.forEach(entry -> conditionPanels.add(createColumnConditionPanel(entry.getValue(), entry.getKey(), fieldFactory)));

		return conditionPanels;
	}

	private <C extends Attribute<?>> FilterColumnConditionPanel<C, ?> createColumnConditionPanel(ConditionModel<?> condition, C identifier,
																																															 FieldFactory<C> fieldFactory) {
		return FilterColumnConditionPanel.builder(condition, identifier)
						.fieldFactory(fieldFactory)
						.tableColumn(columnModel.column(identifier))
						.caption(columnModel.column(identifier).getHeaderValue().toString())
						.build();
	}

	private static final class SimpleConditionPanel extends JPanel {

		private final CustomerConditionPanel customerConditionPanel;
		private final DateConditionPanel dateConditionPanel;

		private SimpleConditionPanel(ColumnConditions<Attribute<?>> ColumnConditions,
																 EntityDefinition invoiceDefinition, Runnable onDateChanged) {
			super(new BorderLayout());
			setBorder(createEmptyBorder(5, 5, 5, 5));
			customerConditionPanel = new CustomerConditionPanel(ColumnConditions.get(Invoice.CUSTOMER_FK), invoiceDefinition);
			dateConditionPanel = new DateConditionPanel(ColumnConditions.get(Invoice.DATE), invoiceDefinition);
			dateConditionPanel.yearValue.addListener(onDateChanged);
			dateConditionPanel.monthValue.addListener(onDateChanged);
			initializeUI();
		}

		private void initializeUI() {
			add(borderLayoutPanel()
							.westComponent(borderLayoutPanel()
											.westComponent(customerConditionPanel)
											.centerComponent(dateConditionPanel)
											.build())
							.build(), BorderLayout.CENTER);
		}

		private Collection<ColumnConditionPanel<Attribute<?>, ?>> conditionPanels() {
			return asList(customerConditionPanel, dateConditionPanel);
		}

		private void activate() {
			customerConditionPanel.condition().operator().set(Operator.IN);
			dateConditionPanel.condition().operator().set(Operator.BETWEEN);
			customerConditionPanel.requestInputFocus();
		}

		private static final class CustomerConditionPanel extends ColumnConditionPanel<Attribute<?>, Entity> {

			private final EntitySearchField searchField;

			private CustomerConditionPanel(ConditionModel<Entity> condition, EntityDefinition invoiceDefinition) {
				super(condition, Invoice.CUSTOMER_FK, invoiceDefinition.attributes().definition(Invoice.CUSTOMER_FK).caption());
				setLayout(new BorderLayout());
				setBorder(createTitledBorder(createEmptyBorder(), caption()));
				ForeignKeyConditionModel foreignKeyCondition = (ForeignKeyConditionModel) condition;
				foreignKeyCondition.operands().in().value().link(foreignKeyCondition.operands().equal());
				searchField = EntitySearchField.builder(foreignKeyCondition.inSearchModel())
								.columns(25)
								.build();
				add(searchField, BorderLayout.CENTER);
			}

			@Override
			public Collection<JComponent> components() {
				return List.of(searchField);
			}

			@Override
			public void requestInputFocus() {
				searchField.requestFocusInWindow();
			}

			@Override
			protected void onStateChanged(ConditionState state) {}

			private boolean hasInputFocus() {
				return searchField.hasFocus();
			}
		}

		private static final class DateConditionPanel extends ColumnConditionPanel<Attribute<?>, LocalDate> {

			private final ComponentValue<Integer, NumberField<Integer>> yearValue = Components.integerField()
							.value(LocalDate.now().getYear())
							.listener(this::updateCondition)
							.focusable(false)
							.columns(4)
							.horizontalAlignment(SwingConstants.CENTER)
							.buildValue();
			private final ComponentValue<Month, JSpinner> monthValue = Components.<Month>itemSpinner(new MonthSpinnerModel())
							.listener(this::updateCondition)
							.editable(false)
							.columns(3)
							.horizontalAlignment(SwingConstants.LEFT)
							.keyEvent(KeyEvents.builder(KeyEvent.VK_UP)
											.modifiers(InputEvent.CTRL_DOWN_MASK)
											.action(command(this::incrementYear)))
							.keyEvent(KeyEvents.builder(KeyEvent.VK_DOWN)
											.modifiers(InputEvent.CTRL_DOWN_MASK)
											.action(command(this::decrementYear)))
							.buildValue();

			private DateConditionPanel(ConditionModel<LocalDate> conditionModel, EntityDefinition invoiceDefinition) {
				super(conditionModel, Invoice.DATE, invoiceDefinition.attributes().definition(Invoice.DATE).caption());
				setLayout(new BorderLayout());
				condition().operator().set(Operator.BETWEEN);
				updateCondition();
				initializeUI();
			}

			@Override
			protected void onStateChanged(ConditionState state) {}

			private void initializeUI() {
				setLayout(new BorderLayout());
				add(flexibleGridLayoutPanel(1, 2)
								.add(borderLayoutPanel()
												.centerComponent(yearValue.component())
												.border(createTitledBorder(createEmptyBorder(),
																BUNDLE.getString("year"), CENTER, DEFAULT_POSITION))
												.build())
								.add(borderLayoutPanel()
												.centerComponent(monthValue.component())
												.border(createTitledBorder(createEmptyBorder(),
																BUNDLE.getString("month")))
												.build())
								.build(), BorderLayout.CENTER);
			}

			private void incrementYear() {
				yearValue.map(year -> year + 1);
			}

			private void decrementYear() {
				yearValue.map(year -> year - 1);
			}

			@Override
			public Collection<JComponent> components() {
				return asList(yearValue.component(), monthValue.component());
			}

			@Override
			public void requestInputFocus() {
				monthValue.component().requestFocusInWindow();
			}

			private void updateCondition() {
				condition().operands().lowerBound().set(lowerBound());
				condition().operands().upperBound().set(upperBound());
			}

			private LocalDate lowerBound() {
				int year = yearValue.optional().orElse(LocalDate.now().getYear());
				Month month = monthValue.optional().orElse(JANUARY);

				return LocalDate.of(year, month, 1);
			}

			private LocalDate upperBound() {
				int year = yearValue.optional().orElse(LocalDate.now().getYear());
				Month month = monthValue.optional().orElse(DECEMBER);
				YearMonth yearMonth = YearMonth.of(year, month);

				return LocalDate.of(year, month, yearMonth.lengthOfMonth());
			}

			private boolean hasInputFocus() {
				return yearValue.component().hasFocus() || monthValue.component().hasFocus();
			}

			private static final class MonthSpinnerModel extends SpinnerListModel {

				private MonthSpinnerModel() {
					super(createMonthsList());
				}

				private static List<Item<Month>> createMonthsList() {
					return Stream.concat(Stream.of(Item.<Month>item(null, "")), Arrays.stream(Month.values())
													.map(month -> Item.item(month, month.getDisplayName(TextStyle.SHORT, Locale.getDefault()))))
									.toList();
				}
			}
		}
	}
}
