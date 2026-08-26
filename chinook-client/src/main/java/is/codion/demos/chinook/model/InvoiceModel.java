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
 * Copyright (c) 2004 - 2026, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.model;

import is.codion.demos.chinook.model.common.InvoiceConfig;
import is.codion.framework.db.EntityConnection;
import is.codion.swing.framework.model.SwingEntityEditModel;
import is.codion.swing.framework.model.SwingEntityEditor;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.model.SwingEntityTableModel;

public final class InvoiceModel extends SwingEntityModel
				implements InvoiceConfig<SwingEntityModel, SwingEntityEditModel, SwingEntityTableModel, SwingEntityEditor> {

	public InvoiceModel(EntityConnection connection) {
		super(new InvoiceEditModel(connection));
		configure();
	}

	@Override
	public SwingEntityModel createInvoiceLineModel(EntityConnection connection) {
		return new SwingEntityModel(new InvoiceLineEditModel(connection));
	}

	@Override
	public boolean invoiceLineLinkActive() {
		// The InvoiceLine panel is embedded in the InvoiceEditPanel rather than being a detail
		// panel the UI navigates to, so there is nothing to activate the link on our behalf.
		return true;
	}
}
