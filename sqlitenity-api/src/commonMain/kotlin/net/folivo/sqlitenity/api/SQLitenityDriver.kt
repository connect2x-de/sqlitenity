package net.folivo.sqlitenity.api

interface SQLitenityDriver<C : SQLitenityConnection<S>, S : SQLitenityStatement> {
    fun open(fileName: String): C

    companion object
}
