package net.folivo.sqlitenity.bundled

internal actual fun loadLibrary(name: String, property: String, clazz: String) {
    System.setProperty(property, clazz)

    when (System.getProperty("java.runtime.name")) {
        "Android Runtime" -> System.loadLibrary(name)
        else -> JvmLoader.load(name)
    }
}
