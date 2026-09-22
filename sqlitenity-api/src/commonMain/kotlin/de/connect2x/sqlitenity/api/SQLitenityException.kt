package de.connect2x.sqlitenity.api

class SQLitenityException(val errorCode: Int, val errorMessage: String?, cause: Throwable?) :
    RuntimeException(buildMessage(errorCode, errorMessage), cause) {
    companion object {
        fun misuse(errorMessage: String?): SQLitenityException {
            return SQLitenityException(21, errorMessage, null)
        }
    }
}

private fun buildMessage(errorCode: Int, errorMessage: String?): String {
    return if (errorMessage == null) {
        "Error code: $errorCode"
    } else {
        "Error code: $errorCode, message: $errorMessage"
    }
}
