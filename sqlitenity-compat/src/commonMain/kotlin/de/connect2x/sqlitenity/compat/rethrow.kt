package de.connect2x.sqlitenity.compat

import androidx.sqlite.throwSQLiteException
import de.connect2x.sqlitenity.api.SQLitenityException

internal inline fun <T> rethrow(block: () -> T): T =
    try {
        block()
    } catch (e: SQLitenityException) {
        throwSQLiteException(e.errorCode, e.errorMessage)
    }
