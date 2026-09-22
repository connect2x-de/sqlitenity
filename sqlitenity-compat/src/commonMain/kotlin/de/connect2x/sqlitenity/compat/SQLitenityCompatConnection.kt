package de.connect2x.sqlitenity.compat

import androidx.sqlite.SQLiteConnection
import de.connect2x.sqlitenity.api.SQLitenityConnection
import de.connect2x.sqlitenity.api.SQLitenityStatement

class SQLitenityCompatConnection<C : SQLitenityConnection<S>, S : SQLitenityStatement>(
    val inner: C
) : SQLiteConnection {

    override fun inTransaction(): Boolean = rethrow { !inner.autoCommitEnabled }

    override fun prepare(sql: String): SQLitenityCompatStatement<S> =
        SQLitenityCompatStatement(rethrow { inner.prepare(sql) })

    override fun close() = rethrow { inner.close() }
}
