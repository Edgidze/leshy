package compose.project.leshy.i18n

import androidx.compose.runtime.Composable
import compose.project.leshy.domain.model.AppLanguage

/** Resolves a [compose.project.leshy.domain.model.Collection.nameKey] to a localized display name. */
@Composable
fun collectionDisplayName(nameKey: String): String =
    collectionNameStringKey(nameKey)?.let { stringResource(it) } ?: nameKey

/** Non-composable counterpart of [collectionDisplayName]. */
fun collectionDisplayName(nameKey: String, language: AppLanguage): String =
    collectionNameStringKey(nameKey)?.let { string(it, language) } ?: nameKey

private fun collectionNameStringKey(nameKey: String): StringKey? = when (nameKey) {
    "collection_demo_north" -> StringKey.CollectionDemoNorth
    "collection_demo_south" -> StringKey.CollectionDemoSouth
    "collection_demo_east" -> StringKey.CollectionDemoEast
    else -> null
}
