@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "NOTHING_TO_INLINE")

package net.folivo.sqlitenity.raw.bindings

import java.lang.OutOfMemoryError

actual typealias NativePointer = Long

actual typealias ByteArrayPointer = ByteArray

actual typealias CharArrayPointer = CharArray

actual typealias NativePointerPointer = LongArray

actual inline val nullPtr: NativePointer
    get() = 0L

actual class InteropScope actual constructor() : AutoCloseable {
    actual inline fun toInterop(value: CharArray): CharArrayPointer = value

    actual inline fun toInterop(value: ByteArray): ByteArrayPointer = value

    actual inline fun toResult(value: ByteArray): ByteArrayPointer = value

    actual inline fun toResult(value: CharArray): CharArrayPointer = value

    actual inline fun toResult(value: NativePointer): NativePointerPointer = LongArray(1) { value }

    actual inline fun apply(ptr: ByteArrayPointer, value: ByteArray): ByteArray = value

    actual inline fun apply(ptr: CharArrayPointer, value: CharArray): CharArray = value

    actual inline fun apply(ptr: NativePointerPointer, value: NativePointer): NativePointer = ptr[0]

    actual override fun close() {
        // NO-OP as no resources are created for interop
    }
}

actual typealias OutOfMemoryError = OutOfMemoryError
