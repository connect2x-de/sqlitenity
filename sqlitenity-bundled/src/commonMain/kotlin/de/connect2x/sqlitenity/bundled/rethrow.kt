package de.connect2x.sqlitenity.bundled

import de.connect2x.sqlitenity.api.SQLitenityException
import de.connect2x.sqlitenity.bindings.NativeException

internal inline fun <T> rethrow(crossinline block: () -> T): T =
    try {
        block()
    } catch (e: NativeException) {
        throw SQLitenityException(e)
    }
