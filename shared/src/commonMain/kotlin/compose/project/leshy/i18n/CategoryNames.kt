package compose.project.leshy.i18n

import androidx.compose.runtime.Composable

/** Resolves a [compose.project.leshy.domain.model.Category.nameKey] to a localized display name. */
@Composable
fun categoryDisplayName(nameKey: String): String = when (nameKey) {
    "category_porcini" -> stringResource(StringKey.CategoryPorcini)
    "category_chanterelle" -> stringResource(StringKey.CategoryChanterelle)
    "category_ryzhik" -> stringResource(StringKey.CategoryRyzhik)
    "category_boletus" -> stringResource(StringKey.CategoryBoletus)
    "category_misc" -> stringResource(StringKey.CategoryMisc)
    else -> nameKey
}
