package de.connect2x.sqlitenity.encrypted.driver

import androidx.sqlite.SQLiteConnection

actual fun EncryptedSQLiteDriver(encryptionKey: EncryptionKey): EncryptedSQLiteDriver {
    return EncryptedSQLiteDriverImpl(encryptionKey)
}

private class EncryptedSQLiteDriverImpl(private val encryptionKey: EncryptionKey) :
    EncryptedSQLiteDriver {

    private val delegate = WebWorkerSQLiteDriver.instance

    override suspend fun open(fileName: String): SQLiteConnection {
        val connection = delegate.open(fileName)

        runCatching {
                configureConnection(
                    encryptionKey = encryptionKey,
                    prepare = { sql -> connection.prepare(sql) },
                    step = { statement -> statement.step() },
                )
            }
            .onFailure { connection.close() }
            .getOrThrow()

        return connection
    }
}
