package de.connect2x.sqlitenity.bundled

import de.connect2x.sqlitenity.api.SQLitenityDriver

class SQLitenityBundledDriver :
    SQLitenityDriver<SQLitenityBundledConnection, SQLitenityBundledStatement> {
    override fun open(fileName: String): SQLitenityBundledConnection =
        SQLitenityBundledConnection(rethrow { Handle.open(fileName) })
}
