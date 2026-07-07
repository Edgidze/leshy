package compose.project.leshy.i18n

import androidx.compose.runtime.Composable
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.category_boletus
import leshy.shared.generated.resources.category_chanterelle
import leshy.shared.generated.resources.category_misc
import leshy.shared.generated.resources.category_porcini
import leshy.shared.generated.resources.category_ryzhik
import org.jetbrains.compose.resources.stringResource

/** Resolves a [compose.project.leshy.domain.model.Category.nameKey] to a localized display name. */
@Composable
fun categoryDisplayName(nameKey: String): String = when (nameKey) {
    "category_porcini" -> stringResource(Res.string.category_porcini)
    "category_chanterelle" -> stringResource(Res.string.category_chanterelle)
    "category_ryzhik" -> stringResource(Res.string.category_ryzhik)
    "category_boletus" -> stringResource(Res.string.category_boletus)
    "category_misc" -> stringResource(Res.string.category_misc)
    else -> nameKey
}
