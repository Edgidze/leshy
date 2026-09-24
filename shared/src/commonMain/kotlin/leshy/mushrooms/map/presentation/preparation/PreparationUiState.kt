package leshy.mushrooms.map.presentation.preparation

import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.OfflineRegionInfo

data class PendingRegionSelection(
    val west: Double,
    val south: Double,
    val east: Double,
    val north: Double,
    val minZoom: Int,
    val maxZoom: Int,
)

data class PreparationUiState(
    val regions: List<OfflineRegionInfo> = emptyList(),
    /**
     * Где пользователь. Нужно на этом экране дважды: показать точку «ты здесь» на карте выбора и
     * навести на неё камеру при первом фиксе, чтобы человек выбирал участок вокруг себя, а не искал
     * себя на карте мира. `null` — фикса ещё нет либо геолокация недоступна; тогда карта так и
     * остаётся обзором мира.
     */
    val currentLocation: GeoPoint? = null,
    val showNameDialog: Boolean = false,
    val nameInput: String = "",
    val pendingSelection: PendingRegionSelection? = null,
    val regionPendingDelete: String? = null,
)
