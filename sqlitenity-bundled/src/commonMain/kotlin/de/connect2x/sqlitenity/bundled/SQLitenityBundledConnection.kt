@file:OptIn(ExperimentalAtomicApi::class)

package de.connect2x.sqlitenity.bundled

import de.connect2x.sqlitenity.api.SQLitenityConnection
import de.connect2x.sqlitenity.bindings.Connection
import de.connect2x.sqlitenity.bindings.autoCommit
import de.connect2x.sqlitenity.bindings.close
import de.connect2x.sqlitenity.bindings.prepare
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

class SQLitenityBundledConnection internal constructor(private val connection: Connection) :
    SQLitenityConnection<SQLitenityBundledStatement> {

    private val isClosed = AtomicBoolean(false)

    override val autoCommitEnabled: Boolean
        get() {
            check(!isClosed.load())

            return rethrow { autoCommit(connection) }
        }

    override fun prepare(sql: String): SQLitenityBundledStatement {
        check(!isClosed.load())

        return SQLitenityBundledStatement(rethrow { prepare(connection, sql) })
    }

    override fun close() {
        if (!isClosed.exchange(true)) close(connection)
    }
}
