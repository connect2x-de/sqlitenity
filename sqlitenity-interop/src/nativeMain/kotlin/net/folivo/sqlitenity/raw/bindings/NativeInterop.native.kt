@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "NOTHING_TO_INLINE")

package net.folivo.sqlitenity.raw.bindings

import kotlin.OutOfMemoryError
import kotlin.native.internal.NativePtr
import kotlinx.cinterop.COpaque
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.pin
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.value

@OptIn(ExperimentalForeignApi::class)
actual typealias NativePointer = NativePtr

@OptIn(ExperimentalForeignApi::class)
actual typealias ByteArrayPointer = NativePtr

@OptIn(ExperimentalForeignApi::class)
actual typealias CharArrayPointer = NativePtr

@OptIn(ExperimentalForeignApi::class)
actual typealias NativePointerPointer = NativePtr

@OptIn(ExperimentalForeignApi::class)
actual inline val nullPtr: NativePointer
    get() = NativePtr.NULL

@OptIn(ExperimentalForeignApi::class)
actual class InteropScope actual constructor() : AutoCloseable {

    @PublishedApi internal var elements: MutableList<Pinned<*>>? = mutableListOf()

    actual inline fun toInterop(value: CharArray): CharArrayPointer = value.ptr()

    actual inline fun toInterop(value: ByteArray): ByteArrayPointer = value.ptr()

    actual inline fun toResult(value: ByteArray): ByteArrayPointer = value.ptr()

    actual inline fun toResult(value: CharArray): CharArrayPointer = value.ptr()

    actual inline fun toResult(value: NativePointer): NativePointerPointer = value.ptr()

    actual inline fun apply(ptr: ByteArrayPointer, value: ByteArray): ByteArray = value

    actual inline fun apply(ptr: CharArrayPointer, value: CharArray): CharArray = value

    actual inline fun apply(ptr: NativePointerPointer, value: NativePointer): NativePointer {
        val longArray = checkNotNull(interpretCPointer<LongVar>(ptr)) { "invalid ptr" }
        val nativePointerValue =
            longArray.pointed.value.takeIf { it != 0L }
                ?: return _root_ide_package_.net.folivo.sqlitenity.raw.bindings.nullPtr
        val nativePointer =
            checkNotNull(nativePointerValue.toCPointer<COpaque>()) {
                "invalid native pointer value"
            }

        return nativePointer.rawValue
    }

    actual override fun close() {
        val elements = this.elements ?: return
        this.elements = null
        elements.forEach { it.unpin() }
    }

    @PublishedApi
    internal inline fun <T : Any> T.save(): Pinned<T> {
        val elements = checkNotNull(elements) { "already closed" }

        val pinned = this.pin()
        elements.add(pinned)
        return pinned
    }

    @PublishedApi internal inline fun ByteArray.ptr() = save().addressOf(0).rawValue

    @PublishedApi internal inline fun CharArray.ptr() = save().addressOf(0).rawValue

    @PublishedApi
    internal inline fun NativePointer.ptr() =
        longArrayOf(this.toLong()).save().addressOf(0).rawValue
}

actual typealias OutOfMemoryError = OutOfMemoryError
