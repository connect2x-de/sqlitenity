@file:OptIn(ExperimentalWasmJsInterop::class)

package de.connect2x.sqlitenity.web.worker

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsModule
import kotlin.js.JsName
import org.w3c.dom.MessageChannel
import org.w3c.dom.MessageEvent
import org.w3c.dom.Worker

object SQLitenityWebWorker {
    val instance: Worker = createWorkerImpl()

    suspend fun delete(prefix: String) {
        return suspendCoroutine { continuation ->
            val channel = MessageChannel()

            channel.port1.onmessage = { event ->
                channel.port1.close()
                channel.port2.close()
                channel.port1.onmessage = null

                when (val error = errorOrNull(event)) {
                    null -> continuation.resume(Unit)
                    else ->
                        continuation.resumeWithException(
                            IllegalStateException("unable to delete ${prefix}: $error")
                        )
                }
            }

            instance.postMessage(
                message = deleteRequest(prefix),
                transfer = listOf(channel.port2).toJsArray(),
            )
        }
    }
}

@JsFun("""(prefix) => ({ data: { cmd: "delete", prefix } })""")
private external fun deleteRequest(prefix: String): JsAny

@JsFun("""(event) => "error" in event.data ? event.data.error : null""")
private external fun errorOrNull(event: MessageEvent): String?

@JsModule("@c2x/sqlite-worker/helper.mjs")
@JsName("createWorker")
private external fun createWorkerImpl(): Worker
