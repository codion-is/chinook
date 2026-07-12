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
/**
 * Domain implementation with JasperReports based reports.
 */
module is.codion.demos.chinook.domain.reports {
	requires is.codion.framework.domain;
	requires transitive is.codion.demos.chinook.domain;
	requires transitive is.codion.plugin.jasperreports;
	requires net.sf.jasperreports.core;

	opens is.codion.demos.chinook.domain.reports;//report resource
	exports is.codion.demos.chinook.domain.reports;

	provides is.codion.framework.domain.Domain
					with is.codion.demos.chinook.domain.reports.ChinookReportsImpl;
}
