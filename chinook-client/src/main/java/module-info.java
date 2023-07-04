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
 * Copyright (c) 2023, Björn Darri Sigurðsson.
 */
/**
 * Client.
 */
module is.codion.framework.demos.chinook.client {
  requires is.codion.swing.common.ui;
  requires is.codion.swing.framework.ui;
  requires is.codion.plugin.imagepanel;

  requires is.codion.framework.demos.chinook.domain.api;
  requires com.formdev.flatlaf.intellijthemes;
  requires jasperreports;

  exports is.codion.framework.demos.chinook.ui;
  exports is.codion.framework.demos.chinook.model;
}