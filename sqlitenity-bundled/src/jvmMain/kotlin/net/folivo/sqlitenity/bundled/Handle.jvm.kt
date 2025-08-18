package net.folivo.sqlitenity.bundled

internal actual fun loadLibrary(name: String, property: String, clazz: String) {
    System.setProperty(property, clazz)
    JvmLoader.load(name)
}
