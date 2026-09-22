package de.connect2x.sqlitenity.encrypted.driver

import androidx.sqlite.SQLiteDriver

interface EncryptedSQLiteDriver : SQLiteDriver

expect fun EncryptedSQLiteDriver(encryptionKey: EncryptionKey): EncryptedSQLiteDriver
