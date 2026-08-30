package leshy.mushrooms.map.domain.model

data class MapFilter(
    val startMillis: Long? = null,
    val endMillis: Long? = null,
    val monthFrom: Int? = null,
    val monthTo: Int? = null,
    /**
     * Whether tracks of past walks are drawn — on both the Map screen and the Record screen (the
     * two places that show more than a single walk). On by default: the routes are the cheapest
     * hint about where the user has already been, and hiding them is the deliberate choice.
     */
    val showPastRoutes: Boolean = true,
)
