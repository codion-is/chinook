package `is`.codion.demos.chinook.android.ui

import `is`.codion.android.framework.model.AndroidEntityModel
import `is`.codion.android.framework.ui.*
import `is`.codion.demos.chinook.android.model.ChinookApplicationModel
import `is`.codion.demos.chinook.android.model.ChinookConnection
import `is`.codion.demos.chinook.domain.api.Chinook.*
import `is`.codion.framework.db.EntityConnection

/**
 * The application view — the Android mirror of the Swing `ChinookAppPanel`: one [EntityView] per model, wired
 * into the same master/detail tree the model describes, plus the application-level configuration.
 *
 * The navigator renders one view at a time, moved through via the top-level root tabs + breadcrumb + drill
 * buttons + sibling tabs.
 *
 * A composition root rather than an override seam — see `.claude/design/android-ui-extension.md`. Both the model
 * and the views must exist before `super(...)` runs, so the wiring lives in the companion below rather than in
 * an override, and the constructor taking a connection delegates to the one taking a built model.
 */
class ChinookApplicationView private constructor(model: ChinookApplicationModel) :
    EntityApplicationView<ChinookApplicationModel>(model, entityViews(model), {
        // A custom full-screen landing shown in place of the CRUD app (demoing Config.main): a card per root,
        // tapped to navigate in. The three roots don't relate, so treat them as independent
        // (Config.independentRoots): no root tabs, and system back from any root returns to ChinookHome — you
        // pick the next root from home, not sideways.
        main { application -> ChinookHome(application) }
        independentRoots = true
    }) {

    constructor(connection: EntityConnection) : this(ChinookApplicationModel(connection))

    private companion object {

        init {
            // Application-wide form defaults, set before the first EntityEditView.Config is built.
            EntityEditView.Config.MODIFIED_WARNING.set(true)
            EntityEditView.Config.PERSIST_TOGGLE.set(true)
            // A delete the database refuses shows the records doing the refusing, instead of reporting a failure
            // there is nothing to be done about — Customer → Invoice → InvoiceLine is a two-level cascade to try it
            // on. Off by default (as in Swing), so an app opts in.
            ReferentialIntegrityErrorHandling.HANDLING.set(ReferentialIntegrityErrorHandling.DEPENDENCIES)
        }

        private fun entityViews(model: ChinookApplicationModel): List<EntityView> {
            val albumView = albumView(model)
            val playlistView = playlistView(model)
            val customerView = customerView(model)

            // Roots in navigation order, which is the landing card order — not the model order.
            return listOf(customerView, albumView, playlistView)
        }

        // Album.RATING is a read-only average (a subquery over its tracks), so it has no edit-form override; the
        // table shows it as a compact star meter via the table view's per-attribute renderer seam.
        private fun albumView(model: ChinookApplicationModel): EntityView {
            val albumModel = model.albumModel
            val albumView = EntityView(
                albumModel,
                edit = EntityEditView(albumModel.editModel()) {
                    photo(Album.COVER, gallery = true, settings = true)
                },
                table = EntityTableView(albumModel.tableModel()) {
                renderer(Album.RATING) { value, _ -> RatingStarsCell(value) }
            })
            albumView.detail().add(trackView(albumModel.detail().get(Track.TYPE)))

            return albumView
        }

        private fun trackView(trackModel: AndroidEntityModel) = EntityView(
            trackModel,
            edit = EntityEditView(trackModel.editModel()) {
                group("Main") {
                    attributes(Track.ALBUM_FK, Track.NAME, Track.GENRE_FK, Track.UNITPRICE)
                }
                remaining("Other")
                choiceButtons(Track.MEDIATYPE_FK)
                slider(Track.RATING)
            },
            table = EntityTableView(trackModel.tableModel()) {
                renderer(Track.RATING) { value, _ -> RatingStarsCell(value) }
            },
        )

        private fun playlistView(model: ChinookApplicationModel): EntityView {
            val playlistModel = model.playlistModel
            val playlistView = EntityView(playlistModel)
            playlistView.detail().add(EntityView(playlistModel.detail().get(PlaylistTrack.TYPE)))

            return playlistView
        }

        // In landscape, show the Customer edit form beside the table (edit left, table right); portrait stays
        // one-at-a-time.
        private fun customerView(model: ChinookApplicationModel): EntityView {
            val customerModel = model.customerModel
            val customerView = EntityView(
                customerModel,
                edit = EntityEditView(customerModel.editModel()) {
                    group("Name & company") {
                        attributes(
                            Customer.LASTNAME,
                            Customer.FIRSTNAME,
                            Customer.COMPANY,
                        )
                    }
                    group("Address") {
                        attributes(
                            Customer.ADDRESS,
                            Customer.CITY,
                            Customer.STATE,
                            Customer.COUNTRY,
                            Customer.POSTALCODE,
                        )
                    }
                    group("Phone & email") {
                        attributes(
                            Customer.PHONE,
                            Customer.EMAIL,
                            Customer.FAX,
                        )
                    }
                    group("Other") {
                        attributes(Customer.SUPPORTREP_FK)
                        // Edit the customer's Preferences (a one-to-one detail) inline on the customer form —
                        // persisted in the same transaction. The detail editor is registered by the shared
                        // CustomerEditConfig; here we just project which of its fields appear,
                        // mirroring the Swing CustomerEditPanel's preferences section.
                        detail(Preferences.CUSTOMER_FK) {
                            attributes(
                                Preferences.NEWSLETTER,
                                Preferences.PREFERRED_GENRE_FK,
                            )
                        }
                    }
                },
                // A custom table action (Config.control) — "Customer report…", enabled while customers are
                // selected, filling a PDF server-side and opening it in the device's PDF viewer (ReportLauncher).
                // The Android counterpart of the Swing CustomerTablePanel PRINT control, reached from the action
                // bar's actions overflow.
                table = EntityTableView(customerModel.tableModel()) {
                    // Reports are filled server-side and returned as PDF bytes, so they exist only over HTTP — the
                    // on-device database has no server to fill them and this client carries no JasperReports. See Reports.kt.
                    if (ChinookConnection.HTTP) {
                        control(customerReportControl(customerModel.tableModel()))
                    }
                },
            ) { splitInLandscape = true }

            val invoiceModel = customerModel.detail().get(Invoice.TYPE)
            // "Invoice…" (Invoice.REPORT_PDF, filled server-side) opening in the device's PDF viewer, offered from
            // both of this view's actions overflows — the table's over the selected invoices, one page each, and the
            // form's over the one record it is on. The same report, gated differently: see Reports.kt.
            val invoiceView = EntityView(
                invoiceModel,
                // The same invoice report as a form action (Config.control), so an invoice can be printed from the
                // record you are looking at and not only from the table — enabled once it exists, an uninserted
                // invoice having no id to fill a report against.
                edit = EntityEditView(invoiceModel.editModel()) {
                    if (ChinookConnection.HTTP) {
                        control(invoiceReportControl(invoiceModel.editModel()))
                    }
                },
                table = EntityTableView(invoiceModel.tableModel()) {
                    hidden(Invoice.INSERT_TIME, Invoice.INSERT_USER)
                    if (ChinookConnection.HTTP) {
                        control(invoiceReportControl(invoiceModel.tableModel()))
                    }
                },
            )
            invoiceView.detail().add(EntityView(invoiceModel.detail().get(InvoiceLine.TYPE)))
            customerView.detail().add(invoiceView)

            return customerView
        }
    }
}
