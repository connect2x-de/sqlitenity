package de.connect2x.sqlitenity.encrypted.driver

import androidx.sqlite.SQLiteException
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.throwSQLiteException

internal inline fun configureConnection(
    encryptionKey: EncryptionKey,
    prepare: (String) -> SQLiteStatement,
    step: (SQLiteStatement) -> Boolean,
) {
    when (encryptionKey) {
        is EncryptionKey.None -> {}
        is EncryptionKey.PlaintextHeader ->
            configurePlaintextHeader(encryptionKey = encryptionKey, prepare = prepare, step = step)
        is EncryptionKey.EncryptedHeader ->
            configureEncryptedHeader(encryptionKey = encryptionKey, prepare = prepare, step = step)
    }
}

private inline fun configurePlaintextHeader(
    encryptionKey: EncryptionKey.PlaintextHeader,
    prepare: (String) -> SQLiteStatement,
    step: (SQLiteStatement) -> Boolean,
) {
    prepare("PRAGMA plaintext_header_size = 24;").use {
        if (!step(it) || it.getColumnNames().getOrNull(0) != "24") {
            throwSQLiteException(1, "plaintext header is not supported")
        }
    }
    configureKey(key = encryptionKey.key, prepare = prepare, step = step)
}

private inline fun configureEncryptedHeader(
    encryptionKey: EncryptionKey.EncryptedHeader,
    prepare: (String) -> SQLiteStatement,
    step: (SQLiteStatement) -> Boolean,
) {
    configureKey(key = encryptionKey.key, prepare = prepare, step = step)
}

private inline fun configureKey(
    key: String,
    prepare: (String) -> SQLiteStatement,
    step: (SQLiteStatement) -> Boolean,
) {
    prepare("PRAGMA key = 'raw:$key';").use {
        if (!step(it) || it.getColumnNames().getOrNull(0) != "ok") {
            throwSQLiteException(1, "encryption is not supported")
        }
    }
    checkSchemaCanBeDecrypted(prepare = prepare, step = step)
}

private inline fun checkSchemaCanBeDecrypted(
    prepare: (String) -> SQLiteStatement,
    step: (SQLiteStatement) -> Boolean,
) {
    try {
        prepare("SELECT count(*) FROM sqlite_master;").use { step(it) }
    } catch (exception: SQLiteException) {
        if (exception.errorCode != SQLITE_NOTADB) throw exception

        throwSQLiteException(
            SQLITE_NOTADB_AUTH_FAILED,
            "unable to open database with the configured key",
        )
    }
}
