package de.connect2x.sqlitenity.encrypted.driver

import androidx.sqlite.SQLiteException

const val SQLITE_BUSY: Int = 5
const val SQLITE_LOCKED: Int = 6
const val SQLITE_NOTADB: Int = 26
const val SQLITE_NOTADB_AUTH_FAILED: Int = SQLITE_NOTADB or (1 shl 8)

val SQLiteException.extendedErrorCode: Int?
    get() = message?.substringAfter("Error code: ", "")?.substringBefore(",")?.trim()?.toIntOrNull()

val SQLiteException.errorCode: Int?
    get() = extendedErrorCode?.let { it and 0xFF }
