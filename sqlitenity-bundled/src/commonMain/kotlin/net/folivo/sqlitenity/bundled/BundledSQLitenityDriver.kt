package net.folivo.sqlitenity.bundled

import net.folivo.sqlitenity.api.SQLitenityDriver

class BundledSQLitenityDriver :
    SQLitenityDriver<BundledSQLitenityConnection, BundledSQLitenityStatement> {
    override fun open(fileName: String): BundledSQLitenityConnection =
        BundledSQLitenityConnection(rethrow { Handle.open(fileName) })
}
