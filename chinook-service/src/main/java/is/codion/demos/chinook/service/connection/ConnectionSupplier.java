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
package is.codion.demos.chinook.service.connection;

import is.codion.common.db.database.Database;
import is.codion.common.db.exception.DatabaseException;
import is.codion.common.db.pool.ConnectionPoolFactory;
import is.codion.common.db.pool.ConnectionPoolStatistics;
import is.codion.common.db.pool.ConnectionPoolWrapper;
import is.codion.common.scheduler.TaskScheduler;
import is.codion.common.user.User;
import is.codion.demos.chinook.domain.api.Chinook;
import is.codion.demos.chinook.domain.api.Chinook.Album;
import is.codion.demos.chinook.domain.api.Chinook.Artist;
import is.codion.demos.chinook.domain.api.Chinook.Genre;
import is.codion.demos.chinook.domain.api.Chinook.MediaType;
import is.codion.demos.chinook.domain.api.Chinook.Track;
import is.codion.framework.db.local.LocalEntityConnection;
import is.codion.framework.domain.Domain;
import is.codion.framework.domain.DomainModel;
import is.codion.framework.domain.entity.Entities;

import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static is.codion.framework.db.local.LocalEntityConnection.localEntityConnection;
import static java.lang.System.currentTimeMillis;
import static org.slf4j.LoggerFactory.getLogger;

public final class ConnectionSupplier implements Supplier<LocalEntityConnection> {

	private static final Logger LOG = getLogger(ConnectionSupplier.class);

	private static final User USER = User.parse("scott:tiger");
	private static final int STATISTICS_PRINT_RATE = 5;

	private final Domain domain = new ServiceDomain();
	// Relies on the codion-dbms-h2 module and the h2 driver being
	// on the classpath and the 'codion.db.url' system property
	private final Database database = Database.instance();
	private final ConnectionPoolWrapper connectionPool =
					// Relies on codion-plugin-hikari-pool being on the classpath
					database.createConnectionPool(ConnectionPoolFactory.instance(), USER);

	public ConnectionSupplier() {
		connectionPool.setCollectCheckOutTimes(true);
		LOG.info("Database: {}", database.url());
		LOG.info("Connection pool: {}", connectionPool.user());
		LOG.info("Domain: {}", domain.type().name());
		TaskScheduler.builder(this::printStatistics)
					.interval(STATISTICS_PRINT_RATE, TimeUnit.SECONDS)
					.start();
	}

	@Override
	public LocalEntityConnection get() {
		try {
			return localEntityConnection(database, domain, connectionPool.connection(USER));
		}
		catch (DatabaseException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public Entities entities() {
		return domain.entities();
	}

	private void printStatistics() {
		ConnectionPoolStatistics poolStatistics =
						// fetch statistics collected since last time we fetched
						connectionPool.statistics(currentTimeMillis() - STATISTICS_PRINT_RATE * 1_000);
		System.out.println("#### Connection Pool ############");
		System.out.println("# Requests per second: " + poolStatistics.requestsPerSecond());
		System.out.println("# Average check out time: " + poolStatistics.averageTime() + " ms");
		System.out.println("# Size: " + poolStatistics.size() + ", in use: " + poolStatistics.inUse());
		System.out.println("#### Database ###################");
		Database.Statistics databaseStatistics = database.statistics();
		System.out.println("# Queries per second: " + databaseStatistics.queriesPerSecond());
		System.out.println("#################################");
	}

	private static final class ServiceDomain extends DomainModel {

		private ServiceDomain() {
			super(Chinook.DOMAIN);
			// Relies on the Chinook domain model
			// being registered as a service
			Entities entities = Domain.domains().getFirst().entities();
			// Only add the entities required for this service
			add(entities.definition(Genre.TYPE));
			add(entities.definition(MediaType.TYPE));
			add(entities.definition(Artist.TYPE));
			add(entities.definition(Album.TYPE));
			add(entities.definition(Track.TYPE));
		}
	}
}
