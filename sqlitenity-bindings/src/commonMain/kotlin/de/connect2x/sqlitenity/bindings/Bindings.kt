@file:Suppress("NOTHING_TO_INLINE")

package de.connect2x.sqlitenity.bindings

import de.connect2x.sqlitenity.interop.NativePointer
import de.connect2x.sqlitenity.interop.SQLITE_BLOB
import de.connect2x.sqlitenity.interop.SQLITE_DONE
import de.connect2x.sqlitenity.interop.SQLITE_FLOAT
import de.connect2x.sqlitenity.interop.SQLITE_INTEGER
import de.connect2x.sqlitenity.interop.SQLITE_NULL
import de.connect2x.sqlitenity.interop.SQLITE_ROW
import de.connect2x.sqlitenity.interop.SQLITE_TEXT
import de.connect2x.sqlitenity.interop.autoCommit
import de.connect2x.sqlitenity.interop.bindBlob
import de.connect2x.sqlitenity.interop.bindDouble
import de.connect2x.sqlitenity.interop.bindLong
import de.connect2x.sqlitenity.interop.bindNull
import de.connect2x.sqlitenity.interop.bindText
import de.connect2x.sqlitenity.interop.blobCopy
import de.connect2x.sqlitenity.interop.clearBindings
import de.connect2x.sqlitenity.interop.close
import de.connect2x.sqlitenity.interop.columnBlob
import de.connect2x.sqlitenity.interop.columnBlobSize
import de.connect2x.sqlitenity.interop.columnCount
import de.connect2x.sqlitenity.interop.columnDouble
import de.connect2x.sqlitenity.interop.columnLong
import de.connect2x.sqlitenity.interop.columnName
import de.connect2x.sqlitenity.interop.columnText
import de.connect2x.sqlitenity.interop.columnTextSize
import de.connect2x.sqlitenity.interop.columnType
import de.connect2x.sqlitenity.interop.finalize
import de.connect2x.sqlitenity.interop.nullPtr
import de.connect2x.sqlitenity.interop.open
import de.connect2x.sqlitenity.interop.prepare
import de.connect2x.sqlitenity.interop.reset
import de.connect2x.sqlitenity.interop.step
import de.connect2x.sqlitenity.interop.strLen
import de.connect2x.sqlitenity.interop.textCopy
import kotlin.jvm.JvmInline

@JvmInline
value class Connection
@PublishedApi
internal constructor(@PublishedApi internal val ptr: NativePointer) {
    companion object
}

@JvmInline
value class Statement
@PublishedApi
internal constructor(@PublishedApi internal val ptr: NativePointer) {
    companion object
}

class NativeException(val errorCode: Int, val errorMsg: String?) :
    RuntimeException(
        run {
            buildString {
                append("Error code: $errorCode")
                if (errorMsg != null) {
                    append(", message: $errorMsg")
                }
            }
        }
    ) {
    companion object
}

sealed class Step(val code: Int) {
    data object Row : Step(SQLITE_ROW)

    data object Done : Step(SQLITE_DONE)

    companion object
}

sealed class ColumnType(val value: Int) {
    data object Integer : ColumnType(SQLITE_INTEGER)

    data object Float : ColumnType(SQLITE_FLOAT)

    data object Text : ColumnType(SQLITE_TEXT)

    data object Blob : ColumnType(SQLITE_BLOB)

    data object Null : ColumnType(SQLITE_NULL)

    companion object
}

inline fun open(filename: String): Connection {
    val filenameBytes = filename.encodeToByteArray() + 0

    return Connection(
        withPointer {
            val rc = open(filename = toInterop(filenameBytes), ppConn = it)
            throwIfNotOk(rc)
        }
    )
}

inline fun prepare(conn: Connection, sql: String): Statement {
    val sqlChars = sql.toCharArray()

    return Statement(
        withPointer {
            val rc =
                prepare(
                    pConn = conn.ptr,
                    sql = toInterop(sqlChars),
                    size = sqlChars.size * 2,
                    ppStmt = it,
                )

            throwIfNotOk(rc)
        }
    )
}

inline fun autoCommit(conn: Connection): Boolean {
    return autoCommit(conn.ptr) != 0
}

inline fun close(conn: Connection): Unit = close(conn.ptr)

inline fun reset(stmt: Statement) {
    val rc = reset(stmt.ptr)
    throwIfNotOk(rc)
}

inline fun step(stmt: Statement): Step {
    return Step.of(step(stmt.ptr))
}

inline fun finalize(stmt: Statement) {
    val rc = finalize(stmt.ptr)
    throwIfNotOk(rc)
}

inline fun bindBlob(stmt: Statement, iCol: Int, blob: ByteArray) {
    interopScope {
        val rc = bindBlob(pStmt = stmt.ptr, iCol = iCol, blob = toInterop(blob), size = blob.size)
        throwIfNotOk(rc)
    }
}

inline fun bindText(stmt: Statement, iCol: Int, text: String) {
    val textChars = text.toCharArray()

    interopScope {
        val rc =
            bindText(
                pStmt = stmt.ptr,
                iCol = iCol,
                text = toInterop(textChars),
                size = textChars.size * 2,
            )
        throwIfNotOk(rc)
    }
}

inline fun bindDouble(stmt: Statement, iCol: Int, real: Double) {
    val rc = bindDouble(pStmt = stmt.ptr, iCol = iCol, real = real)
    throwIfNotOk(rc)
}

inline fun bindLong(stmt: Statement, iCol: Int, integer: Long) {
    val rc = bindLong(pStmt = stmt.ptr, iCol = iCol, integer = integer)
    throwIfNotOk(rc)
}

inline fun bindNull(stmt: Statement, iCol: Int) {
    val rc = bindNull(pStmt = stmt.ptr, iCol = iCol)
    throwIfNotOk(rc)
}

inline fun clearBindings(stmt: Statement) {
    val rc = clearBindings(pStmt = stmt.ptr)
    throwIfNotOk(rc)
}

inline fun columnCount(stmt: Statement): Int = columnCount(pStmt = stmt.ptr)

inline fun columnType(stmt: Statement, iCol: Int): ColumnType {
    throwIfNoRow(stmt)
    throwIfInvalidColumn(stmt, iCol)

    return ColumnType.of(columnType(stmt.ptr, iCol))
}

inline fun columnDouble(stmt: Statement, iCol: Int): Double {
    throwIfNoRow(stmt)
    throwIfInvalidColumn(stmt, iCol)

    return columnDouble(pStmt = stmt.ptr, iCol = iCol)
}

inline fun columnLong(stmt: Statement, iCol: Int): Long {
    throwIfNoRow(stmt)
    throwIfInvalidColumn(stmt, iCol)

    return columnLong(pStmt = stmt.ptr, iCol = iCol)
}

inline fun columnBlob(stmt: Statement, iCol: Int): ByteArray {
    throwIfNoRow(stmt)
    throwIfInvalidColumn(stmt, iCol)

    val blob = columnBlob(pStmt = stmt.ptr, iCol = iCol)
    if (blob == nullPtr) throwIfOutOfMemory(pStmt = stmt)

    val size = columnBlobSize(pStmt = stmt.ptr, iCol = iCol)
    if (size == 0) throwIfOutOfMemory(pStmt = stmt)

    return when (size) {
        0 -> ByteArray(0)
        else -> withBytes(size) { blobCopy(dst = it, src = blob, size = size) }
    }
}

inline fun columnText(stmt: Statement, iCol: Int): String {
    throwIfNoRow(stmt)
    throwIfInvalidColumn(stmt, iCol)

    val pText = columnText(pStmt = stmt.ptr, iCol = iCol)
    if (pText == nullPtr) throwIfOutOfMemory(pStmt = stmt)

    val size = columnTextSize(pStmt = stmt.ptr, iCol = iCol)
    if (size == 0) throwIfOutOfMemory(pStmt = stmt)

    return when (size) {
        0 -> ""
        else -> withText(size / 2) { textCopy(dst = it, src = pText, size = size) }.concatToString()
    }
}

inline fun columnName(stmt: Statement, iCol: Int): String {
    throwIfInvalidColumn(stmt, iCol)

    val pName = columnName(pStmt = stmt.ptr, iCol = iCol)
    if (pName == nullPtr) throwIfOutOfMemory(pStmt = stmt)

    return when (val size = strLen(pName)) {
        0 -> ""
        else -> withBytes(size) { blobCopy(dst = it, src = pName, size = size) }.decodeToString()
    }
}
