package net.folivo.sqlitenity.bundled

import java.nio.file.Files
import java.nio.file.StandardCopyOption

private sealed class Os(val name: String) {
    data object Win32 : Os("win32")

    data object Linux : Os("linux")

    data object Darwin : Os("darwin")

    companion object {
        fun of(name: String): Os =
            when {
                name.contains("windows", true) -> Win32
                name.contains("linux", true) -> Linux
                name.contains("osx", true) || name.contains("mac", true) -> Darwin
                else -> throw IllegalArgumentException("Unsupported OS: $name")
            }

        inline val system: Os
            get() = of(System.getProperty("os.name") ?: "")
    }
}

private sealed class Architecture(val name: String) {
    data object X86 : Architecture("x86")

    data object Arm64 : Architecture("aarch64")

    data object X64 : Architecture("x86-64")

    companion object {
        fun of(name: String): Architecture =
            when (name) {
                "i386",
                "i486",
                "i586",
                "i686",
                "x86",
                "x32" -> X86
                "x64",
                "amd64",
                "x86_64",
                "x86-64" -> X64
                "aarch64",
                "arm64" -> Arm64
                else -> error("Unsupported Architecture: $name")
            }

        inline val system: Architecture
            get() = of(System.getProperty("os.arch") ?: "")
    }
}

internal object JvmLoader {
    @Suppress("UnsafeDynamicallyLoadedCode")
    fun load(name: String) {
        val os = Os.system
        val arch = Architecture.system

        val prefix =
            when (os) {
                Os.Win32 -> ""
                else -> "lib"
            }

        val suffix =
            when (os) {
                Os.Win32 -> "dll"
                Os.Linux -> "so"
                Os.Darwin -> "dylib"
            }

        val targetPath = Files.createTempFile(name, "")
        val resourcePath = "/natives/${os.name}-${arch.name}/$prefix$name.$suffix"

        val resource = this::class.java.getResourceAsStream(resourcePath)

        checkNotNull(resource) { "Could not find $resourcePath in resources" }

        resource.use { Files.copy(it, targetPath, StandardCopyOption.REPLACE_EXISTING) }

        System.load(targetPath.toString())

        Files.deleteIfExists(targetPath)
    }
}
