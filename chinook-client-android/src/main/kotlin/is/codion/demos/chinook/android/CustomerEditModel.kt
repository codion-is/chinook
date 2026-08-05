package `is`.codion.demos.chinook.android

import `is`.codion.android.framework.model.AndroidEntityEditModel
import `is`.codion.android.framework.model.AndroidEntityEditor
import `is`.codion.demos.chinook.domain.api.Chinook.Customer
import `is`.codion.demos.chinook.model.common.CustomerEditConfig
import `is`.codion.framework.db.EntityConnection

class CustomerEditModel(connection: EntityConnection) :
    AndroidEntityEditModel(Customer.TYPE, connection),
    CustomerEditConfig<AndroidEntityEditor> {

    init {
        configure()
    }
}