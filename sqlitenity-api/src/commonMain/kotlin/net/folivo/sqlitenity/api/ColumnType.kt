package net.folivo.sqlitenity.api

sealed interface ColumnType {
    data object Integer : ColumnType

    data object Float : ColumnType

    data object Text : ColumnType

    data object Blob : ColumnType

    data object Null : ColumnType

    companion object
}
