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
 * Copyright (c) 2024, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.service.handler;

import is.codion.framework.demos.chinook.domain.api.Chinook.Genre;
import is.codion.framework.demos.chinook.service.connection.ConnectionSupplier;
import is.codion.framework.domain.entity.Entity;

import io.javalin.http.Context;

import static io.javalin.http.HttpStatus.OK;

public class GenreHandler extends AbstractHandler {

	public GenreHandler(ConnectionSupplier connection) {
		super(connection);
	}

	public void insert(Context context) {
		try (var connection = connection()) {
			Genre.Dto genreDto = context.bodyStreamAsClass(Genre.Dto.class);
			Entity genre = connection.insertSelect(genreDto.entity(entities()::builder));
			context.status(OK)
							.result(mapper().writeValueAsString(Genre.dto(genre)));
		}
		catch (Exception e) {
			handleException(context, e);
		}
	}
}
