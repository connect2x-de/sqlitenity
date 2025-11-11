package de.connect2x.sqlitenity.api

class SQLitenityException(message: String?, cause: Throwable?) : RuntimeException(message, cause) {
    constructor() : this(null, null)

    constructor(message: String?) : this(message, null)

    constructor(cause: Throwable?) : this(null, cause)
}
