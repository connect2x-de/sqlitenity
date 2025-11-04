@file:OptIn(ExperimentalAtomicApi::class)

package net.folivo.sqlitenity.bundled

import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import net.folivo.sqlitenity.api.ColumnType
import net.folivo.sqlitenity.api.SQLitenityStatement
import net.folivo.sqlitenity.api.Step
import net.folivo.sqlitenity.bindings.ColumnType as BindingsColumnType
import net.folivo.sqlitenity.bindings.Statement
import net.folivo.sqlitenity.bindings.Step as BindingsStep
import net.folivo.sqlitenity.bindings.bindBlob
import net.folivo.sqlitenity.bindings.bindDouble
import net.folivo.sqlitenity.bindings.bindLong
import net.folivo.sqlitenity.bindings.bindNull
import net.folivo.sqlitenity.bindings.bindText
import net.folivo.sqlitenity.bindings.clearBindings
import net.folivo.sqlitenity.bindings.columnBlob
import net.folivo.sqlitenity.bindings.columnCount
import net.folivo.sqlitenity.bindings.columnDouble
import net.folivo.sqlitenity.bindings.columnLong
import net.folivo.sqlitenity.bindings.columnName
import net.folivo.sqlitenity.bindings.columnText
import net.folivo.sqlitenity.bindings.columnType
import net.folivo.sqlitenity.bindings.finalize
import net.folivo.sqlitenity.bindings.reset
import net.folivo.sqlitenity.bindings.step

class BundledSQLitenityStatement internal constructor(private val statement: Statement) :
    SQLitenityStatement {

    private val isClosed = AtomicBoolean(false)

    override fun bindBlob(index: Int, value: ByteArray) {
        checkNotClosed()
        rethrow { bindBlob(statement, index, value) }
    }

    override fun bindDouble(index: Int, value: Double) {
        checkNotClosed()
        rethrow { bindDouble(statement, index, value) }
    }

    override fun bindLong(index: Int, value: Long) {
        checkNotClosed()
        rethrow { bindLong(statement, index, value) }
    }

    override fun bindText(index: Int, value: String) {
        checkNotClosed()
        rethrow { bindText(statement, index, value) }
    }

    override fun bindNull(index: Int) {
        checkNotClosed()
        rethrow { bindNull(statement, index) }
    }

    override fun getBlob(index: Int): ByteArray {

        checkNotClosed()

        return rethrow { columnBlob(statement, index) }
    }

    override fun getDouble(index: Int): Double {
        checkNotClosed()
        return rethrow { columnDouble(statement, index) }
    }

    override fun getLong(index: Int): Long {
        checkNotClosed()

        return rethrow { columnLong(statement, index) }
    }

    override fun getText(index: Int): String {
        checkNotClosed()
        return rethrow { columnText(statement, index) }
    }

    override fun getColumnCount(): Int {
        checkNotClosed()
        return rethrow { columnCount(statement) }
    }

    override fun getColumnName(index: Int): String {
        checkNotClosed()
        return rethrow { columnName(statement, index) }
    }

    override fun getColumnType(index: Int): ColumnType {
        checkNotClosed()
        return when (rethrow { columnType(statement, index) }) {
            BindingsColumnType.Integer -> ColumnType.Integer
            BindingsColumnType.Text -> ColumnType.Text
            BindingsColumnType.Blob -> ColumnType.Blob
            BindingsColumnType.Float -> ColumnType.Float
            BindingsColumnType.Null -> ColumnType.Null
        }
    }

    override fun step(): Step {
        checkNotClosed()
        return when (rethrow { step(statement) }) {
            BindingsStep.Row -> Step.Row
            BindingsStep.Done -> Step.Done
        }
    }

    override fun reset() {
        checkNotClosed()
        rethrow { reset(statement) }
    }

    override fun clearBindings() {
        checkNotClosed()
        rethrow { clearBindings(statement) }
    }

    override fun close() {
        if (!isClosed.exchange(true)) rethrow { finalize(statement) }
    }

    private fun checkNotClosed() {
        check(!isClosed.load())
    }
}
