package `is`.codion.demos.chinook.android

import `is`.codion.android.framework.model.AndroidEntityEditModel
import `is`.codion.android.framework.model.AndroidEntityEditor
import `is`.codion.demos.chinook.domain.api.Chinook.Customer
import `is`.codion.demos.chinook.model.common.CustomerEditConfig
import `is`.codion.framework.db.EntityConnectionProvider

class CustomerEditModel(connectionProvider: EntityConnectionProvider) :
    AndroidEntityEditModel(Customer.TYPE, connectionProvider),
    CustomerEditConfig<AndroidEntityEditor> {

    init {
        configure()
    }
}