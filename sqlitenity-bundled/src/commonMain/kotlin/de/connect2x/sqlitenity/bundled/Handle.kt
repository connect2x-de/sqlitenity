package de.connect2x.sqlitenity.bundled

import de.connect2x.sqlitenity.bindings.Connection

internal object Handle {
    init {
        loadLibrary("sqlitenity", "sqlitenity.class", "de/connect2x/sqlitenity/interop/BindingsKt")
    }

    internal fun open(fileName: String): Connection =
        de.connect2x.sqlitenity.bindings.open(fileName)
}

internal expect fun loadLibrary(name: String, property: String, clazz: String)
