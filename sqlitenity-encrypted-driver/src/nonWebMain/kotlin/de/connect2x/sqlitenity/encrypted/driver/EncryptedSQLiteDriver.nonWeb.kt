package de.connect2x.sqlitenity.encrypted.driver

import androidx.sqlite.SQLiteConnection
import de.connect2x.sqlitenity.bundled.SQLitenityBundledDriver
import de.connect2x.sqlitenity.compat.SQLitenityCompatDriver

actual fun EncryptedSQLiteDriver(encryptionKey: EncryptionKey): EncryptedSQLiteDriver {
    return EncryptedSQLiteDriverImpl(encryptionKey)
}

private class EncryptedSQLiteDriverImpl(private val encryptionKey: EncryptionKey) :
    EncryptedSQLiteDriver {

    private val delegate = SQLitenityCompatDriver(SQLitenityBundledDriver())

    override fun open(fileName: String): SQLiteConnection {
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
