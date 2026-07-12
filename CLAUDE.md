# Chinook - Codion Framework Demo Application

A comprehensive demo application for the [Codion](https://codion.is) application framework, modeling a music store database (artists, albums, tracks, playlists, customers, invoices). Licensed under GPL v3.0.

## Build & Test

```bash
# Build all modules
./gradlew build

# Run tests (uses H2 in-memory DB, user scott:tiger)
./gradlew test

# Run the local desktop client (embedded H2)
./gradlew :chinook-client-local:run

# Run the server (RMI + HTTP on ports 1098/2223/4445/8088)
./gradlew :chinook-server:run

# Run the REST service (Javalin on port 8089)
./gradlew :chinook-service:run

# Run a specific test class
./gradlew :chinook-domain:test --tests "is.codion.demos.chinook.domain.ChinookTest"

# Create native installers via jlink/jpackage
./gradlew :chinook-client-local:jlink
./gradlew :chinook-client-local:jpackage
```

**Requirements:** Java 26 (toolchain), Gradle 9.5+. Database: H2 in-memory (auto-initialized from `create_schema.sql`). Test user: `scott:tiger`.

## Project Structure

```
chinook/
├── chinook-domain-api/          # Domain model API (interfaces, constants, DTOs)
│   └── Chinook.java             # THE central file - all entity/column/FK definitions
├── chinook-domain/              # Domain implementation (entity definitions, business logic)
│   └── ChinookImpl.java         # Entity definitions, procedures, functions, migrations
├── chinook-client/              # Swing UI (panels, models, custom components)
│   ├── ui/                      # EntityPanel, EntityEditPanel, EntityTablePanel impls
│   └── model/                   # SwingEntityModel subclasses
├── chinook-client-local/        # Config: desktop client with embedded H2
├── chinook-client-remote/       # Config: desktop client via RMI
├── chinook-client-http/         # Config: desktop client via HTTP
├── chinook-server/              # Config: multi-protocol server (RMI + HTTP)
├── chinook-server-docker/       # Docker deployment with jlink
├── chinook-server-monitor/      # Config: server monitoring UI
├── chinook-service/             # REST API (Javalin + Jackson)
├── chinook-service-load-test/   # HTTP load test scenarios
├── chinook-load-test/           # Framework load test scenarios
├── chinook-load-test-http/      # Config: load test via HTTP
├── chinook-load-test-remote/    # Config: load test via RMI
├── chinook-domain-generator/    # Config: code generation tool
└── buildSrc/                    # Custom Gradle plugins (spotless, jasperreports)
```

## Key Files (Read These First)

| File | Purpose | Lines |
|------|---------|-------|
| `chinook-domain-api/.../Chinook.java` | All entity types, columns, FKs, DTOs, validators | ~497 |
| `chinook-domain/.../ChinookImpl.java` | Entity definitions, procedures, functions | ~743 |
| `chinook-client/.../ChinookAppPanel.java` | Application entry point, panel wiring | ~285 |
| `chinook-domain/src/main/resources/create_schema.sql` | Database DDL | ~181 |

## Codion Framework Architecture

### Layer 1: Domain API (`chinook-domain-api`)

The domain API is a **pure interface** defining the data model. Everything lives in a single interface `Chinook` with nested interfaces per entity:

```java
public interface Chinook {
    DomainType DOMAIN = domainType(Chinook.class);

    interface Artist {
        EntityType TYPE = DOMAIN.entityType("chinook.artist", Artist.class);
        Column<Long> ID = TYPE.longColumn("id");
        Column<String> NAME = TYPE.stringColumn("name");
        ForeignKey ARTIST_FK = TYPE.foreignKey("artist_fk", ARTIST_ID, Artist.ID);
        // DTOs for REST serialization
        record Dto(Long id, String name) { ... }
    }
}
```

**Key types:**
- `EntityType` - identifies an entity (maps to a table)
- `Column<T>` - typed column definition (`longColumn`, `stringColumn`, `bigDecimalColumn`, `localDateColumn`, etc.)
- `ForeignKey` - relationship between entities
- `Attribute<T>` - non-column attribute (e.g., denormalized values)
- `FunctionType<C, P, R>` / `ProcedureType<C, P>` - database operations
- `ConditionType` - custom WHERE clause conditions
- `DerivedValue<T>` - computed from other attributes (e.g., `InvoiceLineTotal`)
- `DefaultValue<T>` - default value supplier (e.g., `LocalDate::now`)

**Entities defined:** Artist, Album, Employee, Customer, Preferences, Genre, MediaType, Track, Invoice, InvoiceLine, Playlist, PlaylistTrack, ArtistRevenue

**Helper classes in Chinook.java:**
- `InvoiceLineTotal` - `DerivedValue<BigDecimal>` (quantity * unitPrice)
- `CustomerFormatter` - locale-aware name formatting (en: "Last, First", is: "First Last")
- `CoverFormat` - displays byte[] as "X Kb"
- `EmailValidator` - `EntityValidator` with regex pattern

### Layer 2: Domain Implementation (`chinook-domain`)

`ChinookImpl extends DomainModel` - defines HOW entities behave:

```java
EntityDefinition artist() {
    return Artist.TYPE.as()
    .attributes(
        Artist.ID.as().primaryKey().generator(identity()),
        Artist.NAME.as().column(REQUIRED_SEARCHABLE).maximumLength(120),
        Artist.NUMBER_OF_ALBUMS.as().subquery("SELECT COUNT(*) FROM ..."))
    .orderBy(ascending(Artist.NAME))
    .formatter(Artist.NAME)
    .build();
}
```

**Key definition patterns:**
- `.primaryKey()` + `.generator(identity())` - auto-increment PKs
- `.column(template)` - apply reusable `ColumnTemplate` (e.g., `REQUIRED_SEARCHABLE`, `INSERT_TIME`)
- `.subquery(sql)` - computed columns via SQL subquery
- `.foreignKey().include(...)` - eager-load FK columns, `.referenceDepth(n)` for depth
- `.derived().from(...).with(derivedValue)` - client-computed values
- `.denormalized().from(fk).using(otherFk)` - flatten nested relationships
- `.selectQuery(EntitySelectQuery.builder().from(...).build())` - custom JOIN queries
- `.condition(type, lambda)` - custom condition with SQL template
- `.readOnly(true)` - columns from joined tables
- `.expression("table.column")` - disambiguate in JOINed queries
- `.smallDataset(true)` - hint for combo boxes (Genre, MediaType)
- `.validator(new EmailValidator(...))` - entity-level validation
- `.converter(Class, Converter, ResultSetGetter)` - custom type mapping (e.g., SQL Array <-> List<String>)

**Database operations (added in constructor):**
- `Track.RAISE_PRICE` - `DatabaseFunction` that selects tracks FOR UPDATE, increases price
- `Invoice.UPDATE_TOTALS` - `DatabaseProcedure` that recalculates invoice totals
- `Playlist.RANDOM_PLAYLIST` - `DatabaseFunction` that creates playlist with random tracks

**Other domain classes:**
- `ChinookAuthenticator` - RMI auth via `chinook.users` table (username + password hash)
- `MigrationManager` / `MigrationDomain` - schema migration system (V1, V2, V3 SQL scripts)
- `ChinookObjectMapperFactory` - JSON serialization config for operation parameters

**Service loader registration:** `META-INF/services/is.codion.framework.domain.Domain` -> `ChinookImpl`

### Layer 3: Swing Client (`chinook-client`)

**Application structure:**
- `ChinookAppPanel extends EntityApplicationPanel` - top-level panel
- `ChinookAppModel extends SwingEntityApplicationModel` - top-level model

**Panel hierarchy (3 main tabs + 5 lookup panels):**
```
ChinookAppPanel
├── CustomerPanel (CustomerModel)      # Main tab: Customer -> Invoice -> InvoiceLine
├── AlbumPanel (AlbumModel)            # Main tab: Album -> Track
├── PlaylistPanel (PlaylistModel)      # Main tab: Playlist -> PlaylistTrack
└── Lookup panels (opened on demand):
    ├── ArtistPanel                    # Artist lookup
    ├── GenrePanel                     # Genre lookup
    ├── MediaTypePanel                 # MediaType lookup
    ├── EmployeePanel                  # Employee with self-referencing hierarchy
    └── PreferencesPanel               # Customer preferences
```

**UI component patterns:**
- `EntityPanel` - contains an edit panel + table panel + optional detail panels
- `EntityEditPanel` - form for editing a single entity
- `EntityTablePanel` - table view with filtering, sorting, CRUD
- `SwingEntityModel` - model binding entity to Swing components

**Custom components demonstrated:**
- `CoverArtValue` - image display/edit for Album.COVER (byte[])
- `DurationPanel` / `DurationPanelBuilder` - minutes:seconds editor for Track.MILLISECONDS
- `TriStateCheckBoxBuilder/Value` - FlatLaf tri-state checkbox for nullable booleans
- `AnalyticsPanel` - JFreeChart pie chart for genre distribution
- `InvoiceConditionPanel` - custom search condition panel
- `RandomPlaylistParametersPanel` - dialog for random playlist creation

**Application startup (in `main()`):**
```java
EntityApplication.builder(ChinookAppModel.class, ChinookAppPanel.class)
    .domain(Chinook.DOMAIN)
    .version(ChinookAppModel.VERSION)
    .defaultLookAndFeel(MaterialTheme.class)
    .defaultUser(User.parse("scott:tiger"))
    .start();
```

### Layer 4: REST Service (`chinook-service`)

Standalone Javalin HTTP service with local H2 database:
- Routes: `/artists`, `/albums`, `/tracks`, `/genres`, `/mediatypes` (GET + POST)
- Uses `Dto` records from domain API for JSON serialization
- `ConnectionSupplier` manages HikariCP pool + domain setup
- `AbstractHandler` base class for all route handlers

### Layer 5: Server (`chinook-server`)

Multi-protocol Codion server (`EntityServer`):
- RMI (port 2223) + HTTP/JSON (port 8088) + Admin (port 4445)
- Registry port 1098, SSL via keystore.jks
- `ChinookAuthenticator` for RMI client authentication
- Docker deployment via jlink custom JRE

## Database Schema

11 tables in `CHINOOK` schema + 1 migration tracking table:

```
ARTIST(id, name)
ALBUM(id, title, artist_id→ARTIST, cover, tags, insert_time, insert_user)
TRACK(id, name, album_id→ALBUM, mediatype_id→MEDIATYPE, genre_id→GENRE,
      composer, milliseconds, bytes, rating[1-10], unitprice, play_count, insert_time, insert_user)
GENRE(id, name)
MEDIATYPE(id, name)
EMPLOYEE(id, lastname, firstname, title, reportsto_id→EMPLOYEE, birthdate, hiredate,
         address, city, state, country, postalcode, phone, fax, email, insert_time, insert_user)
CUSTOMER(id, firstname, lastname, company, address, city, state, country, postalcode,
         phone, fax, email, supportrep_id→EMPLOYEE, insert_time, insert_user)
INVOICE(id, customer_id→CUSTOMER, invoicedate, billing*, total, insert_time, insert_user)
INVOICELINE(id, invoice_id→INVOICE, track_id→TRACK, unitprice, quantity, insert_time, insert_user)
PLAYLIST(id, name)
PLAYLISTTRACK(id, playlist_id→PLAYLIST, track_id→TRACK)
PREFERENCES(customer_id→CUSTOMER, preferred_genre_id→GENRE, newsletter)
USERS(id, username, passwordhash)
```

## i18n

Two locales: English (default) and Icelandic (`is_IS`). Property files follow pattern:
- `Chinook.properties` / `Chinook_is_IS.properties` (shared strings)
- `Chinook$Artist.properties` / `Chinook$Artist_is_IS.properties` (per-entity)
- UI panels have their own property files in `chinook-client/src/main/resources/`

## Dependencies (from `gradle/libs.versions.toml`)

| Component | Version |
|-----------|---------|
| Codion Framework | 0.18.72 |
| H2 Database | 2.3.232 |
| FlatLaf | 3.7.1   |
| Javalin | 7.1.0   |
| Jackson | 2.21.2  |
| JasperReports | 7.0.6   |
| JFreeChart | 1.5.6   |

## Java Module System (JPMS)

All modules use `module-info.java`. Key module names:
- `is.codion.demos.chinook.domain.api` - domain API
- `is.codion.demos.chinook.domain` - domain implementation
- `is.codion.demos.chinook.client` - Swing client
- `is.codion.demos.chinook.service` - REST service
- `is.codion.demos.chinook.client.loadtest` - load testing

## Common Patterns Reference

### Adding a new entity
1. Add table DDL to `create_schema.sql` (or add a migration in `chinook-domain/src/main/resources/db/migration/`)
2. Add interface in `Chinook.java` with `EntityType`, `Column<T>`, `ForeignKey` constants
3. Add `EntityDefinition` method in `ChinookImpl.java`, call it from constructor's `add()`
4. Add property files for captions (English + Icelandic)
5. Create UI panel classes in `chinook-client` (EditPanel, TablePanel, Panel)
6. Wire into `ChinookAppPanel` or as lookup panel

### Adding a column to existing entity
1. Add column to DDL (or migration script)
2. Add `Column<T>` constant in the entity interface in `Chinook.java`
3. Add column definition in `ChinookImpl.java` entity method (`.as()` chain)
4. Add caption to property files
5. Add to edit panel if needed

### EntityConnection API (used in procedures/functions/load tests)
```java
// Select
connection.select(Artist.NAME.equalTo("Metallica"));
connection.selectSingle(Artist.ID.equalTo(42L));
connection.select(where(Track.GENRE_FK.in(genres)).orderBy(...).limit(10).build());

// Insert
Entity entity = entities.entity(Artist.TYPE).with(Artist.NAME, "New Artist").build();
Entity inserted = connection.insertSelect(entity);

// Update
entity.set(Track.UNITPRICE, newPrice);
connection.update(entity);  // or updateSelect() to get updated entity back

// Delete
connection.delete(Entity.primaryKeys(entities));

// Transactions
EntityConnection.transaction(connection, () -> { ... });

// Execute procedures/functions
connection.execute(Invoice.UPDATE_TOTALS, invoiceIds);
Entity result = connection.execute(Playlist.RANDOM_PLAYLIST, params);
```

## Ports (from `gradle.properties`)

| Port | Service |
|------|---------|
| 1098 | RMI Registry |
| 2223 | RMI Server |
| 4445 | Admin (Server Monitor) |
| 8088 | HTTP Servlet (server) |
| 8089 | REST Service (Javalin) |
