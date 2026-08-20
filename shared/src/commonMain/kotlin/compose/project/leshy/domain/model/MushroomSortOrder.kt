package compose.project.leshy.domain.model

enum class MushroomSortOrder {
    ALPHABETICAL,
    /** Non-poisonous species first, poisonous ones last — alphabetical within each of those two groups. */
    POISONOUS_LAST,
}
