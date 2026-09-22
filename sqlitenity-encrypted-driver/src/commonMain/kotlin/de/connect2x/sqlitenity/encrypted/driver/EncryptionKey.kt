package de.connect2x.sqlitenity.encrypted.driver

import de.connect2x.sqlitenity.encrypted.driver.EncryptionKey.EncryptedHeader
import de.connect2x.sqlitenity.encrypted.driver.EncryptionKey.None
import de.connect2x.sqlitenity.encrypted.driver.EncryptionKey.PlaintextHeader

sealed interface EncryptionKey {
    data object None : EncryptionKey

    data class PlaintextHeader(val key: String) : EncryptionKey {
        init {
            requirePlaintextHeaderKey(key)
        }

        companion object {
            const val SIZE = 48
        }
    }

    data class EncryptedHeader(val key: String) : EncryptionKey {
        init {
            requireEncryptedHeaderKey(key)
        }

        companion object {
            const val SIZE = 32
        }
    }
}

fun EncryptionKey(bytes: ByteArray?): EncryptionKey {
    return when {
        bytes == null || bytes.isEmpty() -> None
        bytes.size == PlaintextHeader.SIZE -> PlaintextHeader(bytes.toHexString())
        bytes.size == EncryptedHeader.SIZE -> EncryptedHeader(bytes.toHexString())
        else ->
            throw IllegalArgumentException(
                "bytes must either be empty, size ${PlaintextHeader.SIZE} or size ${EncryptedHeader.SIZE}"
            )
    }
}

private fun requirePlaintextHeaderKey(value: String) {
    require(value.length == PlaintextHeader.SIZE * 2) {
        "EncryptionKey.PlaintextHeader requires the key to by ${PlaintextHeader.SIZE} bytes in size"
    }
    require(isLowercaseHexEncoded(value)) {
        "EncryptionKey.PlaintextHeader requires the combined key to be lowercase hex encoded"
    }
}

private fun requireEncryptedHeaderKey(value: String) {
    require(value.length == EncryptedHeader.SIZE * 2) {
        "EncryptionKey.EncryptedHeader requires the key to be ${EncryptedHeader.SIZE} bytes in size"
    }
    require(isLowercaseHexEncoded(value)) {
        "EncryptionKey.EncryptedHeader requires the combined key to be lowercase hex encoded"
    }
}

private fun isLowercaseHexEncoded(value: String): Boolean {
    return value.isNotEmpty() &&
        value.length % 2 == 0 &&
        value.all { it.isDigit() || it in 'a'..'f' }
}
