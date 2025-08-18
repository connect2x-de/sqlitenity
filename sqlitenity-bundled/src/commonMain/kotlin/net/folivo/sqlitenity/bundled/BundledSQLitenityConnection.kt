@file:OptIn(ExperimentalAtomicApi::class)

package net.folivo.sqlitenity.bundled

import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import net.folivo.sqlitenity.api.SQLitenityConnection
import net.folivo.sqlitenity.bindings.Connection
import net.folivo.sqlitenity.bindings.autoCommit
import net.folivo.sqlitenity.bindings.close
import net.folivo.sqlitenity.bindings.prepare

class BundledSQLitenityConnection internal constructor(private val connection: Connection) :
    SQLitenityConnection<BundledSQLitenityStatement> {

    private val isClosed = AtomicBoolean(false)

    override val autoCommitEnabled: Boolean
        get() {
            check(!isClosed.load())

            return rethrow { autoCommit(connection) }
        }

    override fun prepare(sql: String): BundledSQLitenityStatement {
        check(!isClosed.load())

        return BundledSQLitenityStatement(rethrow { prepare(connection, sql) })
    }

    override fun close() {
        if (!isClosed.exchange(true)) close(connection)
    }
}
