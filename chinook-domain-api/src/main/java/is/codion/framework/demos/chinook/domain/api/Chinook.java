/*
 * Copyright (c) 2004 - 2020, Björn Darri Sigurðsson. All Rights Reserved.
 */
package is.codion.framework.demos.chinook.domain.api;

import is.codion.common.db.operation.FunctionType;
import is.codion.common.db.operation.ProcedureType;
import is.codion.framework.db.EntityConnection;
import is.codion.framework.domain.DomainType;
import is.codion.framework.domain.entity.Attribute;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.EntityType;
import is.codion.framework.domain.entity.ForeignKey;
import is.codion.framework.domain.property.DerivedProperty;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static is.codion.common.db.operation.FunctionType.functionType;
import static is.codion.common.db.operation.ProcedureType.procedureType;
import static is.codion.framework.domain.DomainType.domainType;

public interface Chinook {

  DomainType DOMAIN = domainType("ChinookImpl");

  interface Artist {
    EntityType<Entity> TYPE = DOMAIN.entityType("artist@chinook", Artist.class.getName());

    Attribute<Long> ID = TYPE.longAttribute("artistid");
    Attribute<String> NAME = TYPE.stringAttribute("name");
    Attribute<Integer> NUMBER_OF_ALBUMS = TYPE.integerAttribute("number_of_albums");
    Attribute<Integer> NUMBER_OF_TRACKS = TYPE.integerAttribute("number_of_tracks");
  }

  interface Album {
    EntityType<Entity> TYPE = DOMAIN.entityType("album@chinook", Album.class.getName());

    Attribute<Long> ID = TYPE.longAttribute("albumid");
    Attribute<String> TITLE = TYPE.stringAttribute("title");
    Attribute<Long> ARTIST_ID = TYPE.longAttribute("artistid");
    Attribute<byte[]> COVER = TYPE.byteArrayAttribute("cover");
    Attribute<Image> COVERIMAGE = TYPE.attribute("coverimage", Image.class);
    Attribute<Integer> NUMBER_OF_TRACKS = TYPE.integerAttribute("number_of_tracks");

    ForeignKey ARTIST_FK = TYPE.foreignKey("artist_fk", Album.ARTIST_ID, Artist.ID);
  }

  interface Employee {
    EntityType<Entity> TYPE = DOMAIN.entityType("employee@chinook", Employee.class.getName());

    Attribute<Long> ID = TYPE.longAttribute("employeeid");
    Attribute<String> LASTNAME = TYPE.stringAttribute("lastname");
    Attribute<String> FIRSTNAME = TYPE.stringAttribute("firstname");
    Attribute<String> TITLE = TYPE.stringAttribute("title");
    Attribute<Long> REPORTSTO = TYPE.longAttribute("reportsto");
    Attribute<LocalDate> BIRTHDATE = TYPE.localDateAttribute("birthdate");
    Attribute<LocalDate> HIREDATE = TYPE.localDateAttribute("hiredate");
    Attribute<String> ADDRESS = TYPE.stringAttribute("address");
    Attribute<String> CITY = TYPE.stringAttribute("city");
    Attribute<String> STATE = TYPE.stringAttribute("state");
    Attribute<String> COUNTRY = TYPE.stringAttribute("country");
    Attribute<String> POSTALCODE = TYPE.stringAttribute("postalcode");
    Attribute<String> PHONE = TYPE.stringAttribute("phone");
    Attribute<String> FAX = TYPE.stringAttribute("fax");
    Attribute<String> EMAIL = TYPE.stringAttribute("email");

    ForeignKey REPORTSTO_FK = TYPE.foreignKey("reportsto_fk", Employee.REPORTSTO, Employee.ID);
  }

  interface Customer {
    EntityType<Entity> TYPE = DOMAIN.entityType("customer@chinook", Customer.class.getName());

    Attribute<Long> ID = TYPE.longAttribute("customerid");
    Attribute<String> FIRSTNAME = TYPE.stringAttribute("firstname");
    Attribute<String> LASTNAME = TYPE.stringAttribute("lastname");
    Attribute<String> COMPANY = TYPE.stringAttribute("company");
    Attribute<String> ADDRESS = TYPE.stringAttribute("address");
    Attribute<String> CITY = TYPE.stringAttribute("city");
    Attribute<String> STATE = TYPE.stringAttribute("state");
    Attribute<String> COUNTRY = TYPE.stringAttribute("country");
    Attribute<String> POSTALCODE = TYPE.stringAttribute("postalcode");
    Attribute<String> PHONE = TYPE.stringAttribute("phone");
    Attribute<String> FAX = TYPE.stringAttribute("fax");
    Attribute<String> EMAIL = TYPE.stringAttribute("email");
    Attribute<Long> SUPPORTREP_ID = TYPE.longAttribute("supportrepid");

    ForeignKey SUPPORTREP_FK = TYPE.foreignKey("supportrep_fk", Customer.SUPPORTREP_ID, Employee.ID);
  }

  interface Genre {
    EntityType<Entity> TYPE = DOMAIN.entityType("genre@chinook", Genre.class.getName());
    Attribute<Long> ID = TYPE.longAttribute("genreid");
    Attribute<String> NAME = TYPE.stringAttribute("name");
  }

  interface MediaType {
    EntityType<Entity> TYPE = DOMAIN.entityType("mediatype@chinook", MediaType.class.getName());
    Attribute<Long> ID = TYPE.longAttribute("mediatypeid");
    Attribute<String> NAME = TYPE.stringAttribute("name");
  }

  interface Track extends Entity {
    EntityType<Track> TYPE = DOMAIN.entityType("track@chinook", Track.class, Track.class.getName());

    Attribute<Long> ID = TYPE.longAttribute("trackid");
    Attribute<String> NAME = TYPE.stringAttribute("name");
    Attribute<Entity> ARTIST_DENORM = TYPE.entityAttribute("artist_denorm");
    Attribute<Long> ALBUM_ID = TYPE.longAttribute("albumid");
    Attribute<Long> MEDIATYPE_ID = TYPE.longAttribute("mediatypeid");
    Attribute<Long> GENRE_ID = TYPE.longAttribute("genreid");
    Attribute<String> COMPOSER = TYPE.stringAttribute("composer");
    Attribute<Integer> MILLISECONDS = TYPE.integerAttribute("milliseconds");
    Attribute<String> MINUTES_SECONDS_DERIVED = TYPE.stringAttribute("minutes_seconds_derived");
    Attribute<Integer> BYTES = TYPE.integerAttribute("bytes");
    Attribute<BigDecimal> UNITPRICE = TYPE.bigDecimalAttribute("unitprice");

    ForeignKey ALBUM_FK = TYPE.foreignKey("album_fk", Track.ALBUM_ID, Album.ID);
    ForeignKey MEDIATYPE_FK = TYPE.foreignKey("mediatype_fk", MEDIATYPE_ID, MediaType.ID);
    ForeignKey GENRE_FK = TYPE.foreignKey("genre_fk", Track.GENRE_ID, Genre.ID);

    FunctionType<EntityConnection, Object, List<Entity>> RAISE_PRICE = functionType("chinook.raise_price_function");

    default Track raisePrice(final BigDecimal priceIncrease) {
      put(UNITPRICE, get(UNITPRICE).add(priceIncrease));
      return this;
    }
  }

  interface Invoice extends Entity {
    EntityType<Invoice> TYPE = DOMAIN.entityType("invoice@chinook", Invoice.class, Invoice.class.getName());

    Attribute<Long> ID = TYPE.longAttribute("invoiceid");
    Attribute<Long> CUSTOMER_ID = TYPE.longAttribute("customerid");
    Attribute<LocalDateTime> INVOICEDATE = TYPE.localDateTimeAttribute("invoicedate");
    Attribute<String> BILLINGADDRESS = TYPE.stringAttribute("billingaddress");
    Attribute<String> BILLINGCITY = TYPE.stringAttribute("billingcity");
    Attribute<String> BILLINGSTATE = TYPE.stringAttribute("billingstate");
    Attribute<String> BILLINGCOUNTRY = TYPE.stringAttribute("billingcountry");
    Attribute<String> BILLINGPOSTALCODE = TYPE.stringAttribute("billingpostalcode");
    Attribute<BigDecimal> TOTAL = TYPE.bigDecimalAttribute("total");
    Attribute<BigDecimal> TOTAL_SUBQUERY = TYPE.bigDecimalAttribute("total_subquery");

    ForeignKey CUSTOMER_FK = TYPE.foreignKey("customer_fk", Invoice.CUSTOMER_ID, Customer.ID);

    ProcedureType<EntityConnection, Object> UPDATE_TOTALS = procedureType("chinook.update_totals_procedure");

    default Invoice updateTotal() {
      put(TOTAL, get(TOTAL_SUBQUERY));
      return this;
    }
  }

  interface InvoiceLine {
    EntityType<Entity> TYPE = DOMAIN.entityType("invoiceline@chinook", InvoiceLine.class.getName());

    Attribute<Long> ID = TYPE.longAttribute("invoicelineid");
    Attribute<Long> INVOICE_ID = TYPE.longAttribute("invoiceid");
    Attribute<Long> TRACK_ID = TYPE.longAttribute("trackid");
    Attribute<BigDecimal> UNITPRICE = TYPE.bigDecimalAttribute("unitprice");
    Attribute<Integer> QUANTITY = TYPE.integerAttribute("quantity");
    Attribute<BigDecimal> TOTAL = TYPE.bigDecimalAttribute("total");

    ForeignKey INVOICE_FK = TYPE.foreignKey("invoice_fk", InvoiceLine.INVOICE_ID, Invoice.ID);
    ForeignKey TRACK_FK = TYPE.foreignKey("track_fk", InvoiceLine.TRACK_ID, Track.ID);
  }

  interface Playlist {
    EntityType<Entity> TYPE = DOMAIN.entityType("playlist@chinook", Playlist.class.getName());

    Attribute<Long> ID = TYPE.longAttribute("playlistid");
    Attribute<String> NAME = TYPE.stringAttribute("name");
  }

  interface PlaylistTrack {
    EntityType<Entity> TYPE = DOMAIN.entityType("playlisttrack@chinook", PlaylistTrack.class.getName());

    Attribute<Long> ID = TYPE.longAttribute("playlisttrackid");
    Attribute<Long> PLAYLIST_ID = TYPE.longAttribute("playlistid");
    Attribute<Long> TRACK_ID = TYPE.longAttribute("trackid");
    Attribute<Entity> ALBUM_DENORM = TYPE.entityAttribute("album_denorm");
    Attribute<Entity> ARTIST_DENORM = TYPE.entityAttribute("artist_denorm");

    ForeignKey PLAYLIST_FK = TYPE.foreignKey("playlist_fk", PlaylistTrack.PLAYLIST_ID, Playlist.ID);
    ForeignKey TRACK_FK = TYPE.foreignKey("track_fk", PlaylistTrack.TRACK_ID, Track.ID);
  }

  static Integer getMinutes(final Integer milliseconds) {
    if (milliseconds == null) {
      return null;
    }

    return milliseconds / 1000 / 60;
  }

  static Integer getSeconds(final Integer milliseconds) {
    if (milliseconds == null) {
      return null;
    }

    return milliseconds / 1000 % 60;
  }

  static Integer getMilliseconds(final Integer minutes, final Integer seconds) {
    int milliseconds = minutes == null ? 0 : minutes * 60 * 1000;
    milliseconds += seconds == null ? 0 : seconds * 1000;

    return milliseconds == 0 ? null : milliseconds;
  }

  final class InvoiceLineTotalProvider
          implements DerivedProperty.Provider<BigDecimal> {

    private static final long serialVersionUID = 1;

    @Override
    public BigDecimal get(final DerivedProperty.SourceValues sourceValues) {
      Integer quantity = sourceValues.get(InvoiceLine.QUANTITY);
      BigDecimal unitPrice = sourceValues.get(InvoiceLine.UNITPRICE);
      if (unitPrice == null || quantity == null) {
        return null;
      }

      return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
  }

  final class TrackMinSecProvider
          implements DerivedProperty.Provider<String> {

    private static final long serialVersionUID = 1;

    @Override
    public String get(final DerivedProperty.SourceValues sourceValues) {
      Integer milliseconds = sourceValues.get(Track.MILLISECONDS);
      if (milliseconds == null || milliseconds <= 0) {
        return "";
      }

      return getMinutes(milliseconds) + " min " +
              getSeconds(milliseconds) + " sec";
    }
  }

  final class CoverArtImageProvider
          implements DerivedProperty.Provider<Image> {

    private static final long serialVersionUID = 1;

    @Override
    public Image get(final DerivedProperty.SourceValues sourceValues) {
      byte[] bytes = sourceValues.get(Album.COVER);
      if (bytes == null) {
        return null;
      }

      try {
        return ImageIO.read(new ByteArrayInputStream(bytes));
      }
      catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  final class CustomerStringProvider
          implements Function<Entity, String>, Serializable {

    private static final long serialVersionUID = 1;

    @Override
    public String apply(final Entity customer) {
      StringBuilder builder = new StringBuilder();
      if (customer.isNotNull(Customer.LASTNAME)) {
        builder.append(customer.get(Customer.LASTNAME));
      }
      if (customer.isNotNull(Customer.FIRSTNAME)) {
        builder.append(", ").append(customer.get(Customer.FIRSTNAME));
      }
      if (customer.isNotNull(Customer.EMAIL)) {
        builder.append(" <").append(customer.get(Customer.EMAIL)).append(">");
      }

      return builder.toString();
    }
  }
}