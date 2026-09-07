package leshy.mushrooms.map.domain.model

/**
 * [nameKey] is an identifier, never a display name — resolve it through
 * `i18n.collectionDisplayName`. For [CollectionSource.COUNTRY] it encodes the ISO code and the name
 * comes from `CountryNames`; for the user's own collections it's a generated token whose only job
 * is to be the merge key on import (same contract as `Category.nameKey`), and the text the user
 * typed lives in [name].
 */
data class Collection(
    val id: Long,
    val nameKey: String,
    val order: Int,
    val source: CollectionSource = CollectionSource.COUNTRY,
    /** User-typed name, `null` for country presets and for the service "Other" collection — both
     * of those resolve their display name from the interface language instead. */
    val name: String? = null,
)
