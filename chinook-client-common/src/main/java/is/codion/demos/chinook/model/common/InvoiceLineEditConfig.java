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
 * Copyright (c) 2026, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.model.common;

import is.codion.common.reactive.observer.Observer;
import is.codion.demos.chinook.domain.api.Chinook.InvoiceLine;
import is.codion.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.model.EntityEditModel;
import is.codion.framework.model.EntityEditor;

import java.util.Objects;

public interface InvoiceLineEditConfig<R extends EntityEditor<R>> extends EntityEditModel<R> {

	default void configure() {
		// We populate the unit price when the track is edited
		Observer<Entity> trackEdited = editor().value(InvoiceLine.TRACK_FK).edited();
		trackEdited.when(Objects::nonNull)
						.addConsumer(this::setUnitPrice);
		trackEdited.when(Objects::isNull)
						.addListener(this::clearUnitPrice);
	}

	private void setUnitPrice(Entity track) {
		editor().value(InvoiceLine.UNITPRICE).set(track.get(Track.UNITPRICE));
	}

	private void clearUnitPrice() {
		editor().value(InvoiceLine.UNITPRICE).clear();
	}
}
