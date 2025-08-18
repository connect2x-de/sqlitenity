package net.folivo.sqlitenity.bundled

import net.folivo.sqlitenity.bindings.Connection

internal object Handle {
    init {
        loadLibrary(
            "sqlitenity",
            "sqlitenity.class",
            "net/folivo/sqlitenity/raw/bindings/BindingsKt",
        )
    }

    internal fun open(fileName: String): Connection = net.folivo.sqlitenity.bindings.open(fileName)
}

internal expect fun loadLibrary(name: String, property: String, clazz: String)
