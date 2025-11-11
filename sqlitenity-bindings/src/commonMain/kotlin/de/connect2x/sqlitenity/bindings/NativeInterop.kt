@file:OptIn(ExperimentalContracts::class)

package de.connect2x.sqlitenity.bindings

import de.connect2x.sqlitenity.interop.ByteArrayPointer
import de.connect2x.sqlitenity.interop.CharArrayPointer
import de.connect2x.sqlitenity.interop.InteropScope
import de.connect2x.sqlitenity.interop.NativePointer
import de.connect2x.sqlitenity.interop.NativePointerPointer
import de.connect2x.sqlitenity.interop.nullPtr
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@PublishedApi
internal inline fun <T> interopScope(crossinline block: InteropScope.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    return InteropScope().use(block)
}

@PublishedApi
internal inline fun withBytes(
    size: Int,
    crossinline block: InteropScope.(ByteArrayPointer) -> Unit,
): ByteArray {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    val output = ByteArray(size)

    return interopScope {
        val ptr = toResult(output)
        block(ptr)
        apply(ptr, output)
    }
}

@PublishedApi
internal inline fun withText(
    size: Int,
    crossinline block: InteropScope.(CharArrayPointer) -> Unit,
): CharArray {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    val output = CharArray(size)

    return interopScope {
        val ptr = toResult(output)
        block(ptr)
        apply(ptr, output)
    }
}

@PublishedApi
internal inline fun withPointer(
    crossinline block: InteropScope.(NativePointerPointer) -> Unit
): NativePointer {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    val output = nullPtr

    return interopScope {
        val ptr = toResult(output)
        block(ptr)
        apply(ptr, output)
    }
}
