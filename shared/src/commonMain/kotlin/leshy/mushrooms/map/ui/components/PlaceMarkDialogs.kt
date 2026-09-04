package leshy.mushrooms.map.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.GeoPoint

/**
 * Три диалога отмеченного места — карточка, правка и подтверждение удаления — одним блоком.
 *
 * Стоят они на каждом экране, где по метке места вообще можно нажать (детализация прогулки, её
 * карта, полноэкранная карта находок), и переходы между ними всюду одни и те же: из карточки в
 * правку и обратно в карточку, из карточки в подтверждение и — если подтвердили — сразу мимо
 * карточки наружу. Держать это состояние знает только сам блок; экрану остаётся хранить, какая
 * метка выбрана, и получить [onDismissRequest], когда выбор пора снять.
 */
@Composable
fun PlaceMarkDialogs(
    place: FieldMark?,
    onUpdate: (mark: FieldMark, name: String, description: String, photoPath: String?) -> Unit,
    onDelete: (FieldMark) -> Unit,
    onDismissRequest: () -> Unit,
) {
    if (place == null) return

    // Ключом идёт сама метка, а не Unit: экран меняет выбранную метку, не размонтируя этот блок,
    // и без сброса новая метка открылась бы сразу в правке или в подтверждении удаления,
    // унаследованных от предыдущей.
    var isEditing by remember(place.id) { mutableStateOf(false) }
    var confirmDelete by remember(place.id) { mutableStateOf(false) }

    if (isEditing) {
        AddPlaceDialog(
            location = GeoPoint(place.lat, place.lon, null, place.timestamp),
            initialName = place.name,
            initialDescription = place.description.orEmpty(),
            initialPhotoPath = place.photoPath,
            onSave = { name, description, photoPath ->
                onUpdate(place, name, description, photoPath)
                isEditing = false
            },
            onDismissRequest = { isEditing = false },
        )
    } else {
        PlaceViewDialog(
            mark = place,
            onEditClick = { isEditing = true },
            onDeleteClick = { confirmDelete = true },
            onDismissRequest = onDismissRequest,
        )
    }

    if (confirmDelete) {
        DeletePlaceConfirmDialog(
            onConfirm = {
                onDelete(place)
                confirmDelete = false
                onDismissRequest()
            },
            onDismissRequest = { confirmDelete = false },
        )
    }
}
