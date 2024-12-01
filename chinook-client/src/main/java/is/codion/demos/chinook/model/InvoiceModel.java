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
 * along with Codion Chinook Demo.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2004 - 2022, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.model;

import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.model.ForeignKeyDetailModelLink;
import is.codion.swing.framework.model.SwingEntityModel;

import javax.swing.SwingUtilities;

public final class InvoiceModel extends SwingEntityModel {

	public InvoiceModel(EntityConnectionProvider connectionProvider) {
		super(new InvoiceEditModel(connectionProvider));

		InvoiceLineEditModel invoiceLineEditModel = new InvoiceLineEditModel(connectionProvider);

		SwingEntityModel invoiceLineModel = new SwingEntityModel(invoiceLineEditModel);
		ForeignKeyDetailModelLink<?, ?, ?> detailModelLink = addDetailModel(invoiceLineModel);
		// Prevents accidentally adding a new invoice line to the previously selected invoice,
		// since the selected foreign key value persists when the master selection is cleared by default.
		detailModelLink.clearForeignKeyValueOnEmptySelection().set(true);
		// Usually the UI is responsible for activating the detail model link for the currently
		// active (or visible) detail panel, but since the InvoiceLine panel is embedded in the
		// InvoiceEditPanel, we simply activate the link here.
		detailModelLink.active().set(true);

		// We listen for when the edit model updates the totals for one or more
		// invoices, and replace those in the table model, with the updated ones,
		// which are provided by the event. Note the use of invokeLater() since
		// the event is triggered during update, which happens in a background
		// thread, and we are thereby updating the table data off the Event Dispatch Thread.
		invoiceLineEditModel.addTotalsUpdatedConsumer(updatedInvoices ->
						SwingUtilities.invokeLater(() -> tableModel().replace(updatedInvoices)));
	}
}