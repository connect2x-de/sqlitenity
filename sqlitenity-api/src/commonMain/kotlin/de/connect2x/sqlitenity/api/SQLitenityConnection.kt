package de.connect2x.sqlitenity.api

interface SQLitenityConnection<S : SQLitenityStatement> : AutoCloseable {

    val autoCommitEnabled: Boolean

    fun prepare(sql: String): S

    override fun close()

    companion object
}
