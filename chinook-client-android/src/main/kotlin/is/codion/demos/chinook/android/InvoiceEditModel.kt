package `is`.codion.demos.chinook.android

import `is`.codion.android.framework.model.AndroidEntityEditModel
import `is`.codion.android.framework.model.AndroidEntityEditor
import `is`.codion.demos.chinook.domain.api.Chinook.Invoice
import `is`.codion.demos.chinook.model.common.InvoiceEditConfig
import `is`.codion.framework.db.EntityConnection

class InvoiceEditModel(connection: EntityConnection) :
    AndroidEntityEditModel(Invoice.TYPE, connection),
    InvoiceEditConfig<AndroidEntityEditor> {

    init {
        configure()
    }
}