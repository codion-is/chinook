package `is`.codion.demos.chinook.android

import `is`.codion.android.framework.model.AndroidEntityEditModel
import `is`.codion.android.framework.model.AndroidEntityEditor
import `is`.codion.demos.chinook.domain.api.Chinook.InvoiceLine
import `is`.codion.demos.chinook.model.common.InvoiceLineEditConfig
import `is`.codion.framework.db.EntityConnectionProvider

class InvoiceLineEditModel(connectionProvider: EntityConnectionProvider) :
    AndroidEntityEditModel(InvoiceLine.TYPE, connectionProvider),
    InvoiceLineEditConfig<AndroidEntityEditor> {

    init {
        configure()
    }
}