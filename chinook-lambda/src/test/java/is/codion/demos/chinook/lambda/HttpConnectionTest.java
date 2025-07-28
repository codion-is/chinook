package is.codion.demos.chinook.lambda;

import is.codion.common.user.User;
import is.codion.demos.chinook.domain.api.Chinook;
import is.codion.demos.chinook.domain.api.Chinook.Artist;
import is.codion.framework.db.EntityConnection;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.db.http.HttpEntityConnectionProvider;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.condition.Condition;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class HttpConnectionTest {

	@Test
	@Disabled("Integration test - requires deployed Lambda")
	public void testLambdaConnection() throws Exception {
		System.out.println("=== Testing HttpEntityConnection to Lambda ===");

		// Configure connection
		EntityConnectionProvider connectionProvider = HttpEntityConnectionProvider.builder()
						.hostName("vf5lwi7pgci5g32fw3drtsbovm0vbmsu.lambda-url.eu-north-1.on.aws")
						.https(true)
						.securePort(443)
						.json(false)
						.domain(Chinook.DOMAIN)
						.user(User.parse("scott:tiger"))
						.clientType("ChinookTest")
						.build();

		System.out.println("Attempting to connect...");
		try (EntityConnection connection = connectionProvider.connection()) {
			System.out.println("✓ Connected successfully!");

			// Test 1: Get entity definitions
			System.out.println("\nTest 1: Entity definitions");
			System.out.println("Entities available: " + connection.entities());

			// Test 2: Count artists
			System.out.println("\nTest 2: Count artists");
			int artistCount = 0;
			try {
				artistCount = connection.count(EntityConnection.Count.where(Condition.all(Artist.TYPE)));
				System.out.println("Artist count: " + artistCount);
			} catch (Exception e) {
				System.out.println("Count failed (expected on empty DB): " + e.getMessage());
			}

			// Test 3: Insert and select an artist (since DB is empty)
			System.out.println("\nTest 3: Insert and select an artist");
			if (artistCount == 0) {
				System.out.println("No artists found, inserting test artist...");
				Entity newArtist = connection.entities().entity(Artist.TYPE)
								.with(Artist.NAME, "Test Artist")
								.build();
				Entity inserted = connection.insertSelect(newArtist);
				System.out.println("Inserted artist: " + inserted.get(Artist.NAME) +
								" with ID: " + inserted.get(Artist.ID));
			}
			else {
				Entity artist = connection.selectSingle(
								Artist.ID.equalTo(1L)
				);
				System.out.println("First artist: " + artist.get(Artist.NAME));
			}

			System.out.println("\n✓ All tests passed!");

		}
		catch (Exception e) {
			System.err.println("❌ Error: " + e.getClass().getName() + " - " + e.getMessage());
			e.printStackTrace();

			// Print cause chain
			Throwable cause = e.getCause();
			while (cause != null) {
				System.err.println("Caused by: " + cause.getClass().getName() + " - " + cause.getMessage());
				cause = cause.getCause();
			}
			throw e; // Re-throw for JUnit to catch
		}
	}
}