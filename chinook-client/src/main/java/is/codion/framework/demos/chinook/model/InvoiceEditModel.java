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
package is.codion.framework.demos.chinook.model;

import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.domain.entity.Entity;
import is.codion.swing.framework.model.SwingEntityEditModel;

import static is.codion.framework.demos.chinook.domain.api.Chinook.Customer;
import static is.codion.framework.demos.chinook.domain.api.Chinook.Invoice;

public final class InvoiceEditModel extends SwingEntityEditModel {

	public InvoiceEditModel(EntityConnectionProvider connectionProvider) {
		super(Invoice.TYPE, connectionProvider);
		persist(Invoice.CUSTOMER_FK).set(false);
		bindEvents();
	}

	private void bindEvents() {
		addEditListener(Invoice.CUSTOMER_FK, this::setAddress);
	}

	private void setAddress(Entity customer) {
		if (customer == null) {
			put(Invoice.BILLINGADDRESS, null);
			put(Invoice.BILLINGCITY, null);
			put(Invoice.BILLINGPOSTALCODE, null);
			put(Invoice.BILLINGSTATE, null);
			put(Invoice.BILLINGCOUNTRY, null);
		}
		else {
			put(Invoice.BILLINGADDRESS, customer.get(Customer.ADDRESS));
			put(Invoice.BILLINGCITY, customer.get(Customer.CITY));
			put(Invoice.BILLINGPOSTALCODE, customer.get(Customer.POSTALCODE));
			put(Invoice.BILLINGSTATE, customer.get(Customer.STATE));
			put(Invoice.BILLINGCOUNTRY, customer.get(Customer.COUNTRY));
		}
	}
}
