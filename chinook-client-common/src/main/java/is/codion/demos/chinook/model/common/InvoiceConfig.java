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
import is.codion.framework.db.EntityConnection;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.model.EntityEditModel;
import is.codion.framework.model.EntityEditor;
import is.codion.framework.model.EntityModel;
import is.codion.framework.model.EntityTableModel;
import is.codion.framework.model.ForeignKeyModelLink;

import java.util.Collection;

public interface InvoiceConfig<M extends EntityModel<M, E, T, R>, E extends EntityEditModel<R>,
				T extends EntityTableModel<E, R>, R extends EntityEditor<R>> extends EntityModel<M, E, T, R> {

	/**
	 * Adds the InvoiceLine detail model and wires the invoice total refresh.
	 * <p>Call once, from the implementing model's constructor. Note that it calls {@link #createInvoiceLineModel(EntityConnection)}
	 * and {@link #invoiceLineLinkActive()}, both overridable and both therefore running while that constructor is still
	 * in flight — so neither may depend on state the implementing class has yet to initialize. Hence the connection
	 * being handed to the factory rather than read from the half-built model.
	 */
	default void configure() {
		M invoiceLineModel = createInvoiceLineModel(connection());

		detail().add(ForeignKeyModelLink.builder()
						.model(invoiceLineModel)
						.foreignKey(InvoiceLine.INVOICE_FK)
						// Prevents accidentally adding a new invoice line to the previously selected invoice,
						// since the selected foreign key value persists when the master selection is cleared by default.
						.clearValueOnEmptySelection(true)
						.active(invoiceLineLinkActive())
						.build());

		R invoiceLineEditor = invoiceLineModel.editModel().editor();
		// We listen for invoice line modifications in order to refresh the
		// associated invoices in the table model to display the updated total.
		invoiceLineEditor.events().persisted().addConsumer(this::onInvoiceLinesModified);
	}

	/**
	 * @param connection the connection the model should use
	 * @return the InvoiceLine detail model, of whichever type this client builds
	 */
	M createInvoiceLineModel(EntityConnection connection);

	/**
	 * Whether the InvoiceLine detail link starts out active.
	 * <p>False by default, which is right wherever the UI owns the link: it activates the link of the detail panel
	 * currently on screen and deactivates it again on the way out, so a detail nobody is looking at does not follow
	 * the master selection around. Override where the InvoiceLine panel is not a detail panel the UI navigates to.
	 * @return true if the link should be active from the start
	 */
	default boolean invoiceLineLinkActive() {
		return false;
	}

	private void onInvoiceLinesModified(Collection<Entity> invoiceLines) {
		tableModel().refresh(Entity.keys(InvoiceLine.INVOICE_FK, invoiceLines));
	}
}
