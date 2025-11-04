package net.folivo.sqlitenity.bundled

import net.folivo.sqlitenity.api.SQLitenityException
import net.folivo.sqlitenity.bindings.NativeException

internal inline fun <T> rethrow(crossinline block: () -> T): T =
    try {
        block()
    } catch (e: NativeException) {
        throw SQLitenityException(e)
    }
