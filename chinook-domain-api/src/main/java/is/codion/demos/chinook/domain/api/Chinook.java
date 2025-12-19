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
 * Copyright (c) 2004 - 2025, Björn Darri Sigurðsson.
 */
package is.codion.demos.chinook.domain.api;

import is.codion.common.db.operation.FunctionType;
import is.codion.common.db.operation.ProcedureType;
import is.codion.common.utilities.TypeReference;
import is.codion.framework.db.EntityConnection;
import is.codion.framework.domain.DomainType;
import is.codion.framework.domain.entity.Entities;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.EntityType;
import is.codion.framework.domain.entity.EntityValidator;
import is.codion.framework.domain.entity.attribute.Attribute;
import is.codion.framework.domain.entity.attribute.AttributeDefinition.ValueSupplier;
import is.codion.framework.domain.entity.attribute.Column;
import is.codion.framework.domain.entity.attribute.DerivedValue;
import is.codion.framework.domain.entity.attribute.ForeignKey;
import is.codion.framework.domain.entity.condition.ConditionType;
import is.codion.framework.domain.entity.exception.ValidationException;
import is.codion.plugin.jasperreports.JRReportType;
import is.codion.plugin.jasperreports.JasperReports;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.regex.Pattern;

import static is.codion.common.db.operation.FunctionType.functionType;
import static is.codion.common.db.operation.ProcedureType.procedureType;
import static is.codion.framework.domain.DomainType.domainType;
import static java.util.Objects.requireNonNull;
import static java.util.ResourceBundle.getBundle;

// tag::chinook[]
public interface Chinook {

	DomainType DOMAIN = domainType(Chinook.class);
	// end::chinook[]

	// tag::artist[]
	interface Artist {
		EntityType TYPE = DOMAIN.entityType("chinook.artist", Artist.class);

		Column<Long> ID = TYPE.longColumn("id");
		Column<String> NAME = TYPE.stringColumn("name");
		Column<Integer> NUMBER_OF_ALBUMS = TYPE.integerColumn("number_of_albums");
		Column<Integer> NUMBER_OF_TRACKS = TYPE.integerColumn("number_of_tracks");

		static Dto dto(Entity artist) {
			return artist == null ? null :
							new Dto(artist.get(ID), artist.get(NAME));
		}

		record Dto(Long id, String name) {

			public Entity entity(Entities entities) {
				return entities.entity(TYPE)
								.with(ID, id)
								.with(NAME, name)
								.build();
			}
		}
	}
	// end::artist[]

	// tag::album[]
	interface Album {
		EntityType TYPE = DOMAIN.entityType("chinook.album", Album.class);

		Column<Long> ID = TYPE.longColumn("id");
		Column<String> TITLE = TYPE.stringColumn("title");
		Column<Long> ARTIST_ID = TYPE.longColumn("artist_id");
		Column<byte[]> COVER = TYPE.byteArrayColumn("cover");
		Column<Integer> NUMBER_OF_TRACKS = TYPE.integerColumn("number_of_tracks");
		Column<List<String>> TAGS = TYPE.column("tags", new TypeReference<>() {});
		Column<Integer> RATING = TYPE.integerColumn("rating");

		Column<LocalDateTime> INSERT_TIME = TYPE.localDateTimeColumn("insert_time");
		Column<String> INSERT_USER = TYPE.stringColumn("insert_user");

		ForeignKey ARTIST_FK = TYPE.foreignKey("artist_fk", ARTIST_ID, Artist.ID);

		static Dto dto(Entity album) {
			return album == null ? null :
							new Dto(album.get(ID), album.get(TITLE),
											Artist.dto(album.get(ARTIST_FK)));
		}

		record Dto(Long id, String title, Artist.Dto artist) {

			public Entity entity(Entities entities) {
				return entities.entity(TYPE)
								.with(ID, id)
								.with(TITLE, title)
								.with(ARTIST_FK, artist.entity(entities))
								.build();
			}
		}
	}
	// end::album[]

	// tag::employee[]
	interface Employee {
		EntityType TYPE = DOMAIN.entityType("chinook.employee", Employee.class);

		Column<Long> ID = TYPE.longColumn("id");
		Column<String> LASTNAME = TYPE.stringColumn("lastname");
		Column<String> FIRSTNAME = TYPE.stringColumn("firstname");
		Column<String> TITLE = TYPE.stringColumn("title");
		Column<Long> REPORTSTO = TYPE.longColumn("reportsto_id");
		Column<LocalDate> BIRTHDATE = TYPE.localDateColumn("birthdate");
		Column<LocalDate> HIREDATE = TYPE.localDateColumn("hiredate");
		Column<String> ADDRESS = TYPE.stringColumn("address");
		Column<String> CITY = TYPE.stringColumn("city");
		Column<String> STATE = TYPE.stringColumn("state");
		Column<String> COUNTRY = TYPE.stringColumn("country");
		Column<String> POSTALCODE = TYPE.stringColumn("postalcode");
		Column<String> PHONE = TYPE.stringColumn("phone");
		Column<String> FAX = TYPE.stringColumn("fax");
		Column<String> EMAIL = TYPE.stringColumn("email");

		Column<LocalDateTime> INSERT_TIME = TYPE.localDateTimeColumn("insert_time");
		Column<String> INSERT_USER = TYPE.stringColumn("insert_user");

		ForeignKey REPORTSTO_FK = TYPE.foreignKey("reportsto_fk", REPORTSTO, Employee.ID);
	}
	// end::employee[]

	// tag::customer[]
	interface Customer {
		EntityType TYPE = DOMAIN.entityType("chinook.customer", Customer.class);

		Column<Long> ID = TYPE.longColumn("id");
		Column<String> FIRSTNAME = TYPE.stringColumn("firstname");
		Column<String> LASTNAME = TYPE.stringColumn("lastname");
		Column<String> COMPANY = TYPE.stringColumn("company");
		Column<String> ADDRESS = TYPE.stringColumn("address");
		Column<String> CITY = TYPE.stringColumn("city");
		Column<String> STATE = TYPE.stringColumn("state");
		Column<String> COUNTRY = TYPE.stringColumn("country");
		Column<String> POSTALCODE = TYPE.stringColumn("postalcode");
		Column<String> PHONE = TYPE.stringColumn("phone");
		Column<String> FAX = TYPE.stringColumn("fax");
		Column<String> EMAIL = TYPE.stringColumn("email");
		Column<Long> SUPPORTREP_ID = TYPE.longColumn("supportrep_id");

		Column<LocalDateTime> INSERT_TIME = TYPE.localDateTimeColumn("insert_time");
		Column<String> INSERT_USER = TYPE.stringColumn("insert_user");

		ForeignKey SUPPORTREP_FK = TYPE.foreignKey("supportrep_fk", SUPPORTREP_ID, Employee.ID);

		JRReportType REPORT = JasperReports.reportType("customer_report");
	}
	// end::customer[]

	// tag::preferences[]
	interface Preferences {
		EntityType TYPE = DOMAIN.entityType("chinook.preferences", Preferences.class);

		Column<Long> CUSTOMER_ID = TYPE.longColumn("customer_id");
		Column<Long> PREFERRED_GENRE_ID = TYPE.longColumn("preferred_genre_id");
		Column<Boolean> NEWSLETTER = TYPE.booleanColumn("newsletter");

		ForeignKey CUSTOMER_FK = TYPE.foreignKey("customer_fk", CUSTOMER_ID, Customer.ID);
		ForeignKey PREFERRED_GENRE_FK = TYPE.foreignKey("preferred_genre_fk", PREFERRED_GENRE_ID, Genre.ID);
	}
	// end::preferences[]

	// tag::genre[]
	interface Genre {
		EntityType TYPE = DOMAIN.entityType("chinook.genre", Genre.class);

		Column<Long> ID = TYPE.longColumn("id");
		Column<String> NAME = TYPE.stringColumn("name");

		static Dto dto(Entity genre) {
			return genre == null ? null :
							new Dto(genre.get(ID), genre.get(NAME));
		}

		record Dto(Long id, String name) {

			public Entity entity(Entities entities) {
				return entities.entity(TYPE)
								.with(ID, id)
								.with(NAME, name)
								.build();
			}
		}
	}
	// end::genre[]

	// tag::mediaType[]
	interface MediaType {
		EntityType TYPE = DOMAIN.entityType("chinook.mediatype", MediaType.class);

		Column<Long> ID = TYPE.longColumn("id");
		Column<String> NAME = TYPE.stringColumn("name");

		static Dto dto(Entity mediaType) {
			return mediaType == null ? null :
							new Dto(mediaType.get(ID), mediaType.get(NAME));
		}

		record Dto(Long id, String name) {

			public Entity entity(Entities entities) {
				return entities.entity(TYPE)
								.with(ID, id)
								.with(NAME, name)
								.build();
			}
		}
	}
	// end::mediaType[]

	// tag::track[]
	interface Track {
		EntityType TYPE = DOMAIN.entityType("chinook.track", Track.class);

		Column<Long> ID = TYPE.longColumn("id");
		Column<String> NAME = TYPE.stringColumn("name");
		Column<Long> ALBUM_ID = TYPE.longColumn("album_id");
		Column<String> ARTIST_NAME = TYPE.stringColumn("artist_name");
		Column<Long> MEDIATYPE_ID = TYPE.longColumn("mediatype_id");
		Column<Long> GENRE_ID = TYPE.longColumn("genre_id");
		Column<String> COMPOSER = TYPE.stringColumn("composer");
		Column<Integer> MILLISECONDS = TYPE.integerColumn("milliseconds");
		Column<Integer> BYTES = TYPE.integerColumn("bytes");
		Column<Integer> RATING = TYPE.integerColumn("rating");
		Column<BigDecimal> UNITPRICE = TYPE.bigDecimalColumn("unitprice");
		Column<Integer> PLAY_COUNT = TYPE.integerColumn("play_count");
		Column<Void> RANDOM = TYPE.column("random()", Void.class);

		Column<LocalDateTime> INSERT_TIME = TYPE.localDateTimeColumn("insert_time");
		Column<String> INSERT_USER = TYPE.stringColumn("insert_user");

		ForeignKey ALBUM_FK = TYPE.foreignKey("album_fk", ALBUM_ID, Album.ID);
		ForeignKey MEDIATYPE_FK = TYPE.foreignKey("mediatype_fk", MEDIATYPE_ID, MediaType.ID);
		ForeignKey GENRE_FK = TYPE.foreignKey("genre_fk", GENRE_ID, Genre.ID);

		FunctionType<EntityConnection, RaisePriceParameters, Collection<Entity>> RAISE_PRICE = functionType("chinook.raise_price");

		ConditionType NOT_IN_PLAYLIST = TYPE.conditionType("not_in_playlist");

		static Dto dto(Entity track) {
			return track == null ? null :
							new Dto(track.get(ID), track.get(NAME),
											track.get(ARTIST_NAME),
											Album.dto(track.get(ALBUM_FK)),
											Genre.dto(track.get(GENRE_FK)),
											MediaType.dto(track.get(MEDIATYPE_FK)),
											track.get(MILLISECONDS),
											track.get(RATING),
											track.get(UNITPRICE),
											track.get(PLAY_COUNT));
		}

		record RaisePriceParameters(Collection<Long> trackIds, BigDecimal priceIncrease) implements Serializable {

			public RaisePriceParameters {
				requireNonNull(trackIds);
				requireNonNull(priceIncrease);
			}
		}

		record Dto(Long id, String name, String artistName, Album.Dto album,
							 Genre.Dto genre, MediaType.Dto mediaType,
							 Integer milliseconds, Integer rating,
							 BigDecimal unitPrice, Integer playCount) {

			public Entity entity(Entities entities) {
				return entities.entity(TYPE)
								.with(ID, id)
								.with(NAME, name)
								.with(ARTIST_NAME, artistName)
								.with(ALBUM_FK, album.entity(entities))
								.with(GENRE_FK, genre.entity(entities))
								.with(MEDIATYPE_FK, mediaType.entity(entities))
								.with(MILLISECONDS, milliseconds)
								.with(RATING, rating)
								.with(UNITPRICE, unitPrice)
								.with(PLAY_COUNT, playCount)
								.build();
			}
		}
	}
	// end::track[]

	// tag::invoice[]
	interface Invoice {
		EntityType TYPE = DOMAIN.entityType("chinook.invoice", Invoice.class);

		Column<Long> ID = TYPE.longColumn("id");
		Column<Long> CUSTOMER_ID = TYPE.longColumn("customer_id");
		Column<LocalDate> DATE = TYPE.localDateColumn("invoicedate");
		Column<String> BILLINGADDRESS = TYPE.stringColumn("billingaddress");
		Column<String> BILLINGCITY = TYPE.stringColumn("billingcity");
		Column<String> BILLINGSTATE = TYPE.stringColumn("billingstate");
		Column<String> BILLINGCOUNTRY = TYPE.stringColumn("billingcountry");
		Column<String> BILLINGPOSTALCODE = TYPE.stringColumn("billingpostalcode");
		Column<BigDecimal> TOTAL = TYPE.bigDecimalColumn("total");
		Column<BigDecimal> CALCULATED_TOTAL = TYPE.bigDecimalColumn("calculated_total");

		Column<LocalDateTime> INSERT_TIME = TYPE.localDateTimeColumn("insert_time");
		Column<String> INSERT_USER = TYPE.stringColumn("insert_user");

		ForeignKey CUSTOMER_FK = TYPE.foreignKey("customer_fk", CUSTOMER_ID, Customer.ID);

		ProcedureType<EntityConnection, Collection<Long>> UPDATE_TOTALS = procedureType("chinook.update_totals");

		ValueSupplier<LocalDate> DATE_DEFAULT_VALUE = LocalDate::now;
	}
	// end::invoice[]

	// tag::invoiceLine[]
	interface InvoiceLine {
		EntityType TYPE = DOMAIN.entityType("chinook.invoiceline", InvoiceLine.class);

		Column<Long> ID = TYPE.longColumn("id");
		Column<Long> INVOICE_ID = TYPE.longColumn("invoice_id");
		Column<Long> TRACK_ID = TYPE.longColumn("track_id");
		Column<BigDecimal> UNITPRICE = TYPE.bigDecimalColumn("unitprice");
		Column<Integer> QUANTITY = TYPE.integerColumn("quantity");
		Column<BigDecimal> TOTAL = TYPE.bigDecimalColumn("total");

		Column<LocalDateTime> INSERT_TIME = TYPE.localDateTimeColumn("insert_time");
		Column<String> INSERT_USER = TYPE.stringColumn("insert_user");

		ForeignKey INVOICE_FK = TYPE.foreignKey("invoice_fk", INVOICE_ID, Invoice.ID);
		ForeignKey TRACK_FK = TYPE.foreignKey("track_fk", TRACK_ID, Track.ID);
	}
	// end::invoiceLine[]

	// tag::playlist[]
	interface Playlist {
		EntityType TYPE = DOMAIN.entityType("chinook.playlist", Playlist.class);

		Column<Long> ID = TYPE.longColumn("id");
		Column<String> NAME = TYPE.stringColumn("name");

		FunctionType<EntityConnection, RandomPlaylistParameters, Entity> RANDOM_PLAYLIST = functionType("chinook.random_playlist");

		record RandomPlaylistParameters(String playlistName, Integer noOfTracks, Collection<Entity> genres) implements Serializable {}
	}
	// end::playlist[]

	// tag::playlistTrack[]
	interface PlaylistTrack {
		EntityType TYPE = DOMAIN.entityType("chinook.playlisttrack", PlaylistTrack.class);

		Column<Long> ID = TYPE.longColumn("id");
		Column<Long> PLAYLIST_ID = TYPE.longColumn("playlist_id");
		Column<Long> TRACK_ID = TYPE.longColumn("track_id");
		Attribute<Entity> ALBUM = TYPE.entityAttribute("album");
		Attribute<Entity> ARTIST = TYPE.entityAttribute("artist");

		ForeignKey PLAYLIST_FK = TYPE.foreignKey("playlist_fk", PLAYLIST_ID, Playlist.ID);
		ForeignKey TRACK_FK = TYPE.foreignKey("track_fk", TRACK_ID, Track.ID);
	}
	// end::playlistTrack[]

	// tag::artistRevenue[]
	interface ArtistRevenue {
		EntityType TYPE = DOMAIN.entityType("chinook.artist_revenue");

		Column<Long> ARTIST_ID = TYPE.longColumn("artist_id");
		Column<String> NAME = TYPE.stringColumn("name");
		Column<BigDecimal> TOTAL_REVENUE = TYPE.bigDecimalColumn("total_revenue");
	}
	// end::artistRevenue[]

	// tag::invoiceLineTotal[]
	final class InvoiceLineTotal
					implements DerivedValue<BigDecimal> {

		@Serial
		private static final long serialVersionUID = 1;

		@Override
		public BigDecimal from(SourceValues values) {
			Integer quantity = values.get(InvoiceLine.QUANTITY);
			BigDecimal unitPrice = values.get(InvoiceLine.UNITPRICE);
			if (unitPrice == null || quantity == null) {
				return null;
			}

			return unitPrice.multiply(BigDecimal.valueOf(quantity));
		}
	}
	// end::invoiceLineTotal[]

	// tag::customerFormatter[]
	final class CustomerFormatter
					implements Function<Entity, String>, Serializable {

		@Serial
		private static final long serialVersionUID = 1;

		private static final String LANGUAGE = Locale.getDefault().getLanguage();

		@Override
		public String apply(Entity customer) {
			return switch (LANGUAGE) {
				case "en" -> new StringBuilder()
								.append(customer.get(Customer.LASTNAME))
								.append(", ")
								.append(customer.get(Customer.FIRSTNAME))
								.toString();
				case "is" -> new StringBuilder()
								.append(customer.get(Customer.FIRSTNAME))
								.append(" ")
								.append(customer.get(Customer.LASTNAME))
								.toString();
				default -> throw new IllegalArgumentException("Unsupported language: " + LANGUAGE);
			};
		}
	}
	// end::customerFormatter[]

	// tag::coverFormat[]
	final class CoverFormat extends Format {

		private final NumberFormat kbFormat = NumberFormat.getIntegerInstance();

		@Override
		public StringBuffer format(Object value, StringBuffer toAppendTo, FieldPosition pos) {
			if (value != null) {
				toAppendTo.append(kbFormat.format(((byte[]) value).length / 1024) + " Kb");
			}

			return toAppendTo;
		}

		@Override
		public Object parseObject(String source, ParsePosition pos) {
			throw new UnsupportedOperationException();
		}
	}
	// end::coverFormat[]

	// tag::emailValidator[]
	final class EmailValidator implements EntityValidator, Serializable {

		private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(.+)$");
		private static final ResourceBundle BUNDLE = getBundle(Chinook.class.getName());

		private final Column<String> emailColumn;

		public EmailValidator(Column<String> emailColumn) {
			this.emailColumn = emailColumn;
		}

		@Override
		public void validate(Entity entity, Attribute<?> attribute) {
			EntityValidator.super.validate(entity, attribute);
			if (attribute.equals(emailColumn)) {
				validateEmail(entity.get(emailColumn));
			}
		}

		private void validateEmail(String email) {
			if (!EMAIL_PATTERN.matcher(email).matches()) {
				throw new ValidationException(emailColumn, email, BUNDLE.getString("invalid_email"));
			}
		}
	}
	// end::emailValidator[]
}