package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.i18n.HelpKey
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.ic_mushrooms
import org.jetbrains.compose.resources.painterResource

/**
 * Картинка к блоку справки раздела ([leshy.mushrooms.map.ui.screens.HelpScreen]): тот самый
 * элемент интерфейса, о котором говорит текст блока, нарисованный рядом с ним.
 *
 * Как и макеты обзорной страницы (`MiniMockups.kt`, откуда взяты общие кирпичики), это не
 * скриншоты, а вёрстка теми же цветами схемы и теми же значками, что у настоящих экранов, — иначе
 * картинки пришлось бы перевыпускать под тему, под язык и после каждой правки экрана.
 *
 * **Отличие от обзорной страницы — подписи.** Там их нет вовсе. Здесь кнопка подписана ровно тем
 * же [StringKey], которым она подписана в настоящем интерфейсе (`RecordStart`, `DataDoneButton`,
 * `SettingsClearMapCacheButton`, …), потому что справка про кнопку «Старт» и должна показывать
 * кнопку со словом «Старт» на языке читателя. Своих строк у картинок нет ни одной: всё, что на них
 * написано, уже переведено как часть интерфейса, так что новых переводов эти иллюстрации не
 * создают. Отсюда же требование к вёрстке: подпись — одна строка с многоточием
 * ([MockButton]), а не жёстко заданная ширина, иначе длинный перевод порвёт картинку.
 *
 * Блок без картинки — нормально: у предупреждений («удаление необратимо», «повторный импорт
 * создаст дубликаты») показывать нечего, кроме кнопки, уже показанной выше. Такие ветки `when`
 * возвращают `Unit` явно.
 */
@Composable
fun HelpIllustration(key: HelpKey, modifier: Modifier = Modifier) {
    when (key) {
        HelpKey.RecordPurpose -> HelpFrame(modifier) { RecordScreenMock() }
        HelpKey.RecordStartFinish -> HelpFrame(modifier) { RecordButtonsMock() }
        HelpKey.RecordTiles -> HelpFrame(modifier) { MushroomTilesMock() }
        HelpKey.RecordPlace -> HelpFrame(modifier) { PlaceButtonMock() }
        HelpKey.RecordNavigation -> HelpFrame(modifier) { NavigationPanelMock() }
        HelpKey.RecordSearchAndOwn -> HelpFrame(modifier) { SearchAndOwnSpeciesMock() }
        HelpKey.RecordFilters -> HelpFrame(modifier) { FilterButtonMock() }
        HelpKey.RecordBackground -> HelpFrame(modifier) { PastWalksMapMock() }

        HelpKey.ArchivePurpose -> HelpFrame(modifier) { WalkCardsMock() }
        HelpKey.ArchiveDetail -> HelpFrame(modifier) { WalkDetailMock() }
        HelpKey.ArchiveShare -> HelpFrame(modifier) { ShareDialogMock() }
        HelpKey.ArchiveSelection -> HelpFrame(modifier) { SelectionModeMock() }
        HelpKey.ArchiveUnfinished -> HelpFrame(modifier) { UnfinishedWalkMock() }

        HelpKey.MapPurpose -> HelpFrame(modifier) { FindsMapMock() }
        HelpKey.MapFullScreen -> HelpFrame(modifier) { ClusterZoomMock() }
        HelpKey.MapSliders -> HelpFrame(modifier) { SlidersMock() }
        HelpKey.MapStats -> HelpFrame(modifier) { StatsMock() }
        HelpKey.MapFilters -> HelpFrame(modifier) { FilterDialogMock() }
        HelpKey.MapPlaces -> HelpFrame(modifier) { PlaceCardMock() }

        HelpKey.SpeciesPurpose -> HelpFrame(modifier) { SpeciesSectionsMock() }
        HelpKey.SpeciesCollections -> HelpFrame(modifier) { CollectionRowMock() }
        HelpKey.SpeciesOwn -> HelpFrame(modifier) { OwnSpeciesMock() }
        HelpKey.SpeciesCheckboxes -> HelpFrame(modifier) { SpeciesCheckboxesMock() }
        // Единственная картинка к этому блоку — красный баннер с ровно тем же текстом, что и сам
        // блок; показывать рядом два одинаковых предложения незачем.
        HelpKey.SpeciesImages -> Unit

        HelpKey.PreparationPurpose -> HelpFrame(modifier) { PreparationScreenMock() }
        HelpKey.PreparationDownload -> HelpFrame(modifier) { DownloadAreaMock() }
        HelpKey.PreparationRegions -> HelpFrame(modifier) { RegionChipMock() }
        HelpKey.PreparationAreaSize -> HelpFrame(modifier) { AreaSizeMock() }
        // Фоновое скачивание и пауза видны на плашке области, показанной двумя блоками выше.
        HelpKey.PreparationBackground -> Unit

        HelpKey.DataPurpose -> HelpFrame(modifier) { TransferMock() }
        HelpKey.DataExport -> HelpFrame(modifier) { ExportMock() }
        HelpKey.DataImport -> HelpFrame(modifier) { ImportMock() }
        // Содержимое архива и правила повторной загрузки — про то, чего на экране не видно.
        HelpKey.DataArchiveContents -> Unit
        HelpKey.DataDuplicates -> Unit

        HelpKey.SettingsPurpose -> HelpFrame(modifier) { SettingsScreenMock() }
        HelpKey.SettingsLanguage -> HelpFrame(modifier) { LanguageRowMock() }
        HelpKey.SettingsTheme -> HelpFrame(modifier) { ThemeOptionsMock() }
        HelpKey.SettingsMushroomSize -> HelpFrame(modifier) { MushroomSizeMock() }
        HelpKey.SettingsMushroomOrder -> HelpFrame(modifier) { MushroomOrderMock() }
        HelpKey.SettingsMapData -> HelpFrame(modifier) { MapDataButtonsMock() }
    }
}

// ─── Общая рамка и кирпичики, из которых собраны картинки ───────────────────────────────────────

private val FRAME_CORNER = 12.dp

/**
 * Площадка картинки: подложка, обводка, отступы.
 *
 * `clearAndSetSemantics` — не косметика: без него скринридер зачитывает каждую подпись, галочку и
 * ползунок макета как настоящий элемент управления, то есть предлагает нажать кнопку, которой нет.
 * Картинка иллюстрирует соседний текст и для озвучки не нужна: всё, что на ней есть, сказано в
 * самом блоке словами.
 */
@Composable
private fun HelpFrame(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(FRAME_CORNER))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(FRAME_CORNER),
            )
            .padding(12.dp)
            .clearAndSetSemantics { },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}

private enum class MockButtonTone { FILLED, TONAL, OUTLINED }

/**
 * Кнопка с настоящей подписью: заливка и обводка — как у [LeshyButton]/[MapFilterButton], текст —
 * готовая строка интерфейса. Одна строка с многоточием: длина перевода не наша забота, но и рвать
 * картинку она не должна.
 */
@Composable
private fun MockButton(
    label: String,
    modifier: Modifier = Modifier,
    tone: MockButtonTone = MockButtonTone.FILLED,
    leadingIcon: ImageVector? = null,
) {
    val container = when (tone) {
        MockButtonTone.FILLED -> MaterialTheme.colorScheme.primary
        MockButtonTone.TONAL -> MaterialTheme.colorScheme.secondaryContainer
        MockButtonTone.OUTLINED -> Color.Transparent
    }
    val content = when (tone) {
        MockButtonTone.FILLED -> MaterialTheme.colorScheme.onPrimary
        MockButtonTone.TONAL -> MaterialTheme.colorScheme.onSecondaryContainer
        MockButtonTone.OUTLINED -> MaterialTheme.colorScheme.primary
    }
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(container)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Круглая боковая кнопка «Записи» — та же обводка и та же заливка, что у `RecordSideButton`. */
@Composable
private fun MockRoundButton(icon: ImageVector, modifier: Modifier = Modifier, size: Dp = 40.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(size / 2),
        )
    }
}

/** Маленький значок-кнопка внутри карточки или плашки (карандаш, крестик, пауза). */
@Composable
private fun MockIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    size: Dp = 16.dp,
) {
    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = modifier.size(size))
}

/** Поле ввода или строка-кнопка: подложка, необязательный значок и полоска вместо текста. */
@Composable
private fun MockField(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    fillFraction: Float = 0.6f,
    label: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            MockIcon(leadingIcon, size = 14.dp)
            Spacer(modifier = Modifier.width(6.dp))
        }
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        } else {
            MiniTextLine(widthFraction = fillFraction, modifier = Modifier.weight(1f), thickness = 5.dp)
        }
        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(6.dp))
            MockIcon(trailingIcon, size = 14.dp)
        }
    }
}

/** Строка с настоящей галочкой и настоящей подписью — из «Настроек» и «Моих грибов». */
@Composable
private fun MockCheckRow(checked: Boolean, label: String? = null, fillFraction: Float = 0.7f) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = null)
        Spacer(modifier = Modifier.width(8.dp))
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        } else {
            MiniTextLine(widthFraction = fillFraction, modifier = Modifier.weight(1f), thickness = 5.dp)
        }
    }
}

/** Заголовок раздела внутри макета — настоящий заголовок настоящего экрана. */
@Composable
private fun MockSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.fillMaxWidth(),
    )
}

/** Плитка «добавить свой вид» — последняя в ленте «Записи»: пустая площадка с плюсом. */
@Composable
private fun MockAddTile(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )
    }
}

// ─── «Запись» ───────────────────────────────────────────────────────────────────────────────────

@Composable
private fun RecordScreenMock() {
    MiniMap(modifier = Modifier.fillMaxWidth().height(96.dp), withTrack = true)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        MiniMushroomTile(count = "3", modifier = Modifier.weight(1f), imageSize = 26.dp, controlSize = 12.dp)
        MiniMushroomTile(count = "7", modifier = Modifier.weight(1f), imageSize = 26.dp, controlSize = 12.dp)
        MiniMushroomTile(count = "1", modifier = Modifier.weight(1f), imageSize = 26.dp, controlSize = 12.dp)
    }
}

/** Нижний ряд «Записи» в двух состояниях: идёт запись и стоит на паузе. */
@Composable
private fun RecordButtonsMock() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MockRoundButton(Icons.Filled.AddLocationAlt)
        MockButton(stringResource(StringKey.RecordStart), modifier = Modifier.weight(1f))
        MockRoundButton(Icons.Filled.Search)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MockButton(stringResource(StringKey.RecordResume), modifier = Modifier.weight(1f))
        MockButton(stringResource(StringKey.RecordFinish), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MushroomTilesMock() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MiniMushroomTile(count = "0", modifier = Modifier.weight(1f), imageSize = 34.dp, controlSize = 16.dp)
        MiniMushroomTile(count = "12", modifier = Modifier.weight(1f), imageSize = 34.dp, controlSize = 16.dp)
        MiniMushroomTile(count = "5", modifier = Modifier.weight(1f), imageSize = 34.dp, controlSize = 16.dp)
    }
}

@Composable
private fun PlaceButtonMock() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MockRoundButton(Icons.Filled.AddLocationAlt, size = 44.dp)
        MockIcon(Icons.AutoMirrored.Filled.ArrowForward, size = 18.dp)
        PlaceCard(modifier = Modifier.weight(1f))
    }
}

/** Карточка отмеченного места: фотография, название, описание. */
@Composable
private fun PlaceCard(modifier: Modifier = Modifier, actions: Boolean = false) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            MockIcon(Icons.Filled.AddLocationAlt, size = 18.dp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            MiniTextLine(widthFraction = 0.7f, thickness = 5.dp)
            MiniTextLine(widthFraction = 0.95f, thickness = 4.dp)
        }
        if (actions) {
            Spacer(modifier = Modifier.width(8.dp))
            MockIcon(Icons.Filled.Edit)
            Spacer(modifier = Modifier.width(8.dp))
            MockIcon(Icons.Filled.Delete)
        }
    }
}

/** Панель навигации к месту: она висит справа сверху над картой. */
@Composable
private fun NavigationPanelMock() {
    Box(modifier = Modifier.fillMaxWidth().height(96.dp)) {
        MiniMap(modifier = Modifier.fillMaxSize(), withTrack = true)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .fillMaxWidth(0.72f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                MiniTextLine(widthFraction = 0.8f, thickness = 5.dp)
                MiniTextLine(widthFraction = 0.55f, thickness = 4.dp)
            }
            Spacer(modifier = Modifier.width(6.dp))
            MockIcon(Icons.Filled.Close, size = 14.dp)
        }
    }
}

@Composable
private fun SearchAndOwnSpeciesMock() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MockRoundButton(Icons.Filled.Search, size = 44.dp)
        MockField(modifier = Modifier.weight(1f), fillFraction = 0.5f)
        MockAddTile(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun FilterButtonMock() {
    MockButton(
        label = "${stringResource(StringKey.MapFilterButtonLabel)}: 3",
        tone = MockButtonTone.TONAL,
        leadingIcon = Icons.Filled.FilterList,
    )
}

@Composable
private fun PastWalksMapMock() {
    MiniMap(modifier = Modifier.fillMaxWidth().height(100.dp), withTrack = true, pastFinds = true)
}

// ─── «Архив прогулок» ───────────────────────────────────────────────────────────────────────────

@Composable
private fun WalkCardsMock() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MiniWalkCard(modifier = Modifier.height(48.dp), thumbnailWidth = 44.dp, lineThickness = 5.dp)
        MiniWalkCard(modifier = Modifier.height(48.dp), thumbnailWidth = 44.dp, lineThickness = 5.dp)
    }
}

@Composable
private fun WalkDetailMock() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatBox(modifier = Modifier.weight(1f))
        StatBox(modifier = Modifier.weight(1f))
        StatBox(modifier = Modifier.weight(1f))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DonutChartMock(modifier = Modifier.size(52.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            MiniTextLine(widthFraction = 0.9f, thickness = 5.dp)
            MiniTextLine(widthFraction = 0.65f, thickness = 5.dp)
        }
    }
    MockButton(stringResource(StringKey.WalkDetailViewMap), tone = MockButtonTone.TONAL)
}

@Composable
private fun StatBox(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MiniTextLine(widthFraction = 0.8f, thickness = 6.dp, color = MaterialTheme.colorScheme.primary)
        MiniTextLine(widthFraction = 0.6f, thickness = 4.dp)
    }
}

/** Круговая диаграмма находок по видам — как на «Карте находок» и в карточке прогулки. */
@Composable
private fun DonutChartMock(modifier: Modifier = Modifier) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
    )
    val hole = MaterialTheme.colorScheme.surfaceContainerLowest
    val sweeps = listOf(140f, 95f, 70f, 55f)
    Canvas(modifier = modifier) {
        var start = -90f
        sweeps.forEachIndexed { index, sweep ->
            drawArc(
                color = colors[index],
                startAngle = start,
                sweepAngle = sweep - 2f,
                useCenter = true,
            )
            start += sweep
        }
        drawCircle(color = hole, radius = size.minDimension * 0.28f)
    }
}

@Composable
private fun ShareDialogMock() {
    MockSectionTitle(stringResource(StringKey.WalkShareDialogTitle))
    MockCheckRow(checked = true, label = stringResource(StringKey.WalkShareOptionStats))
    MockCheckRow(checked = true, label = stringResource(StringKey.WalkShareOptionDiagram))
    MockCheckRow(checked = false, label = stringResource(StringKey.WalkShareOptionMap))
    MockButton(stringResource(StringKey.WalkDetailShareAction), leadingIcon = Icons.Filled.Share)
}

@Composable
private fun SelectionModeMock() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MiniWalkCard(
            modifier = Modifier.height(44.dp),
            thumbnailWidth = 36.dp,
            lineThickness = 5.dp,
            leading = { Checkbox(checked = true, onCheckedChange = null) },
        )
        MiniWalkCard(
            modifier = Modifier.height(44.dp),
            thumbnailWidth = 36.dp,
            lineThickness = 5.dp,
            leading = { Checkbox(checked = false, onCheckedChange = null) },
        )
    }
    MockButton(stringResource(StringKey.ArchiveDeleteWalksButton), leadingIcon = Icons.Filled.Delete)
}

@Composable
private fun UnfinishedWalkMock() {
    MiniWalkCard(
        modifier = Modifier.height(48.dp),
        thumbnailWidth = 44.dp,
        lineThickness = 5.dp,
        trailing = {
            Text(
                text = stringResource(StringKey.WalkDetailInProgress),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}

// ─── «Карта находок» ────────────────────────────────────────────────────────────────────────────

@Composable
private fun FindsMapMock() {
    MiniMap(modifier = Modifier.fillMaxWidth().height(110.dp), withTrack = false)
}

/** Скопление находок и оно же после приближения — то, ради чего в тексте сказано «приблизьте». */
@Composable
private fun ClusterZoomMock() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ClusterCanvas(modifier = Modifier.weight(1f).height(74.dp), clustered = true)
        MockIcon(Icons.AutoMirrored.Filled.ArrowForward, size = 18.dp)
        ClusterCanvas(modifier = Modifier.weight(1f).height(74.dp), clustered = false)
    }
}

@Composable
private fun ClusterCanvas(modifier: Modifier = Modifier, clustered: Boolean) {
    val findColor = MaterialTheme.colorScheme.error
    val onFind = MaterialTheme.colorScheme.onError
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (clustered) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension * 0.3f
                drawCircle(color = findColor.copy(alpha = 0.25f), radius = radius * 1.45f, center = center)
                drawFindDot(center, radius, findColor, onFind)
            } else {
                listOf(
                    Offset(size.width * 0.28f, size.height * 0.3f),
                    Offset(size.width * 0.6f, size.height * 0.24f),
                    Offset(size.width * 0.38f, size.height * 0.68f),
                    Offset(size.width * 0.72f, size.height * 0.62f),
                    Offset(size.width * 0.52f, size.height * 0.46f),
                ).forEach { center ->
                    drawFindDot(center, size.minDimension * 0.1f, findColor, onFind)
                }
            }
        }
        if (clustered) {
            Text(
                text = "24",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onError,
            )
        }
    }
}

@Composable
private fun SlidersMock() {
    MockSectionTitle(stringResource(StringKey.MapFilterDateRangeTitle))
    RangeSlider(value = 0.15f..0.7f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
    MockSectionTitle(stringResource(StringKey.MapFilterMonthRangeTitle))
    RangeSlider(value = 0.4f..0.85f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun StatsMock() {
    MockSectionTitle(stringResource(StringKey.MapStatsTitle))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatBox(modifier = Modifier.weight(1f))
        StatBox(modifier = Modifier.weight(1f))
        StatBox(modifier = Modifier.weight(1f))
        StatBox(modifier = Modifier.weight(1f))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            MiniMushroomTile(count = "9", modifier = Modifier.weight(1f), imageSize = 22.dp, controlSize = 10.dp)
            MiniMushroomTile(count = "4", modifier = Modifier.weight(1f), imageSize = 22.dp, controlSize = 10.dp)
        }
        DonutChartMock(modifier = Modifier.size(56.dp))
    }
}

@Composable
private fun FilterDialogMock() {
    MockButton(
        label = "${stringResource(StringKey.MapFilterButtonLabel)}: 2",
        tone = MockButtonTone.TONAL,
        leadingIcon = Icons.Filled.FilterList,
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MockSectionTitle(stringResource(StringKey.MapFilterDialogTitle))
        RangeSlider(value = 0.2f..0.75f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
        MockCheckRow(checked = true, label = stringResource(StringKey.MapFilterShowPastRoutes))
        MockCheckRow(checked = true)
        MockCheckRow(checked = false)
    }
}

@Composable
private fun PlaceCardMock() {
    PlaceCard(modifier = Modifier.fillMaxWidth(), actions = true)
}

// ─── «Мои грибы» ────────────────────────────────────────────────────────────────────────────────

@Composable
private fun SpeciesSectionsMock() {
    MockSectionTitle(stringResource(StringKey.SpeciesCollectionsTitle))
    MockCheckRow(checked = true)
    MockCheckRow(checked = false)
    MockSectionTitle(stringResource(StringKey.SpeciesMyMushroomsTitle))
    MockCheckRow(checked = true)
}

@Composable
private fun CollectionRowMock() {
    MockField(leadingIcon = Icons.Filled.Search, fillFraction = 0.45f)
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = true, onCheckedChange = null)
        Spacer(modifier = Modifier.width(8.dp))
        MiniTextLine(widthFraction = 0.6f, modifier = Modifier.weight(1f), thickness = 5.dp)
        MockIcon(Icons.Filled.KeyboardArrowDown)
    }
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SpeciesRow(checked = true)
        SpeciesRow(checked = false)
    }
}

/** Строка вида внутри подборки: галочка, картинка гриба, название. */
@Composable
private fun SpeciesRow(
    checked: Boolean,
    modifier: Modifier = Modifier,
    trailing: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = null)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            painter = painterResource(Res.drawable.ic_mushrooms),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        MiniTextLine(widthFraction = 0.55f, modifier = Modifier.weight(1f), thickness = 5.dp)
        trailing?.invoke(this)
    }
}

@Composable
private fun OwnSpeciesMock() {
    MockButton(stringResource(StringKey.SpeciesAddButton), leadingIcon = Icons.Filled.Add)
    SpeciesRow(
        checked = true,
        trailing = {
            MockIcon(Icons.Filled.Edit)
            Spacer(modifier = Modifier.width(12.dp))
            MockIcon(Icons.Filled.Close)
        },
    )
}

@Composable
private fun SpeciesCheckboxesMock() {
    SpeciesRow(checked = true)
    SpeciesRow(
        checked = false,
        trailing = {
            MockIcon(Icons.Filled.Close, tint = MaterialTheme.colorScheme.error)
        },
    )
}

// ─── «Предзагрузка» ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PreparationScreenMock() {
    Box(modifier = Modifier.fillMaxWidth().height(96.dp)) {
        MiniMap(modifier = Modifier.fillMaxSize(), withTrack = false)
        MockRoundButton(
            icon = Icons.Filled.Download,
            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
            size = 34.dp,
        )
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        RegionChip(modifier = Modifier.weight(1f), compact = true)
        RegionChip(modifier = Modifier.weight(1f), compact = true)
    }
}

@Composable
private fun DownloadAreaMock() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MockRoundButton(Icons.Filled.Download, size = 44.dp)
        Text(
            text = "12,4 ${stringResource(StringKey.UnitMegabytes)}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MockButton(
            stringResource(StringKey.PreparationCancelButton),
            tone = MockButtonTone.OUTLINED,
        )
        MockButton(
            stringResource(StringKey.PreparationDownloadThisAreaButton),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun RegionChipMock() {
    RegionChip(modifier = Modifier.fillMaxWidth(), compact = false)
}

/** Плашка скачанной области: название, состояние, полоса прогресса и кнопки над ней. */
@Composable
private fun RegionChip(modifier: Modifier = Modifier, compact: Boolean) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        MiniTextLine(widthFraction = 0.7f, thickness = 5.dp)
        if (!compact) {
            Text(
                text = stringResource(StringKey.PreparationStatusDownloading),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        LinearProgressIndicator(progress = { 0.6f }, modifier = Modifier.fillMaxWidth())
        if (!compact) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.End),
            ) {
                MockIcon(Icons.Filled.Pause)
                MockIcon(Icons.Filled.PlayArrow)
                MockIcon(Icons.Filled.Refresh)
                MockIcon(Icons.Filled.Delete)
            }
        }
    }
}

/** Одна большая область против нескольких маленьких: у большой сетка тайлов заметно грубее. */
@Composable
private fun AreaSizeMock() {
    val outline = MaterialTheme.colorScheme.outline
    val fill = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    val surface = MaterialTheme.colorScheme.surfaceVariant
    Row(
        modifier = Modifier.fillMaxWidth().height(76.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
            drawRect(color = surface)
            drawRect(color = fill)
            val step = size.width / 2f
            var x = step
            while (x < size.width) {
                drawLine(outline, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.5f)
                x += step
            }
            var y = size.height / 2f
            while (y < size.height) {
                drawLine(outline, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5f)
                y += size.height / 2f
            }
        }
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(2) {
                Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    drawRect(color = surface)
                    drawRect(color = fill)
                    val step = size.width / 6f
                    var x = step
                    while (x < size.width) {
                        drawLine(outline, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                        x += step
                    }
                    var y = size.height / 3f
                    while (y < size.height) {
                        drawLine(outline, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                        y += size.height / 3f
                    }
                }
            }
        }
    }
}

// ─── «Экспорт/Импорт» ───────────────────────────────────────────────────────────────────────────

@Composable
private fun TransferMock() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PhoneMock(modifier = Modifier.weight(1f))
        MockIcon(Icons.AutoMirrored.Filled.ArrowForward, size = 16.dp)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MockIcon(Icons.Filled.FileOpen, tint = MaterialTheme.colorScheme.primary, size = 28.dp)
        }
        MockIcon(Icons.AutoMirrored.Filled.ArrowForward, size = 16.dp)
        PhoneMock(modifier = Modifier.weight(1f))
    }
}

/** Условный телефон: рамка с треком внутри — то, что переезжает файлом. */
@Composable
private fun PhoneMock(modifier: Modifier = Modifier) {
    val trackColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .padding(5.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) { drawMiniTrack(trackColor) }
    }
}

@Composable
private fun ExportMock() {
    ModeSwitchMock(exportSelected = true)
    MockField(fillFraction = 0.55f)
    MockField(fillFraction = 0.4f, trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight)
    MockButton(stringResource(StringKey.DataDoneButton), leadingIcon = Icons.Filled.Check)
}

@Composable
private fun ImportMock() {
    ModeSwitchMock(exportSelected = false)
    MockButton(stringResource(StringKey.DataChooseFileButton), tone = MockButtonTone.TONAL, leadingIcon = Icons.Filled.FileOpen)
    MockField(fillFraction = 0.35f)
    MockButton(stringResource(StringKey.DataGoToArchiveButton))
}

/** Переключатель «Экспорт | Импорт» из шапки раздела. */
@Composable
private fun ModeSwitchMock(exportSelected: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
    ) {
        ModeSwitchHalf(
            label = stringResource(StringKey.DataExportOption),
            selected = exportSelected,
            modifier = Modifier.weight(1f),
        )
        ModeSwitchHalf(
            label = stringResource(StringKey.DataImportOption),
            selected = !exportSelected,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ModeSwitchHalf(label: String, selected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
            )
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// ─── «Настройки» ────────────────────────────────────────────────────────────────────────────────

@Composable
private fun SettingsScreenMock() {
    LanguageRowMock()
    MockSectionTitle(stringResource(StringKey.SettingsThemeTitle))
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = true, onClick = null)
        Spacer(modifier = Modifier.width(8.dp))
        MiniTextLine(widthFraction = 0.4f, thickness = 5.dp)
    }
    MockSectionTitle(stringResource(StringKey.SettingsMushroomSortTitle))
    MockCheckRow(checked = false)
}

@Composable
private fun LanguageRowMock() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${stringResource(StringKey.SettingsLanguageTitle)} → ${LocalAppLanguage.current.endonym}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        MockIcon(Icons.AutoMirrored.Filled.KeyboardArrowRight)
    }
}

@Composable
private fun ThemeOptionsMock() {
    ThemeOptionRow(stringResource(StringKey.SettingsThemeLight), selected = false)
    ThemeOptionRow(stringResource(StringKey.SettingsThemeSystem), selected = true)
    ThemeOptionRow(stringResource(StringKey.SettingsThemeDark), selected = false)
}

@Composable
private fun ThemeOptionRow(label: String, selected: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MushroomSizeMock() {
    Slider(value = 0.62f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_mushrooms),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp),
        )
        Icon(
            painter = painterResource(Res.drawable.ic_mushrooms),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(30.dp),
        )
        Icon(
            painter = painterResource(Res.drawable.ic_mushrooms),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(42.dp),
        )
    }
}

@Composable
private fun MushroomOrderMock() {
    MockCheckRow(checked = true, label = stringResource(StringKey.SettingsFreezeMushroomOrder))
    MockCheckRow(checked = false, label = stringResource(StringKey.SettingsResetMushroomOrderOnWalkFinish))
}

@Composable
private fun MapDataButtonsMock() {
    MockButton(stringResource(StringKey.SettingsRefreshMapDataButton), leadingIcon = Icons.Filled.Refresh)
    MockButton(
        stringResource(StringKey.SettingsClearMapCacheButton),
        tone = MockButtonTone.OUTLINED,
        leadingIcon = Icons.Filled.Delete,
    )
}
