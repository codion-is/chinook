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
 * Copyright (c) 2025 - 2026, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.domain.reports;

import is.codion.demos.chinook.domain.ChinookImpl;
import is.codion.demos.chinook.domain.api.Chinook;
import is.codion.demos.chinook.domain.api.Chinook.Customer;
import is.codion.demos.chinook.domain.api.Chinook.Invoice;
import is.codion.plugin.jasperreports.JRExport;

import static is.codion.plugin.jasperreports.JasperReports.classPathReport;
import static is.codion.plugin.jasperreports.JasperReports.export;

/**
 * The Chinook domain with the JasperReports based reports registered.
 */
public final class ChinookReportsImpl extends ChinookImpl {

	public ChinookReportsImpl() {
		super();
		add(Customer.REPORT_PRINT, export(classPathReport(ChinookReportsImpl.class, "customer_report.jasper"), JRExport.SERIALIZED));
		add(Customer.REPORT_PDF, export(classPathReport(ChinookReportsImpl.class, "customer_report.jasper"), JRExport.PDF));
		add(Invoice.REPORT_PRINT, export(classPathReport(ChinookReportsImpl.class, "invoice.jasper"), JRExport.SERIALIZED));
		add(Invoice.REPORT_PDF, export(classPathReport(ChinookReportsImpl.class, "invoice.jasper"), JRExport.PDF));
	}
}
