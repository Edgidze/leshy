package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.ic_mushrooms
import org.jetbrains.compose.resources.painterResource

/**
 * Сторона поля под грибом. Крупно намеренно: пустой экран — это единственное место, где картинке
 * не с чем конкурировать, и ровно тот экран, который новичок видит раньше любого заполненного.
 * Гриб шире, чем выше (поле заполняется целиком, см. `tools/prepare_icon_assets.py`), так что
 * видимая высота выходит примерно на десятую меньше.
 */
private val GLYPH_SIZE = 112.dp

/**
 * Насколько гриб бледнее обычного значка. Это не значок и не кнопка — на него не нажимают, и в
 * полную силу цвета он перетягивал бы внимание с текста и кнопки под собой.
 */
private const val GLYPH_ALPHA = 0.45f

/** Предел ширины текстовой колонки — на планшете подпись иначе растягивается в одну длинную строку. */
private val TEXT_MAX_WIDTH = 320.dp

/**
 * Пустое состояние раздела: гриб, заголовок, одна поясняющая фраза и, если есть куда вести, кнопка.
 *
 * До этого пустой Архив и пустая Статистика показывали одну и ту же серую строку по центру экрана —
 * ни картинки, ни объяснения, ни выхода (§4.4 дизайн-аудита). Причина, по которой это чинится
 * первым после тёмной темы: именно эти экраны новый пользователь открывает РАНЬШЕ любых
 * заполненных, и именно они формируют впечатление «пусто и недоделано».
 *
 * Гриб взят тот же (`ic_mushrooms.webp`), что стоит на карточке архива, в шапке «Записи» и в
 * разделе «Виды» меню — одно понятие, один символ (§2.3). Своей картинки под пустой экран не
 * рисуется: набор служебной графики в приложении сознательно один.
 *
 * [action] — обычно кнопка «Начать прогулку». Отдельным слотом, а не парой «текст + колбэк»:
 * пустое состояние без выхода (когда вести некуда) — законный случай, и тогда кнопки просто нет.
 */
@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_mushrooms),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = GLYPH_ALPHA),
            modifier = Modifier.size(GLYPH_SIZE),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp).widthIn(max = TEXT_MAX_WIDTH),
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp).widthIn(max = TEXT_MAX_WIDTH),
        )
        if (action != null) {
            Column(modifier = Modifier.padding(top = 24.dp)) { action() }
        }
    }
}

/**
 * Пустое состояние с единственной кнопкой приложения, которая тут вообще возможна, — «Начать
 * прогулку». Отдельный композабл, потому что и Архив, и Статистика упираются в одно и то же: пока
 * не записана первая прогулка, показывать им нечего, и ведут они в одно и то же место.
 */
@Composable
fun NoWalksYetState(descriptionKey: StringKey, onStartWalkClick: () -> Unit, modifier: Modifier = Modifier) {
    EmptyState(
        title = stringResource(StringKey.ArchiveEmpty),
        description = stringResource(descriptionKey),
        modifier = modifier,
        action = {
            LeshyButton(onClick = onStartWalkClick) {
                Text(stringResource(StringKey.EmptyStartWalkButton))
            }
        },
    )
}

/**
 * То, что стоит на месте содержимого, пока неизвестно, будет ли оно.
 *
 * Существует ради одной ошибки: пустое состояние — это утверждение «здесь ничего нет», и делать
 * его, пока база ещё не ответила, нельзя. Между первой композицией экрана и первой выдачей Room
 * проходит несколько кадров, и у пользователя с полным архивом крупный гриб с надписью «прогулок
 * пока нет» успевал мелькнуть перед его же списком. Это хуже, чем просто некрасиво: экран
 * заявляет неправду, а потом сам себя опровергает.
 *
 * Поэтому у экранов с пустым состоянием три состояния, а не два: загрузка → (содержимое | пусто).
 * Флаг — `isLoading` в соответствующем `UiState`, снимается первой же настоящей выдачей.
 */
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
