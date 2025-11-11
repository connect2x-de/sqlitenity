package de.connect2x.sqlitenity.bundled

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.reflect.KClass

internal actual fun loadLibrary(name: String, property: String, clazz: String) {
    System.setProperty(property, clazz)
    JvmLoader.load(name)
}

internal actual fun <T : Any> KClass<T>.copy(
    source: String,
    target: Path,
    option: StandardCopyOption,
) {
    checkNotNull(java.getResourceAsStream(source)) { "Could not find $source in resources" }
        .use { Files.copy(it, target, option) }
}
