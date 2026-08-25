package leshy.mushrooms.map.data.catalog

/**
 * The pre-v11 `nameKey`s of the bundled catalog, mapped onto their key in the 408-entry catalog
 * (`catalog.json`). Until the v10→v11 migration the 30 bundled species were keyed
 * `category_<latin>`; the new catalog uses the bare key (`boletus_edulis`), so all but seven are
 * just the old key minus its `category_` prefix.
 *
 * Those seven are the ones the old catalog kept as a broad "group" entry and the new one doesn't —
 * every target below was chosen by the project owner (`.claude/plans/countries-and-languages.md`
 * §3.3, keyed by GC code) and is part of the `RU` preset, so remapping never strands a Russian
 * user's existing finds outside their own collection.
 */
val LEGACY_CATEGORY_KEY_REMAP: Map<String, String> = mapOf(
    "category_russula_species" to "russula_cyanoxantha",
    "category_agaricus_species" to "agaricus_campestris",
    "category_morchella_species" to "morchella_esculenta",
    "category_gyromitra_species" to "gyromitra_esculenta__2",
    "category_lycoperdon_calvatia_species" to "lycoperdon_perlatum",
    "category_xerocomus_subtomentosus_group" to "xerocomus_subtomentosus",
    // Deliberately GC0056 (`suillus_luteus__2`, the broad "slippery jack" concept already in the RU
    // preset) rather than the literal `suillus_luteus` (GC0023, not in RU) — see §3.3.
    "category_suillus_luteus" to "suillus_luteus__2",
)

private const val LEGACY_KEY_PREFIX = "category_"

/** Service keys — not catalog entries, they keep their `category_` prefix and their `StringKey`
 * (see `i18n/CategoryNames.kt`), so neither the migration nor import may rewrite them. */
private val SERVICE_NAME_KEYS = setOf("category_misc", "category_unknown_mushroom")

/**
 * [nameKey] translated to its catalog key, or returned unchanged when it isn't a legacy catalog key
 * (a service key, a `user_…` species, or an already-migrated catalog key).
 *
 * Used in two places that must agree: the v10→v11 migration, and archive import — an archive
 * exported before v11 records each find's species as a legacy key, and without this its finds would
 * all land on `category_misc`.
 */
fun catalogKeyForLegacy(nameKey: String): String = when {
    nameKey in SERVICE_NAME_KEYS -> nameKey
    nameKey in LEGACY_CATEGORY_KEY_REMAP -> LEGACY_CATEGORY_KEY_REMAP.getValue(nameKey)
    nameKey.startsWith(LEGACY_KEY_PREFIX) -> nameKey.removePrefix(LEGACY_KEY_PREFIX)
    else -> nameKey
}
