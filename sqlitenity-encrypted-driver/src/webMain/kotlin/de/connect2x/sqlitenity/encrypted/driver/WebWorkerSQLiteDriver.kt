package de.connect2x.sqlitenity.encrypted.driver

import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import de.connect2x.sqlitenity.web.worker.SQLitenityWebWorker
import org.w3c.dom.Worker

// This has to be a singleton for now as we make the worker itself a singleton.
// Each instance of the driver requires its own worker instance, otherwise
// the drivers conflict with each other because each driver has its own
// message id counter for the protocol.
// Theoretically we could give each driver its own worker, however as there
// is currently no easy way to close the driver again and this would require
// that each worker is initialized with an isolated SAHPool as those cannot
// be shared across workers.
internal object WebWorkerSQLiteDriver {
    val instance = createNewWebWorkerSQLiteDriver(SQLitenityWebWorker.instance)
}

internal expect fun createNewWebWorkerSQLiteDriver(worker: Worker): WebWorkerSQLiteDriver
