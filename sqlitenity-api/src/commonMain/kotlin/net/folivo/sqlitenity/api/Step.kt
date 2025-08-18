package net.folivo.sqlitenity.api

sealed interface Step {
    data object Row : Step

    data object Done : Step

    companion object
}
