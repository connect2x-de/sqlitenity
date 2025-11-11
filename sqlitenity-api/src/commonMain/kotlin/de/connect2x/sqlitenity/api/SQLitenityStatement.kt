package de.connect2x.sqlitenity.api

interface SQLitenityStatement : AutoCloseable {
    fun bindBlob(index: Int, value: ByteArray)

    fun bindDouble(index: Int, value: Double)

    fun bindLong(index: Int, value: Long)

    fun bindText(index: Int, value: String)

    fun bindNull(index: Int)

    fun getBlob(index: Int): ByteArray

    fun getDouble(index: Int): Double

    fun getLong(index: Int): Long

    fun getText(index: Int): String

    fun getColumnCount(): Int

    fun getColumnName(index: Int): String

    fun getColumnType(index: Int): ColumnType

    fun step(): Step

    fun reset()

    fun clearBindings()

    override fun close()

    companion object
}
