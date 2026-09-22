package de.connect2x.sqlitenity.compat

import androidx.sqlite.SQLiteStatement
import de.connect2x.sqlitenity.api.ColumnType
import de.connect2x.sqlitenity.api.SQLitenityStatement
import de.connect2x.sqlitenity.api.Step

class SQLitenityCompatStatement<T : SQLitenityStatement>(val inner: T) : SQLiteStatement {
    override fun bindBlob(index: Int, value: ByteArray) = rethrow { inner.bindBlob(index, value) }

    override fun bindDouble(index: Int, value: Double) = rethrow { inner.bindDouble(index, value) }

    override fun bindLong(index: Int, value: Long) = rethrow { inner.bindLong(index, value) }

    override fun bindText(index: Int, value: String) = rethrow { inner.bindText(index, value) }

    override fun bindNull(index: Int) = rethrow { inner.bindNull(index) }

    override fun getBlob(index: Int): ByteArray = rethrow { inner.getBlob(index) }

    override fun getDouble(index: Int): Double = rethrow { inner.getDouble(index) }

    override fun getLong(index: Int): Long = rethrow { inner.getLong(index) }

    override fun getText(index: Int): String = rethrow { inner.getText(index) }

    override fun isNull(index: Int): Boolean =
        rethrow { inner.getColumnType(index) } is ColumnType.Null

    override fun getColumnCount(): Int = rethrow { inner.getColumnCount() }

    override fun getColumnName(index: Int): String = rethrow { inner.getColumnName(index) }

    override fun getColumnType(index: Int): Int = Int.of(rethrow { inner.getColumnType(index) })

    override fun step(): Boolean = rethrow { inner.step() } is Step.Row

    override fun reset() = rethrow { inner.reset() }

    override fun clearBindings() = rethrow { inner.clearBindings() }

    override fun close() = rethrow { inner.close() }
}

private fun Int.Companion.of(columnType: ColumnType): Int =
    when (columnType) {
        ColumnType.Integer -> 1
        ColumnType.Float -> 2
        ColumnType.Text -> 3
        ColumnType.Blob -> 4
        ColumnType.Null -> 5
    }
