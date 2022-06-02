/*
 * Copyright (c) 2004 - 2021, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.chinook.domain;

import is.codion.common.db.exception.DatabaseException;
import is.codion.common.db.operation.DatabaseFunction;
import is.codion.common.db.result.ResultPacker;
import is.codion.common.formats.LocaleDateTimePattern;
import is.codion.framework.db.EntityConnection;
import is.codion.framework.db.condition.SelectCondition;
import is.codion.framework.db.local.LocalEntityConnection;
import is.codion.framework.demos.chinook.domain.api.Chinook;
import is.codion.framework.demos.chinook.domain.api.Chinook.Playlist.RandomPlaylistParameters;
import is.codion.framework.demos.chinook.domain.api.Chinook.Track.RaisePriceParameters;
import is.codion.framework.domain.DefaultDomain;
import is.codion.framework.domain.entity.Entities;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.Key;
import is.codion.framework.domain.entity.OrderBy;
import is.codion.framework.domain.entity.StringFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static is.codion.framework.db.condition.Conditions.where;
import static is.codion.framework.domain.entity.KeyGenerator.identity;
import static is.codion.framework.domain.entity.OrderBy.ascending;
import static is.codion.framework.domain.property.Properties.*;
import static is.codion.plugin.jasperreports.model.JasperReports.classPathReport;

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

  void artist() {
    define(Artist.TYPE, "chinook.artist",
            primaryKeyProperty(Artist.ID),
            columnProperty(Artist.NAME)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(120)
                    .preferredColumnWidth(160),
            subqueryProperty(Artist.NUMBER_OF_ALBUMS, """
                            select count(*)
                            from chinook.album
                            where album.artistid = artist.artistid"""),
            subqueryProperty(Artist.NUMBER_OF_TRACKS, """
                            select count(*)
                            from chinook.track
                            join chinook.album on track.albumid = album.albumid
                            where album.artistid = artist.artistid"""))
            .keyGenerator(identity())
            .orderBy(ascending(Artist.NAME))
            .stringFactory(Artist.NAME);
  }

  void album() {
    define(Album.TYPE, "chinook.album",
            primaryKeyProperty(Album.ID),
            columnProperty(Album.ARTIST_ID)
                    .nullable(false),
            foreignKeyProperty(Album.ARTIST_FK)
                    .selectAttributes(Artist.NAME)
                    .preferredColumnWidth(160),
            columnProperty(Album.TITLE)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(160)
                    .preferredColumnWidth(160),
            blobProperty(Album.COVER)
                    .eagerlyLoaded()
                    .format(new CoverFormatter()),
            derivedProperty(Album.COVERIMAGE,
                    new CoverArtImageProvider(), Album.COVER),
            subqueryProperty(Album.NUMBER_OF_TRACKS, """
                            select count(*)
                            from chinook.track
                            where track.albumid = album.albumid"""))
            .keyGenerator(identity())
            .orderBy(ascending(Album.ARTIST_ID, Album.TITLE))
            .stringFactory(Album.TITLE);
  }

  void employee() {
    define(Employee.TYPE, "chinook.employee",
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
                    .maximumLength(60))
            .keyGenerator(identity())
            .orderBy(ascending(Employee.LASTNAME, Employee.FIRSTNAME))
            .stringFactory(StringFactory.builder()
                    .value(Employee.LASTNAME)
                    .text(", ")
                    .value(Employee.FIRSTNAME));
  }

  void customer() {
    define(Customer.TYPE, "chinook.customer",
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
            .keyGenerator(identity())
            .orderBy(ascending(Customer.LASTNAME, Customer.FIRSTNAME))
            .stringFactory(new CustomerStringProvider());

    defineReport(Customer.REPORT, classPathReport(ChinookImpl.class, "customer_report.jasper"));
  }

  void genre() {
    define(Genre.TYPE, "chinook.genre",
            primaryKeyProperty(Genre.ID),
            columnProperty(Genre.NAME)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(120)
                    .preferredColumnWidth(160))
            .keyGenerator(identity())
            .orderBy(ascending(Genre.NAME))
            .stringFactory(Genre.NAME)
            .smallDataset(true);
  }

  void mediaType() {
    define(MediaType.TYPE, "chinook.mediatype",
            primaryKeyProperty(MediaType.ID),
            columnProperty(MediaType.NAME)
                    .nullable(false)
                    .maximumLength(120)
                    .preferredColumnWidth(160))
            .keyGenerator(identity())
            .stringFactory(MediaType.NAME)
            .smallDataset(true);
  }

  void track() {
    define(Track.TYPE, "chinook.track",
            primaryKeyProperty(Track.ID),
            denormalizedViewProperty(Track.ARTIST_DENORM,
                    Track.ALBUM_FK, Album.ARTIST_FK)
                    .preferredColumnWidth(160),
            columnProperty(Track.ALBUM_ID),
            foreignKeyProperty(Track.ALBUM_FK)
                    .selectAttributes(Album.ARTIST_FK, Album.TITLE)
                    .fetchDepth(2)
                    .preferredColumnWidth(160),
            columnProperty(Track.NAME)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(200)
                    .preferredColumnWidth(160),
            columnProperty(Track.GENRE_ID),
            foreignKeyProperty(Track.GENRE_FK),
            columnProperty(Track.COMPOSER)
                    .maximumLength(220)
                    .preferredColumnWidth(160),
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
                    .maximumFractionDigits(2))
            .keyGenerator(identity())
            .orderBy(ascending(Track.NAME))
            .stringFactory(Track.NAME);

    defineFunction(Track.RAISE_PRICE, new RaisePriceFunction());
  }

  void invoice() {
    define(Invoice.TYPE, "chinook.invoice",
            primaryKeyProperty(Invoice.ID),
            columnProperty(Invoice.CUSTOMER_ID)
                    .nullable(false),
            foreignKeyProperty(Invoice.CUSTOMER_FK)
                    .selectAttributes(Customer.FIRSTNAME, Customer.LASTNAME),
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
                    .maximumFractionDigits(2),
            subqueryProperty(Invoice.TOTAL_SUBQUERY, """
                            select sum(unitprice * quantity) from chinook.invoiceline
                            where invoiceid = invoice.invoiceid""")
                    .maximumFractionDigits(2)
                    .hidden(true))
            .keyGenerator(identity())
            .orderBy(OrderBy.builder()
                    .ascending(Invoice.CUSTOMER_ID)
                    .descending(Invoice.DATE)
                    .build())
            .stringFactory(Invoice.ID);

    defineFunction(Invoice.UPDATE_TOTALS, new UpdateTotalsFunction());
  }

  void invoiceLine() {
    define(InvoiceLine.TYPE, "chinook.invoiceline",
            primaryKeyProperty(InvoiceLine.ID),
            columnProperty(InvoiceLine.INVOICE_ID)
                    .nullable(false),
            foreignKeyProperty(InvoiceLine.INVOICE_FK)
                    .fetchDepth(0),
            columnProperty(InvoiceLine.TRACK_ID)
                    .nullable(false),
            foreignKeyProperty(InvoiceLine.TRACK_FK)
                    .selectAttributes(Track.NAME, Track.UNITPRICE)
                    .preferredColumnWidth(100),
            denormalizedProperty(InvoiceLine.UNITPRICE,
                    InvoiceLine.TRACK_FK, Track.UNITPRICE)
                    .nullable(false),
            columnProperty(InvoiceLine.QUANTITY)
                    .nullable(false)
                    .defaultValue(1),
            derivedProperty(InvoiceLine.TOTAL, new InvoiceLineTotalProvider(),
                    InvoiceLine.QUANTITY, InvoiceLine.UNITPRICE))
            .keyGenerator(identity());
  }

  void playlist() {
    define(Playlist.TYPE, "chinook.playlist",
            primaryKeyProperty(Playlist.ID),
            columnProperty(Playlist.NAME)
                    .searchProperty(true)
                    .nullable(false)
                    .maximumLength(120)
                    .preferredColumnWidth(160))
            .keyGenerator(identity())
            .orderBy(ascending(Playlist.NAME))
            .stringFactory(Playlist.NAME);

    defineFunction(Playlist.RANDOM_PLAYLIST, new CreateRandomPlaylistFunction(getEntities()));
  }

  void playlistTrack() {
    define(PlaylistTrack.TYPE, "chinook.playlisttrack",
            primaryKeyProperty(PlaylistTrack.ID),
            columnProperty(PlaylistTrack.PLAYLIST_ID)
                    .nullable(false),
            foreignKeyProperty(PlaylistTrack.PLAYLIST_FK)
                    .preferredColumnWidth(120),
            denormalizedViewProperty(PlaylistTrack.ARTIST_DENORM,
                    PlaylistTrack.ALBUM_DENORM, Album.ARTIST_FK)
                    .preferredColumnWidth(160),
            columnProperty(PlaylistTrack.TRACK_ID)
                    .nullable(false),
            foreignKeyProperty(PlaylistTrack.TRACK_FK)
                    .fetchDepth(3)
                    .preferredColumnWidth(160),
            denormalizedViewProperty(PlaylistTrack.ALBUM_DENORM,
                    PlaylistTrack.TRACK_FK, Track.ALBUM_FK)
                    .preferredColumnWidth(160))
            .keyGenerator(identity())
            .stringFactory(StringFactory.builder()
                    .value(PlaylistTrack.PLAYLIST_FK)
                    .text(" - ")
                    .value(PlaylistTrack.TRACK_FK));
  }

  private static final class UpdateTotalsFunction implements DatabaseFunction<EntityConnection, Collection<Long>, Collection<Entity>> {

    @Override
    public Collection<Entity> execute(EntityConnection connection,
                                      Collection<Long> invoiceIds) throws DatabaseException {
      return connection.update(Entity.castTo(Invoice.class,
                      connection.select(where(Invoice.ID)
                              .equalTo(invoiceIds)
                              .toSelectCondition()
                              .forUpdate()
                              .fetchDepth(0)))
              .stream()
              .map(Invoice::updateTotal)
              .filter(Invoice::isModified)
              .collect(Collectors.toList()));
    }
  }

  private static final class CreateRandomPlaylistFunction implements DatabaseFunction<EntityConnection, RandomPlaylistParameters, Entity> {

    private static final ResultPacker<Long> TRACK_ID_PACKER = resultSet -> resultSet.getLong(1);
    private static final String TRACK_ID_QUERY = """
              select trackid
              from chinook.track
              order by random()
              limit""";

    private final Entities entities;

    private CreateRandomPlaylistFunction(Entities entities) {
      this.entities = entities;
    }

    @Override
    public Entity execute(EntityConnection connection,
                          RandomPlaylistParameters parameters) throws DatabaseException {
      connection.beginTransaction();
      try {
        Key playlistKey = insertPlaylistTracks(connection, parameters.playlistName(),
                getRandomTrackIds((LocalEntityConnection) connection, parameters.noOfTracks()));

        connection.commitTransaction();

        return connection.selectSingle(playlistKey);
      }
      catch (DatabaseException e) {
        connection.rollbackTransaction();
        throw e;
      }
    }

    private Key insertPlaylistTracks(EntityConnection connection, String playlistName,
                                     List<Long> trackIds) throws DatabaseException {
      Key playlistKey = connection.insert(createPlaylist(playlistName));

      connection.insert(createPlaylistTracks(playlistKey.get(), trackIds));

      return playlistKey;
    }

    private Entity createPlaylist(String playlistName) {
      return entities.builder(Playlist.TYPE)
              .with(Playlist.NAME, playlistName)
              .build();
    }

    private List<Entity> createPlaylistTracks(Long playlistId, List<Long> trackIds) {
      return trackIds.stream()
              .map(trackId -> createPlaylistTrack(playlistId, trackId))
              .collect(Collectors.toList());
    }

    private Entity createPlaylistTrack(Long playlistId, Long trackId) {
      return entities.builder(PlaylistTrack.TYPE)
              .with(PlaylistTrack.PLAYLIST_ID, playlistId)
              .with(PlaylistTrack.TRACK_ID, trackId)
              .build();
    }

    private static List<Long> getRandomTrackIds(LocalEntityConnection connection,
                                                int noOfTracks) throws DatabaseException {
      try (Statement statement = connection.getDatabaseConnection().getConnection().createStatement();
           ResultSet resultSet = statement.executeQuery(TRACK_ID_QUERY + " " + noOfTracks)) {
        return TRACK_ID_PACKER.pack(resultSet);
      }
      catch (SQLException e) {
        throw new DatabaseException(e);
      }
    }
  }

  private static final class RaisePriceFunction implements DatabaseFunction<EntityConnection, RaisePriceParameters, List<Entity>> {

    @Override
    public List<Entity> execute(EntityConnection entityConnection,
                                RaisePriceParameters parameters) throws DatabaseException {
      SelectCondition selectCondition =
              where(Track.ID).equalTo(parameters.trackIds()).toSelectCondition()
                      .forUpdate();

      return entityConnection.update(Entity.castTo(Track.class,
                      entityConnection.select(selectCondition)).stream()
              .map(track -> track.raisePrice(parameters.priceIncrease()))
              .collect(Collectors.toList()));
    }
  }
}