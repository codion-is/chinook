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
package is.codion.demos.chinook.domain;

import is.codion.common.resource.Resources;
import is.codion.framework.i18n.FrameworkMessages;

import java.util.Locale;

/**
 * Replace the english modified warning text and title.
 */
public final class ChinookResources implements Resources {

	private static final String FRAMEWORK_MESSAGES =
					FrameworkMessages.class.getName();

	private final boolean english = Locale.getDefault()
					.equals(Locale.of("en", "EN"));

	@Override
	public String getString(String baseBundleName, String key, String defaultString) {
		if (english && baseBundleName.equals(FRAMEWORK_MESSAGES)) {
			return switch (key) {
				case "modified_warning" -> "Unsaved changes will be lost, continue?";
				case "modified_warning_title" -> "Unsaved changes";
				default -> defaultString;
			};
		}

		return defaultString;
	}
}
