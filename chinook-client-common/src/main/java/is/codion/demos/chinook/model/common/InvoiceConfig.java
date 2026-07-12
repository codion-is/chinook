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
 * Copyright (c) 2026, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.model.common;

import is.codion.demos.chinook.domain.api.Chinook.InvoiceLine;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.model.EntityEditModel;
import is.codion.framework.model.EntityEditor;
import is.codion.framework.model.EntityModel;
import is.codion.framework.model.EntityTableModel;

import java.util.Collection;

public interface InvoiceConfig<M extends EntityModel<M, E, T, R>, E extends EntityEditModel<R>,
				T extends EntityTableModel<E, R>, R extends EntityEditor<R>> extends EntityModel<M, E, T, R> {

	default void configure() {
		R invoiceLineEditor = detail().get(InvoiceLine.TYPE).editModel().editor();
		// We listen for invoice line modifications in order to refresh the
		// associated invoices in the table model to display the updated total.
		invoiceLineEditor.events().persisted().addConsumer(this::onInvoiceLinesModified);
	}

	private void onInvoiceLinesModified(Collection<Entity> invoiceLines) {
		tableModel().refresh(Entity.keys(InvoiceLine.INVOICE_FK, invoiceLines));
	}
}
