@file:OptIn(ExperimentalAtomicApi::class)

package de.connect2x.sqlitenity.bundled

import de.connect2x.sqlitenity.api.ColumnType
import de.connect2x.sqlitenity.api.SQLitenityStatement
import de.connect2x.sqlitenity.api.Step
import de.connect2x.sqlitenity.bindings.ColumnType as BindingsColumnType
import de.connect2x.sqlitenity.bindings.Statement
import de.connect2x.sqlitenity.bindings.Step as BindingsStep
import de.connect2x.sqlitenity.bindings.bindBlob
import de.connect2x.sqlitenity.bindings.bindDouble
import de.connect2x.sqlitenity.bindings.bindLong
import de.connect2x.sqlitenity.bindings.bindNull
import de.connect2x.sqlitenity.bindings.bindText
import de.connect2x.sqlitenity.bindings.clearBindings
import de.connect2x.sqlitenity.bindings.columnBlob
import de.connect2x.sqlitenity.bindings.columnCount
import de.connect2x.sqlitenity.bindings.columnDouble
import de.connect2x.sqlitenity.bindings.columnLong
import de.connect2x.sqlitenity.bindings.columnName
import de.connect2x.sqlitenity.bindings.columnText
import de.connect2x.sqlitenity.bindings.columnType
import de.connect2x.sqlitenity.bindings.finalize
import de.connect2x.sqlitenity.bindings.reset
import de.connect2x.sqlitenity.bindings.step
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

class SQLitenityBundledStatement internal constructor(private val statement: Statement) :
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
