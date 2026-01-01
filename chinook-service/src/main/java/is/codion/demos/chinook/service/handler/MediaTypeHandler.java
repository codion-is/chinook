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
 * Copyright (c) 2024 - 2026, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.service.handler;

import is.codion.demos.chinook.domain.api.Chinook.MediaType;
import is.codion.demos.chinook.service.connection.ConnectionSupplier;
import is.codion.framework.domain.entity.Entity;

import io.javalin.http.Context;

import static io.javalin.http.HttpStatus.OK;

public class MediaTypeHandler extends AbstractHandler{

	public MediaTypeHandler(ConnectionSupplier connection) {
		super(connection);
	}

	public void insert(Context context) {
		try (var connection = connection()) {
			MediaType.Dto mediaTypeDto = context.bodyStreamAsClass(MediaType.Dto.class);
			Entity mediaType = connection.insertSelect(mediaTypeDto.entity(entities()));
			context.status(OK)
							.result(mapper().writeValueAsString(MediaType.dto(mediaType)));
		}
		catch (Exception e) {
			handleException(context, e);
		}
	}
}
