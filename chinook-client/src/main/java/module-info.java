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
 * Copyright (c) 2023 - 2025, Björn Darri Sigurðsson.
 */
/**
 * Client.
 */
module is.codion.demos.chinook.client {
	requires is.codion.swing.common.ui;
	requires is.codion.swing.framework.ui;
	requires is.codion.plugin.imagepanel;
	requires is.codion.plugin.flatlaf;
	requires is.codion.plugin.flatlaf.intellij.themes;

	requires is.codion.demos.chinook.domain.api;
	requires org.kordamp.ikonli.foundation;
	requires net.sf.jasperreports.core;
	requires net.sf.jasperreports.pdf;
	requires org.jfree.jfreechart;
	requires org.apache.commons.logging;
	requires com.github.librepdf.openpdf;
	requires is.codion.plugin.swing.mcp;

	exports is.codion.demos.chinook.ui;
	exports is.codion.demos.chinook.model;

	provides is.codion.common.resource.Resources
					with is.codion.demos.chinook.i18n.ChinookResources;
}