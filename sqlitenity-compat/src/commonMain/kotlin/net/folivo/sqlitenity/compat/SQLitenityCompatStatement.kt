package net.folivo.sqlitenity.compat

import androidx.sqlite.SQLiteStatement
import net.folivo.sqlitenity.api.ColumnType
import net.folivo.sqlitenity.api.SQLitenityStatement
import net.folivo.sqlitenity.api.Step

class SQLitenityCompatStatement<T : SQLitenityStatement>(val inner: T) : SQLiteStatement {
    override fun bindBlob(index: Int, value: ByteArray) = inner.bindBlob(index, value)

    override fun bindDouble(index: Int, value: Double) = inner.bindDouble(index, value)

    override fun bindLong(index: Int, value: Long) = inner.bindLong(index, value)

    override fun bindText(index: Int, value: String) = inner.bindText(index, value)

    override fun bindNull(index: Int) = inner.bindNull(index)

    override fun getBlob(index: Int): ByteArray = inner.getBlob(index)

    override fun getDouble(index: Int): Double = inner.getDouble(index)

    override fun getLong(index: Int): Long = inner.getLong(index)

    override fun getText(index: Int): String = inner.getText(index)

    override fun isNull(index: Int): Boolean = inner.getColumnType(index) is ColumnType.Null

    override fun getColumnCount(): Int = inner.getColumnCount()

    override fun getColumnName(index: Int): String = inner.getColumnName(index)

    override fun getColumnType(index: Int): Int = Int.of(inner.getColumnType(index))

    override fun step(): Boolean = inner.step() is Step.Row

    override fun reset() = inner.reset()

    override fun clearBindings() = inner.clearBindings()

    override fun close() = inner.close()
}

private fun Int.Companion.of(columnType: ColumnType): Int =
    when (columnType) {
        ColumnType.Integer -> 1
        ColumnType.Float -> 2
        ColumnType.Text -> 3
        ColumnType.Blob -> 4
        ColumnType.Null -> 5
    }
