package de.connect2x.sqlitenity.api

interface SQLitenityDriver<C : SQLitenityConnection<S>, S : SQLitenityStatement> {
    fun open(fileName: String): C

    companion object
}
