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
 * Copyright (c) 2024 - 2025, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.service.handler;

import is.codion.demos.chinook.service.connection.ConnectionSupplier;
import is.codion.framework.db.local.LocalEntityConnection;
import is.codion.framework.domain.entity.Entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractHandler {

	private static final Logger LOG = getLogger(AbstractHandler.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final ConnectionSupplier connection;

	protected AbstractHandler(ConnectionSupplier connection) {
		this.connection = connection;
	}

	protected final LocalEntityConnection connection() {
		return connection.get();
	}

	protected final ObjectMapper mapper() {
		return MAPPER;
	}

	protected final Entities entities() {
		return connection.entities();
	}

	protected static void handleException(Context context, Exception exception) {
		LOG.error(exception.getMessage(), exception);
		context.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
	}
}
