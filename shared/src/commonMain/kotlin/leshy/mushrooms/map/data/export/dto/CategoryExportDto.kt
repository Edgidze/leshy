package leshy.mushrooms.map.data.export.dto

import kotlinx.serialization.Serializable

const val CATEGORIES_ENTRY_NAME = "categories/categories.json"

fun categoryIconEntryName(nameKey: String) = "categories/$nameKey.png"

/** Mirrors the non-catalog fields of `CategoryEntity` — only species with `source != APP` are ever
 * written here (`.claude/plans/user-mushrooms.md`, Phase 6); catalog species are reseeded on every
 * install by `EnsureDefaultCategoriesUseCase` and never travel in the archive. [nameKey] is the
 * *only* field import is allowed to merge on — never [customNames]/[scientificName], which two
 * independently created species can easily share. [customNames] is keyed by `AppLanguage.code`,
 * the same shape `Converters` already stores in Room. [hasIcon] says whether
 * `categories/<nameKey>.png` is present in the archive — a species can legitimately have no icon. */
@Serializable
data class CategoryExportDto(
    val nameKey: String,
    val customNames: Map<String, String>,
    val scientificName: String?,
    val colorHex: String,
    val hasIcon: Boolean,
)
