package net.folivo.sqlitenity.compat

import androidx.sqlite.SQLiteDriver
import net.folivo.sqlitenity.api.SQLitenityConnection
import net.folivo.sqlitenity.api.SQLitenityDriver
import net.folivo.sqlitenity.api.SQLitenityStatement

class SQLitenityCompatDriver<
    T : SQLitenityDriver<C, S>,
    C : SQLitenityConnection<S>,
    S : SQLitenityStatement,
>(val inner: T) : SQLiteDriver {

    override fun open(fileName: String): SQLitenityCompatConnection<C, S> =
        SQLitenityCompatConnection(inner.open(fileName))
}
