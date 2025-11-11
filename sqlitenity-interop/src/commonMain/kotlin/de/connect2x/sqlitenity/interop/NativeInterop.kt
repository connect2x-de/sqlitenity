@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package de.connect2x.sqlitenity.interop

expect class NativePointer

expect class ByteArrayPointer

expect class CharArrayPointer

expect class NativePointerPointer

expect inline val nullPtr: NativePointer

expect class InteropScope() : AutoCloseable {

    inline fun toInterop(value: CharArray): CharArrayPointer

    inline fun toInterop(value: ByteArray): ByteArrayPointer

    inline fun toResult(value: ByteArray): ByteArrayPointer

    inline fun toResult(value: CharArray): CharArrayPointer

    inline fun toResult(value: NativePointer): NativePointerPointer

    inline fun apply(ptr: ByteArrayPointer, value: ByteArray): ByteArray

    inline fun apply(ptr: CharArrayPointer, value: CharArray): CharArray

    inline fun apply(ptr: NativePointerPointer, value: NativePointer): NativePointer

    override fun close()
}

expect open class OutOfMemoryError() : Error
