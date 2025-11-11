package de.connect2x.sqlitenity.bundled

import de.connect2x.sqlitenity.api.SQLitenityDriver

class BundledSQLitenityDriver :
    SQLitenityDriver<BundledSQLitenityConnection, BundledSQLitenityStatement> {
    override fun open(fileName: String): BundledSQLitenityConnection =
        BundledSQLitenityConnection(rethrow { Handle.open(fileName) })
}
