package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.searchOrderedCategories
import leshy.mushrooms.map.ui.util.parseHexColor

private val GRID_TILE_MIN_SIZE = 100.dp
private val GRID_MAX_HEIGHT = 360.dp

/**
 * Lets the user pick another mushroom's bundled catalog illustration to reuse as the starting
 * photo for the species being created/edited in [SpeciesFormDialog] — the third photo source
 * ("Картинки") next to Camera/Gallery. Only catalog species with a bundled illustration
 * ([Category.iconRef]) are shown; the picked category's own image still goes through
 * [IconEditorDialog] afterwards, exactly like a camera/gallery photo.
 */
@Composable
fun CatalogPhotoPickerDialog(
    categories: List<Category>,
    onSelect: (Category) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val language = LocalAppLanguage.current
    val orderedCategories = remember(categories, query, language) {
        searchOrderedCategories(categories, query, language)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(StringKey.CatalogPhotoPickerTitle),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = GRID_TILE_MIN_SIZE),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = GRID_MAX_HEIGHT),
                ) {
                    items(orderedCategories, key = { it.id }) { category ->
                        CatalogPhotoTile(category = category, onClick = { onSelect(category) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogPhotoTile(category: Category, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, parseHexColor(category.colorHex), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        MushroomPhoto(category = category, modifier = Modifier.fillMaxSize())
    }
}
