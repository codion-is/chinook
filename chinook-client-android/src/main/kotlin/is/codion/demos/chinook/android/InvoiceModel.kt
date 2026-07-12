package `is`.codion.demos.chinook.android

import `is`.codion.android.framework.model.AndroidEntityEditModel
import `is`.codion.android.framework.model.AndroidEntityEditor
import `is`.codion.android.framework.model.AndroidEntityModel
import `is`.codion.android.framework.model.AndroidEntityTableModel
import `is`.codion.demos.chinook.model.common.InvoiceConfig
import `is`.codion.framework.db.EntityConnectionProvider

class InvoiceModel(connectionProvider: EntityConnectionProvider) :
    AndroidEntityModel(InvoiceEditModel(connectionProvider)),
    InvoiceConfig<AndroidEntityModel, AndroidEntityEditModel, AndroidEntityTableModel, AndroidEntityEditor> {

    init {
        detail().add(AndroidEntityModel(InvoiceLineEditModel(connectionProvider))) // todo mirror swing model setup
        configure()
    }
}