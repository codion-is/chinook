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
import is.codion.framework.db.condition.SelectCondition;
import is.codion.framework.demos.chinook.domain.api.Chinook;
import is.codion.framework.demos.chinook.domain.api.Chinook.Playlist.RandomPlaylistParameters;
import is.codion.framework.demos.chinook.domain.api.Chinook.Track.RaisePriceParameters;
import is.codion.framework.domain.DefaultDomain;
import is.codion.framework.domain.entity.Entities;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.Key;
import is.codion.framework.domain.entity.OrderBy;
import is.codion.framework.domain.entity.StringFactory;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;

import static is.codion.framework.db.condition.Condition.*;
import static is.codion.framework.domain.entity.EntityDefinition.definition;
import static is.codion.framework.domain.entity.KeyGenerator.identity;
import static is.codion.framework.domain.entity.OrderBy.ascending;
import static is.codion.framework.domain.property.Property.*;
import static is.codion.plugin.jasperreports.model.JasperReports.classPathReport;
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
    add(definition(
            primaryKeyProperty(Artist.ID),
            columnProperty(Artist.NAME)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(120),
            subqueryProperty(Artist.NUMBER_OF_ALBUMS, """
                            select count(*)
                            from chinook.album
                            where album.artistid = artist.artistid"""),
            subqueryProperty(Artist.NUMBER_OF_TRACKS, """
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
    add(definition(
            primaryKeyProperty(Album.ID),
            columnProperty(Album.ARTIST_ID)
                    .nullable(false),
            foreignKeyProperty(Album.ARTIST_FK)
                    .selectAttributes(Artist.NAME),
            columnProperty(Album.TITLE)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(160),
            blobProperty(Album.COVER)
                    .eagerlyLoaded(true)
                    .format(new CoverFormatter()),
            derivedProperty(Album.COVERIMAGE,
                    new CoverArtImageProvider(), Album.COVER),
            subqueryProperty(Album.NUMBER_OF_TRACKS, """
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
    add(definition(
            primaryKeyProperty(Employee.ID),
            columnProperty(Employee.LASTNAME)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(20),
            columnProperty(Employee.FIRSTNAME)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(20),
            columnProperty(Employee.TITLE)
                    .maximumLength(30),
            columnProperty(Employee.REPORTSTO),
            foreignKeyProperty(Employee.REPORTSTO_FK)
                    .selectAttributes(Employee.FIRSTNAME, Employee.LASTNAME),
            columnProperty(Employee.BIRTHDATE),
            columnProperty(Employee.HIREDATE)
                    .localeDateTimePattern(LocaleDateTimePattern.builder()
                            .delimiterDot()
                            .yearFourDigits()
                            .build()),
            columnProperty(Employee.ADDRESS)
                    .maximumLength(70),
            columnProperty(Employee.CITY)
                    .maximumLength(40),
            columnProperty(Employee.STATE)
                    .maximumLength(40),
            columnProperty(Employee.COUNTRY)
                    .maximumLength(40),
            columnProperty(Employee.POSTALCODE)
                    .maximumLength(10),
            columnProperty(Employee.PHONE)
                    .maximumLength(24),
            columnProperty(Employee.FAX)
                    .maximumLength(24),
            columnProperty(Employee.EMAIL)
                    .searchProperty(true)
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
    add(definition(
            primaryKeyProperty(Customer.ID),
            columnProperty(Customer.LASTNAME)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(20),
            columnProperty(Customer.FIRSTNAME)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(40),
            columnProperty(Customer.COMPANY)
                    .maximumLength(80),
            columnProperty(Customer.ADDRESS)
                    .maximumLength(70),
            columnProperty(Customer.CITY)
                    .maximumLength(40),
            columnProperty(Customer.STATE)
                    .maximumLength(40),
            columnProperty(Customer.COUNTRY)
                    .maximumLength(40),
            columnProperty(Customer.POSTALCODE)
                    .maximumLength(10),
            columnProperty(Customer.PHONE)
                    .maximumLength(24),
            columnProperty(Customer.FAX)
                    .maximumLength(24),
            columnProperty(Customer.EMAIL)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(60),
            columnProperty(Customer.SUPPORTREP_ID),
            foreignKeyProperty(Customer.SUPPORTREP_FK)
                    .selectAttributes(Employee.FIRSTNAME, Employee.LASTNAME))
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
    add(definition(
            primaryKeyProperty(Genre.ID),
            columnProperty(Genre.NAME)
                    .searchProperty(true)
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
    add(definition(
            primaryKeyProperty(MediaType.ID),
            columnProperty(MediaType.NAME)
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
    add(definition(
            primaryKeyProperty(Track.ID),
            denormalizedProperty(Track.ARTIST_DENORM,
                    Track.ALBUM_FK, Album.ARTIST_FK),
            columnProperty(Track.ALBUM_ID),
            foreignKeyProperty(Track.ALBUM_FK)
                    .selectAttributes(Album.ARTIST_FK, Album.TITLE)
                    .fetchDepth(2),
            columnProperty(Track.NAME)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(200),
            columnProperty(Track.GENRE_ID),
            foreignKeyProperty(Track.GENRE_FK),
            columnProperty(Track.COMPOSER)
                    .maximumLength(220),
            columnProperty(Track.MEDIATYPE_ID)
                    .nullable(false),
            foreignKeyProperty(Track.MEDIATYPE_FK),
            columnProperty(Track.MILLISECONDS)
                    .nullable(false)
                    .format(NumberFormat.getIntegerInstance()),
            derivedProperty(Track.MINUTES_SECONDS_DERIVED,
                    new TrackMinSecProvider(), Track.MILLISECONDS),
            columnProperty(Track.BYTES)
                    .format(NumberFormat.getIntegerInstance()),
            columnProperty(Track.UNITPRICE)
                    .nullable(false)
                    .maximumFractionDigits(2),
            columnProperty(Track.RANDOM)
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
    add(definition(
            primaryKeyProperty(Invoice.ID),
            columnProperty(Invoice.CUSTOMER_ID)
                    .nullable(false),
            foreignKeyProperty(Invoice.CUSTOMER_FK)
                    .selectAttributes(Customer.FIRSTNAME, Customer.LASTNAME, Customer.EMAIL),
            columnProperty(Invoice.DATE)
                    .nullable(false)
                    .defaultValueSupplier(Invoice.DATE_DEFAULT_VALUE)
                    .localeDateTimePattern(LocaleDateTimePattern.builder()
                            .delimiterDot()
                            .yearFourDigits()
                            .build()),
            columnProperty(Invoice.BILLINGADDRESS)
                    .maximumLength(70),
            columnProperty(Invoice.BILLINGCITY)
                    .maximumLength(40),
            columnProperty(Invoice.BILLINGSTATE)
                    .maximumLength(40),
            columnProperty(Invoice.BILLINGCOUNTRY)
                    .maximumLength(40),
            columnProperty(Invoice.BILLINGPOSTALCODE)
                    .maximumLength(10),
            columnProperty(Invoice.TOTAL)
                    .nullable(false)
                    .maximumFractionDigits(2),
            subqueryProperty(Invoice.CALCULATED_TOTAL, """
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
    add(definition(
            primaryKeyProperty(InvoiceLine.ID),
            columnProperty(InvoiceLine.INVOICE_ID)
                    .nullable(false),
            foreignKeyProperty(InvoiceLine.INVOICE_FK)
                    .fetchDepth(0),
            columnProperty(InvoiceLine.TRACK_ID)
                    .nullable(false),
            foreignKeyProperty(InvoiceLine.TRACK_FK)
                    .selectAttributes(Track.NAME, Track.UNITPRICE),
            columnProperty(InvoiceLine.UNITPRICE)
                    .nullable(false),
            columnProperty(InvoiceLine.QUANTITY)
                    .nullable(false)
                    .defaultValue(1),
            derivedProperty(InvoiceLine.TOTAL, new InvoiceLineTotalProvider(),
                    InvoiceLine.QUANTITY, InvoiceLine.UNITPRICE))
            .tableName("chinook.invoiceline")
            .keyGenerator(identity()));
  }
  // end::invoiceLine[]

  // tag::playlist[]
  void playlist() {
    add(definition(
            primaryKeyProperty(Playlist.ID),
            columnProperty(Playlist.NAME)
                    .searchProperty(true)
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
    add(definition(
            primaryKeyProperty(PlaylistTrack.ID),
            columnProperty(PlaylistTrack.PLAYLIST_ID)
                    .nullable(false),
            foreignKeyProperty(PlaylistTrack.PLAYLIST_FK),
            denormalizedProperty(PlaylistTrack.ARTIST_DENORM,
                    PlaylistTrack.ALBUM_DENORM, Album.ARTIST_FK),
            columnProperty(PlaylistTrack.TRACK_ID)
                    .nullable(false),
            foreignKeyProperty(PlaylistTrack.TRACK_FK)
                    .fetchDepth(3),
            denormalizedProperty(PlaylistTrack.ALBUM_DENORM,
                    PlaylistTrack.TRACK_FK, Track.ALBUM_FK))
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
      return connection.update(Entity.castTo(Invoice.class,
                      connection.select(where(attribute(Invoice.ID).in(invoiceIds))
                              .selectBuilder()
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
      Key playlistKey = connection.insert(createPlaylist(playlistName));

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
      return connection.select(Track.ID, where(foreignKey(Track.GENRE_FK).in(genres))
              .selectBuilder()
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
      SelectCondition selectCondition = where(attribute(Track.ID).in(parameters.trackIds()))
              .selectBuilder()
              .forUpdate()
              .build();

      return entityConnection.update(Entity.castTo(Track.class,
                      entityConnection.select(selectCondition)).stream()
              .peek(track -> track.raisePrice(parameters.priceIncrease()))
              .toList());
    }
  }
  // end::raisePriceFunction[]
}