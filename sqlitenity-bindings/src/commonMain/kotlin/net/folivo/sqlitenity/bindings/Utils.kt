@file:Suppress("NOTHING_TO_INLINE")

package net.folivo.sqlitenity.bindings

import net.folivo.sqlitenity.raw.bindings.OutOfMemoryError
import net.folivo.sqlitenity.raw.bindings.SQLITE_BLOB
import net.folivo.sqlitenity.raw.bindings.SQLITE_DONE
import net.folivo.sqlitenity.raw.bindings.SQLITE_FLOAT
import net.folivo.sqlitenity.raw.bindings.SQLITE_INTEGER
import net.folivo.sqlitenity.raw.bindings.SQLITE_MISUSE
import net.folivo.sqlitenity.raw.bindings.SQLITE_NULL
import net.folivo.sqlitenity.raw.bindings.SQLITE_OK
import net.folivo.sqlitenity.raw.bindings.SQLITE_RANGE
import net.folivo.sqlitenity.raw.bindings.SQLITE_ROW
import net.folivo.sqlitenity.raw.bindings.SQLITE_TEXT
import net.folivo.sqlitenity.raw.bindings.blobCopy
import net.folivo.sqlitenity.raw.bindings.errStr
import net.folivo.sqlitenity.raw.bindings.invalidColumn
import net.folivo.sqlitenity.raw.bindings.noRow
import net.folivo.sqlitenity.raw.bindings.nullPtr
import net.folivo.sqlitenity.raw.bindings.outOfMemory
import net.folivo.sqlitenity.raw.bindings.strLen

@PublishedApi
internal inline fun throwIfNotOk(rc: Int) {
    if (rc != SQLITE_OK) throwException(rc)
}

@PublishedApi
internal inline fun throwIfOutOfMemory(pStmt: Statement) {
    if (outOfMemory(pStmt)) throw OutOfMemoryError()
}

@PublishedApi
internal inline fun throwIfNoRow(pStmt: Statement) {
    if (noRow(pStmt)) throwException(SQLITE_MISUSE, "no row")
}

@PublishedApi
internal inline fun throwIfInvalidColumn(pStmt: Statement, iCol: Int) {
    if (invalidColumn(pStmt, iCol)) throwException(SQLITE_RANGE, "column index out of range")
}

@PublishedApi
internal inline fun throwException(code: Int, message: String? = null): Nothing {
    throw NativeException(code and 0xFF, message ?: errorString(code))
}

@PublishedApi
internal inline fun errorString(errCode: Int): String? {
    val pText = errStr(errCode).takeIf { it != nullPtr } ?: return null
    val size = strLen(pText)

    return withBytes(size) { blobCopy(it, pText, size) }.decodeToString()
}

@PublishedApi
internal inline fun outOfMemory(pStmt: Statement): Boolean =
    outOfMemory(pStmt = pStmt.ptr) != SQLITE_OK

@PublishedApi
internal inline fun noRow(pStmt: Statement): Boolean = noRow(pStmt = pStmt.ptr) != SQLITE_OK

@PublishedApi
internal inline fun invalidColumn(pStmt: Statement, iCol: Int): Boolean =
    invalidColumn(pStmt = pStmt.ptr, iCol = iCol) != SQLITE_OK

@PublishedApi
internal inline fun ColumnType.Companion.of(rc: Int) =
    when (rc) {
        SQLITE_INTEGER -> ColumnType.Integer
        SQLITE_FLOAT -> ColumnType.Float
        SQLITE_TEXT -> ColumnType.Text
        SQLITE_BLOB -> ColumnType.Blob
        SQLITE_NULL -> ColumnType.Null
        else -> throwException(rc)
    }

@PublishedApi
internal inline fun Step.Companion.of(rc: Int) =
    when (rc) {
        SQLITE_ROW -> Step.Row
        SQLITE_DONE -> Step.Done
        else -> throwException(rc)
    }
