package de.connect2x.sqlitenity.compat

import androidx.sqlite.SQLiteDriver
import de.connect2x.sqlitenity.api.SQLitenityConnection
import de.connect2x.sqlitenity.api.SQLitenityDriver
import de.connect2x.sqlitenity.api.SQLitenityStatement

class SQLitenityCompatDriver<
    T : SQLitenityDriver<C, S>,
    C : SQLitenityConnection<S>,
    S : SQLitenityStatement,
>(val inner: T) : SQLiteDriver {

    override fun open(fileName: String): SQLitenityCompatConnection<C, S> =
        SQLitenityCompatConnection(rethrow { inner.open(fileName) })
}
