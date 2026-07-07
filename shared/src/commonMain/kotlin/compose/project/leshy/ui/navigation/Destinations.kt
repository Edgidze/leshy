package compose.project.leshy.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object Record : Destination

    @Serializable
    data object RecordMap : Destination

    @Serializable
    data object Archive : Destination

    @Serializable
    data object Map : Destination

    @Serializable
    data object Settings : Destination
}
