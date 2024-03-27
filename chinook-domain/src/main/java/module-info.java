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
 * Domain implementation.
 */
module is.codion.framework.demos.chinook.domain {
	requires is.codion.common.db;
	requires is.codion.common.rmi;
	requires is.codion.framework.db.core;
	requires is.codion.framework.db.local;
	requires transitive is.codion.framework.demos.chinook.domain.api;

	opens is.codion.framework.demos.chinook.domain;//report resource
	exports is.codion.framework.demos.chinook.domain;
	exports is.codion.framework.demos.chinook.server;

	provides is.codion.framework.domain.Domain
					with is.codion.framework.demos.chinook.domain.ChinookImpl;
	provides is.codion.common.rmi.server.Authenticator
					with is.codion.framework.demos.chinook.server.ChinookAuthenticator;
}