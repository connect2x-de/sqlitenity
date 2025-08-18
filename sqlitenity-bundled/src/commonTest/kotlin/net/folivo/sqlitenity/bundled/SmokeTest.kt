package net.folivo.sqlitenity.bundled

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import net.folivo.sqlitenity.api.Step

class SmokeTest {

    val userTableQuery =
        """
            CREATE TABLE IF NOT EXISTS users (
                email TEXT PRIMARY KEY COLLATE NOCASE,
                name  TEXT,
                age   INTEGER,
                ranking FLOAT
            );
        """
            .trimIndent()

    val insertUsersQuery =
        """
            INSERT INTO users (email, name, age, ranking) VALUES (?, ?, ?, ?);
        """
            .trimIndent()

    val selectUsersQuery =
        """
            SELECT * FROM users;
        """
            .trimIndent()

    val beginTransaction = "BEGIN"
    val commitTransaction = "COMMIT"

    val users =
        listOf(
            User("max@mustermann.com", "Max Mustermann", 28, 4.7),
            User("alice@example.com", "Alice Example", 43, 2.5),
        )

    val driver = BundledSQLitenityDriver()

    @Test
    fun openInMemory() = deferScope {
        val connection = driver.open(":memory:")
        defer { connection.close() }

        val createUsersTable = connection.prepare(userTableQuery)
        defer { createUsersTable.close() }

        assertEquals(Step.Done, createUsersTable.step())
        createUsersTable.close()

        val createUsers = connection.prepare(insertUsersQuery)
        defer { createUsers.close() }

        for (user in users) {
            createUsers.bindText(1, user.email)
            createUsers.bindText(2, user.name)
            createUsers.bindLong(3, user.age)
            createUsers.bindDouble(4, user.ranking)
            assertEquals(Step.Done, createUsers.step())
            createUsers.reset()
        }
        createUsers.close()

        val beginTransaction = connection.prepare(beginTransaction)
        defer { beginTransaction.close() }
        val selectUsers = connection.prepare(selectUsersQuery)
        defer { selectUsers.close() }
        val commitTransaction = connection.prepare(commitTransaction)
        defer { commitTransaction.close() }

        assertTrue(connection.autoCommitEnabled)
        assertEquals(Step.Done, beginTransaction.step())
        assertFalse(connection.autoCommitEnabled)

        for (user in users) {
            assertEquals(Step.Row, selectUsers.step())
            assertEquals(user.email, selectUsers.getText(0))
            assertEquals(user.name, selectUsers.getText(1))
            assertEquals(user.age, selectUsers.getLong(2))
            assertEquals(user.ranking, selectUsers.getDouble(3))
        }
        assertEquals(Step.Done, selectUsers.step())
        assertEquals(Step.Done, commitTransaction.step())

        beginTransaction.close()
        commitTransaction.close()
        selectUsers.close()
    }

    data class User(val email: String, val name: String, val age: Long, val ranking: Double)
}

class DeferScope : AutoCloseable {
    val defers = mutableListOf<() -> Unit>()

    fun defer(block: () -> Unit) = defers.add(block)

    override fun close() {
        defers.reversed().forEach { it() }
    }
}

@OptIn(ExperimentalContracts::class)
fun deferScope(block: DeferScope.() -> Unit) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    DeferScope().use { it.block() }
}
