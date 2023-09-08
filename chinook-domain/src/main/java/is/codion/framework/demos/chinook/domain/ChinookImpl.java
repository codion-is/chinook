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
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.chinook.domain;

import is.codion.common.db.exception.DatabaseException;
import is.codion.common.db.operation.DatabaseFunction;
import is.codion.common.format.LocaleDateTimePattern;
import is.codion.framework.db.EntityConnection;
import is.codion.framework.db.EntityConnection.Select;
import is.codion.framework.demos.chinook.domain.api.Chinook;
import is.codion.framework.demos.chinook.domain.api.Chinook.Playlist.RandomPlaylistParameters;
import is.codion.framework.demos.chinook.domain.api.Chinook.Track.RaisePriceParameters;
import is.codion.framework.domain.DefaultDomain;
import is.codion.framework.domain.entity.Entities;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.OrderBy;
import is.codion.framework.domain.entity.StringFactory;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;

import static is.codion.framework.db.EntityConnection.Select.where;
import static is.codion.framework.db.condition.Condition.column;
import static is.codion.framework.db.condition.Condition.foreignKey;
import static is.codion.framework.domain.entity.KeyGenerator.identity;
import static is.codion.framework.domain.entity.OrderBy.ascending;
import static is.codion.plugin.jasperreports.JasperReports.classPathReport;
import static java.util.stream.Collectors.toList;

// tag::chinook[]
public final class ChinookImpl extends DefaultDomain implements Chinook {

  public ChinookImpl() {
    super(DOMAIN);
    artist();
    album();
    employee();
    customer();
    genre();
    mediaType();
    track();
    invoice();
    invoiceLine();
    playlist();
    playlistTrack();
  }
  // end::chinook[]

  // tag::artist[]
  void artist() {
    add(Artist.TYPE.define(
            Artist.ID
                    .primaryKeyColumn(),
            Artist.NAME
                    .column()
                    .searchColumn(true)
                    .nullable(false)
                    .maximumLength(120),
            Artist.NUMBER_OF_ALBUMS
                    .subqueryColumn("""
                            select count(*)
                            from chinook.album
                            where album.artistid = artist.artistid"""),
            Artist.NUMBER_OF_TRACKS
                    .subqueryColumn("""
                            select count(*)
                            from chinook.track
                            join chinook.album on track.albumid = album.albumid
                            where album.artistid = artist.artistid"""))
            .tableName("chinook.artist")
            .keyGenerator(identity())
            .orderBy(ascending(Artist.NAME))
            .stringFactory(Artist.NAME));
  }
  // end::artist[]

  // tag::album[]
  void album() {
    add(Album.TYPE.define(
            Album.ID
                    .primaryKeyColumn(),
            Album.ARTIST_ID
                    .column()
                    .nullable(false),
            Album.ARTIST_FK
                    .foreignKey()
                    .attributes(Artist.NAME),
            Album.TITLE
                    .column()
                    .searchColumn(true)
                    .nullable(false)
                    .maximumLength(160),
            Album.COVER
                    .blobColumn()
                    .eagerlyLoaded(true)
                    .format(new CoverFormatter()),
            Album.COVERIMAGE
                    .derivedAttribute(new CoverArtImageProvider(), Album.COVER),
            Album.NUMBER_OF_TRACKS
                    .subqueryColumn("""
                            select count(*)
                            from chinook.track
                            where track.albumid = album.albumid"""))
            .tableName("chinook.album")
            .keyGenerator(identity())
            .orderBy(ascending(Album.ARTIST_ID, Album.TITLE))
            .stringFactory(Album.TITLE));
  }
  // end::album[]

  // tag::employee[]
  void employee() {
    add(Employee.TYPE.define(
            Employee.ID
                    .primaryKeyColumn(),
            Employee.LASTNAME
                    .column()
                    .searchColumn(true)
                    .nullable(false)
                    .maximumLength(20),
            Employee.FIRSTNAME
                    .column()
                    .searchColumn(true)
                    .nullable(false)
                    .maximumLength(20),
            Employee.TITLE
                    .column()
                    .maximumLength(30),
            Employee.REPORTSTO
                    .column(),
            Employee.REPORTSTO_FK
                    .foreignKey()
                    .attributes(Employee.FIRSTNAME, Employee.LASTNAME),
            Employee.BIRTHDATE
                    .column(),
            Employee.HIREDATE
                    .column()
                    .localeDateTimePattern(LocaleDateTimePattern.builder()
                            .delimiterDot()
                            .yearFourDigits()
                            .build()),
            Employee.ADDRESS
                    .column()
                    .maximumLength(70),
            Employee.CITY
                    .column()
                    .maximumLength(40),
            Employee.STATE
                    .column()
                    .maximumLength(40),
            Employee.COUNTRY
                    .column()
                    .maximumLength(40),
            Employee.POSTALCODE
                    .column()
                    .maximumLength(10),
            Employee.PHONE
                    .column()
                    .maximumLength(24),
            Employee.FAX
                    .column()
                    .maximumLength(24),
            Employee.EMAIL
                    .column()
                    .searchColumn(true)
                    .nullable(false)
                    .maximumLength(60))
            .tableName("chinook.employee")
            .keyGenerator(identity())
            .validator(new EmailValidator(Employee.EMAIL))
            .orderBy(ascending(Employee.LASTNAME, Employee.FIRSTNAME))
            .stringFactory(StringFactory.builder()
                    .value(Employee.LASTNAME)
                    .text(", ")
                    .value(Employee.FIRSTNAME)
                    .build()));
  }
  // end::employee[]

  // tag::customer[]
  void customer() {
    add(Customer.TYPE.define(
            Customer.ID
                    .primaryKeyColumn(),
            Customer.LASTNAME
                    .column()
                    .searchColumn(true)
                    .nullable(false)
                    .maximumLength(20),
            Customer.FIRSTNAME
                    .column()
                    .searchColumn(true)
                    .nullable(false)
                    .maximumLength(40),
            Customer.COMPANY
                    .column()
                    .maximumLength(80),
            Customer.ADDRESS
                    .column()
                    .maximumLength(70),
            Customer.CITY
                    .column()
                    .maximumLength(40),
            Customer.STATE
                    .column()
                    .maximumLength(40),
            Customer.COUNTRY
                    .column()
                    .maximumLength(40),
            Customer.POSTALCODE
                    .column()
                    .maximumLength(10),
            Customer.PHONE
                    .column()
                    .maximumLength(24),
            Customer.FAX
                    .column()
                    .maximumLength(24),
            Customer.EMAIL
                    .column()
                    .searchColumn(true)
                    .nullable(false)
                    .maximumLength(60),
            Customer.SUPPORTREP_ID
                    .column(),
            Customer.SUPPORTREP_FK
                    .foreignKey()
                    .attributes(Employee.FIRSTNAME, Employee.LASTNAME))
            .tableName("chinook.customer")
            .keyGenerator(identity())
            .validator(new EmailValidator(Customer.EMAIL))
            .orderBy(ascending(Customer.LASTNAME, Customer.FIRSTNAME))
            .stringFactory(new CustomerStringFactory()));

    add(Customer.REPORT, classPathReport(ChinookImpl.class, "customer_report.jasper"));
  }
  // end::customer[]

  // tag::genre[]
  void genre() {
    add(Genre.TYPE.define(
            Genre.ID
                    .primaryKeyColumn(),
            Genre.NAME
                    .column()
                    .searchColumn(true)
                    .nullable(false)
                    .maximumLength(120))
            .tableName("chinook.genre")
            .keyGenerator(identity())
            .orderBy(ascending(Genre.NAME))
            .stringFactory(Genre.NAME)
            .smallDataset(true));
  }
  // end::genre[]

  // tag::mediaType[]
  void mediaType() {
    add(MediaType.TYPE.define(
            MediaType.ID
                    .primaryKeyColumn(),
            MediaType.NAME
                    .column()
                    .nullable(false)
                    .maximumLength(120))
            .tableName("chinook.mediatype")
            .keyGenerator(identity())
            .stringFactory(MediaType.NAME)
            .smallDataset(true));
  }
  // end::mediaType[]

  // tag::track[]
  void track() {
    add(Track.TYPE.define(
            Track.ID
                    .primaryKeyColumn(),
            Track.ARTIST_DENORM
                    .denormalizedAttribute(Track.ALBUM_FK, Album.ARTIST_FK),
            Track.ALBUM_ID
                    .column(),
            Track.ALBUM_FK
                    .foreignKey()
                    .attributes(Album.ARTIST_FK, Album.TITLE)
                    .fetchDepth(2),
            Track.NAME
                    .column()
                    .searchColumn(true)
                    .nullable(false)
                    .maximumLength(200),
            Track.GENRE_ID
                    .column(),
            Track.GENRE_FK
                    .foreignKey(),
            Track.COMPOSER
                    .column()
                    .maximumLength(220),
            Track.MEDIATYPE_ID
                    .column()
                    .nullable(false),
            Track.MEDIATYPE_FK
                    .foreignKey(),
            Track.MILLISECONDS
                    .column()
                    .nullable(false)
                    .format(NumberFormat.getIntegerInstance()),
            Track.MINUTES_SECONDS_DERIVED
                    .derivedAttribute(new TrackMinSecProvider(), Track.MILLISECONDS),
            Track.BYTES
                    .column()
                    .format(NumberFormat.getIntegerInstance()),
            Track.UNITPRICE
                    .column()
                    .nullable(false)
                    .maximumFractionDigits(2),
            Track.RANDOM
                    .column()
                    .readOnly(true)
                    .selectable(false))
            .tableName("chinook.track")
            .keyGenerator(identity())
            .orderBy(ascending(Track.NAME))
            .stringFactory(Track.NAME));

    add(Track.RAISE_PRICE, new RaisePriceFunction());
  }
  // end::track[]

  // tag::invoice[]
  void invoice() {
    add(Invoice.TYPE.define(
            Invoice.ID
                    .primaryKeyColumn(),
            Invoice.CUSTOMER_ID
                    .column()
                    .nullable(false),
            Invoice.CUSTOMER_FK
                    .foreignKey()
                    .attributes(Customer.FIRSTNAME, Customer.LASTNAME, Customer.EMAIL),
            Invoice.DATE
                    .column()
                    .nullable(false)
                    .defaultValueSupplier(Invoice.DATE_DEFAULT_VALUE)
                    .localeDateTimePattern(LocaleDateTimePattern.builder()
                            .delimiterDot()
                            .yearFourDigits()
                            .build()),
            Invoice.BILLINGADDRESS
                    .column()
                    .maximumLength(70),
            Invoice.BILLINGCITY
                    .column()
                    .maximumLength(40),
            Invoice.BILLINGSTATE
                    .column()
                    .maximumLength(40),
            Invoice.BILLINGCOUNTRY
                    .column()
                    .maximumLength(40),
            Invoice.BILLINGPOSTALCODE
                    .column()
                    .maximumLength(10),
            Invoice.TOTAL
                    .column()
                    .nullable(false)
                    .maximumFractionDigits(2),
            Invoice.CALCULATED_TOTAL
                    .subqueryColumn("""
                            select sum(unitprice * quantity)
                            from chinook.invoiceline
                            where invoiceid = invoice.invoiceid""")
                    .maximumFractionDigits(2))
            .tableName("chinook.invoice")
            .keyGenerator(identity())
            .orderBy(OrderBy.builder()
                    .ascending(Invoice.CUSTOMER_ID)
                    .descending(Invoice.DATE)
                    .build())
            .stringFactory(Invoice.ID));

    add(Invoice.UPDATE_TOTALS, new UpdateTotalsFunction());
  }
  // end::invoice[]

  // tag::invoiceLine[]
  void invoiceLine() {
    add(InvoiceLine.TYPE.define(
            InvoiceLine.ID
                    .primaryKeyColumn(),
            InvoiceLine.INVOICE_ID
                    .column()
                    .nullable(false),
            InvoiceLine.INVOICE_FK
                    .foreignKey()
                    .fetchDepth(0),
            InvoiceLine.TRACK_ID
                    .column()
                    .nullable(false),
            InvoiceLine.TRACK_FK
                    .foreignKey()
                    .attributes(Track.NAME, Track.UNITPRICE),
            InvoiceLine.UNITPRICE
                    .column()
                    .nullable(false),
            InvoiceLine.QUANTITY
                    .column()
                    .nullable(false)
                    .defaultValue(1),
            InvoiceLine.TOTAL
                    .derivedAttribute(new InvoiceLineTotalProvider(),
                            InvoiceLine.QUANTITY, InvoiceLine.UNITPRICE))
            .tableName("chinook.invoiceline")
            .keyGenerator(identity()));
  }
  // end::invoiceLine[]

  // tag::playlist[]
  void playlist() {
    add(Playlist.TYPE.define(
            Playlist.ID
                    .primaryKeyColumn(),
            Playlist.NAME
                    .column()
                    .searchColumn(true)
                    .nullable(false)
                    .maximumLength(120))
            .tableName("chinook.playlist")
            .keyGenerator(identity())
            .orderBy(ascending(Playlist.NAME))
            .stringFactory(Playlist.NAME));

    add(Playlist.RANDOM_PLAYLIST, new CreateRandomPlaylistFunction(entities()));
  }
  // end::playlist[]

  // tag::playlistTrack[]
  void playlistTrack() {
    add(PlaylistTrack.TYPE.define(
            PlaylistTrack.ID
                    .primaryKeyColumn(),
            PlaylistTrack.PLAYLIST_ID
                    .column()
                    .nullable(false),
            PlaylistTrack.PLAYLIST_FK
                    .foreignKey(),
            PlaylistTrack.ARTIST_DENORM
                    .denormalizedAttribute(PlaylistTrack.ALBUM_DENORM, Album.ARTIST_FK),
            PlaylistTrack.TRACK_ID
                    .column()
                    .nullable(false),
            PlaylistTrack.TRACK_FK
                    .foreignKey()
                    .fetchDepth(3),
            PlaylistTrack.ALBUM_DENORM
                    .denormalizedAttribute(PlaylistTrack.TRACK_FK, Track.ALBUM_FK))
            .tableName("chinook.playlisttrack")
            .keyGenerator(identity())
            .stringFactory(StringFactory.builder()
                    .value(PlaylistTrack.PLAYLIST_FK)
                    .text(" - ")
                    .value(PlaylistTrack.TRACK_FK)
                    .build()));
  }
  // end::playlistTrack[]

  // tag::updateTotalsFunction[]
  private static final class UpdateTotalsFunction implements DatabaseFunction<EntityConnection, Collection<Long>, Collection<Entity>> {

    @Override
    public Collection<Entity> execute(EntityConnection connection,
                                      Collection<Long> invoiceIds) throws DatabaseException {
      return connection.updateSelect(Entity.castTo(Invoice.class,
                      connection.select(where(column(Invoice.ID).in(invoiceIds))
                              .forUpdate()
                              .fetchDepth(0)
                              .build()))
              .stream()
              .peek(Invoice::updateTotal)
              .filter(Invoice::isModified)
              .collect(toList()));
    }
  }
  // end::updateTotalsFunction[]

  // tag::createRandomPlaylistFunction[]
  private static final class CreateRandomPlaylistFunction implements DatabaseFunction<EntityConnection, RandomPlaylistParameters, Entity> {

    private final Entities entities;

    private CreateRandomPlaylistFunction(Entities entities) {
      this.entities = entities;
    }

    @Override
    public Entity execute(EntityConnection connection,
                          RandomPlaylistParameters parameters) throws DatabaseException {
      List<Long> trackIds = randomTrackIds(connection, parameters.noOfTracks(), parameters.genres());

      return insertPlaylist(connection, parameters.playlistName(), trackIds);
    }

    private Entity insertPlaylist(EntityConnection connection, String playlistName,
                                  List<Long> trackIds) throws DatabaseException {
      Entity.Key playlistKey = connection.insert(createPlaylist(playlistName));

      connection.insert(createPlaylistTracks(playlistKey.get(), trackIds));

      return connection.select(playlistKey);
    }

    private Entity createPlaylist(String playlistName) {
      return entities.builder(Playlist.TYPE)
              .with(Playlist.NAME, playlistName)
              .build();
    }

    private List<Entity> createPlaylistTracks(Long playlistId, List<Long> trackIds) {
      return trackIds.stream()
              .map(trackId -> createPlaylistTrack(playlistId, trackId))
              .toList();
    }

    private Entity createPlaylistTrack(Long playlistId, Long trackId) {
      return entities.builder(PlaylistTrack.TYPE)
              .with(PlaylistTrack.PLAYLIST_ID, playlistId)
              .with(PlaylistTrack.TRACK_ID, trackId)
              .build();
    }

    private static List<Long> randomTrackIds(EntityConnection connection, int noOfTracks,
                                             Collection<Entity> genres) throws DatabaseException {
      return connection.select(Track.ID,
              where(foreignKey(Track.GENRE_FK).in(genres))
                      .orderBy(ascending(Track.RANDOM))
                      .limit(noOfTracks)
                      .build());
    }
  }
  // end::createRandomPlaylistFunction[]

  // tag::raisePriceFunction[]
  private static final class RaisePriceFunction implements DatabaseFunction<EntityConnection, RaisePriceParameters, Collection<Entity>> {

    @Override
    public Collection<Entity> execute(EntityConnection entityConnection,
                                      RaisePriceParameters parameters) throws DatabaseException {
      Select select = where(column(Track.ID).in(parameters.trackIds()))
              .forUpdate()
              .build();

      return entityConnection.updateSelect(Entity.castTo(Track.class,
                      entityConnection.select(select)).stream()
              .peek(track -> track.raisePrice(parameters.priceIncrease()))
              .toList());
    }
  }
  // end::raisePriceFunction[]
}