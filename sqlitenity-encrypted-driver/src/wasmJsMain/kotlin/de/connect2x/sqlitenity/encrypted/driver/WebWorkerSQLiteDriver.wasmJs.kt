package de.connect2x.sqlitenity.encrypted.driver

import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.w3c.dom.Worker

internal actual fun createNewWebWorkerSQLiteDriver(worker: Worker): WebWorkerSQLiteDriver {
    return WebWorkerSQLiteDriver(worker)
}
