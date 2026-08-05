package `is`.codion.demos.chinook.android

import `is`.codion.android.framework.model.AndroidEntityEditModel
import `is`.codion.android.framework.model.AndroidEntityEditor
import `is`.codion.demos.chinook.domain.api.Chinook.InvoiceLine
import `is`.codion.demos.chinook.model.common.InvoiceLineEditConfig
import `is`.codion.framework.db.EntityConnection

class InvoiceLineEditModel(connection: EntityConnection) :
    AndroidEntityEditModel(InvoiceLine.TYPE, connection),
    InvoiceLineEditConfig<AndroidEntityEditor> {

    init {
        configure()
    }
}