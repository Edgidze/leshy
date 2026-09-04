package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.data.platform.WALK_THUMBNAIL_ASPECT_RATIO
import leshy.mushrooms.map.domain.model.iconSource
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.map.MapStats
import leshy.mushrooms.map.presentation.map.MapUiState
import leshy.mushrooms.map.presentation.map.MapViewModel
import leshy.mushrooms.map.presentation.mapfilter.MapFilterUiState
import leshy.mushrooms.map.presentation.mapfilter.MapFilterViewModel
import leshy.mushrooms.map.ui.components.FindTilesGrid
import leshy.mushrooms.map.ui.components.FindsEmptyBlock
import leshy.mushrooms.map.ui.components.LoadingState
import leshy.mushrooms.map.ui.components.MapDateRangeSlider
import leshy.mushrooms.map.ui.components.MapMonthRangeSlider
import leshy.mushrooms.map.ui.components.MetricCard
import leshy.mushrooms.map.ui.components.MushroomPieChart
import leshy.mushrooms.map.ui.components.NoWalksYetState
import leshy.mushrooms.map.ui.components.SectionHeader
import leshy.mushrooms.map.ui.components.hasDateRange
import leshy.mushrooms.map.ui.map.AggregatedFindsMap
import leshy.mushrooms.map.ui.map.MapMarker
import leshy.mushrooms.map.ui.map.mapOrnamentOptions
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.ui.util.formatDurationLabeled
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.ic_mushrooms
import leshy.shared.generated.resources.ic_route
import leshy.shared.generated.resources.ic_stopwatch
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.map.GestureOptions

private val SCREEN_PADDING = 16.dp
private val HERO_CORNER_RADIUS = 16.dp
private val METRIC_SPACING = 8.dp

/** Высота полосы «идёт пересчёт» — место под неё занято всегда, см. её место в [MapScreen]. */
private val BUSY_INDICATOR_HEIGHT = 8.dp
private val BUSY_INDICATOR_TOP_GAP = 12.dp

/** Столько же, сколько на диаграмме прогулки (`WalkDetailScreen`). */
private val MUSHROOM_TOAST_DURATION = 3000.milliseconds

/**
 * «Карта находок» — та же страница-детализация, что у одной прогулки, только по всем сразу:
 * заставка с картой наверху, под ней свод.
 *
 * Прежде экран был двумя подразделами по переключателю «Карта»/«Статистика»: под первым лежала
 * карта во весь экран, под вторым — список строк «ключ: значение». Переключатель разводил по
 * разным экранам две вещи, которые смотрят вместе («где» и «сколько»), а список чисел не имел
 * ничего общего с тем, как те же числа показаны у отдельной прогулки. Теперь строение одно на
 * оба экрана (`WalkDetailScreen`, `ui/components/StatsBlocks.kt`), и карта осталась во весь
 * экран — но как переход с заставки ([FindsMapScreen]), а не как половина переключателя.
 *
 * Ползунки дат и сезона стоят прямо на странице, а не за кнопкой фильтра: свод под ними
 * пересчитывается на месте, и «сколько было в сентябре» — это движение пальцем, а не поход в
 * диалог и обратно. Фильтр по видам в ползунки не влезает и остался кнопкой — на полноэкранной
 * карте, где он и нужен.
 */
@Composable
fun MapScreen(
    onStartWalkClick: () -> Unit,
    onOpenFullMap: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = koinViewModel(),
    filterViewModel: MapFilterViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val filterState by filterViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Пока база не ответила, `walkCount == 0` означает «ещё не знаем», а не «прогулок нет» —
    // см. [leshy.mushrooms.map.ui.components.LoadingState].
    if (uiState.isLoading) {
        LoadingState(modifier = modifier)
        return
    }
    // Пусто ли вообще — по `hasAnyWalks`, а не по числу прогулок в своде: второе обнуляется и
    // фильтром, а предлагать «запишите первую прогулку» тому, кто просто сдвинул ползунок мимо
    // своих прогулок, неверно — и ползунок, которым это исправить, вместе с приглашением пропал
    // бы с экрана.
    if (!uiState.hasAnyWalks) {
        NoWalksYetState(
            descriptionKey = StringKey.MapStatsEmptyHint,
            onStartWalkClick = onStartWalkClick,
            modifier = modifier,
        )
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Не `LazyColumn`, в отличие от экрана прогулки: на заставке живая карта MapLibre,
                // а `LazyColumn` уничтожал бы её нативное представление, стоит ей уехать за верх
                // экрана, и заводил заново на возврате — то есть на каждой прокрутке туда-обратно.
                // Содержимое ниже — свод и плитки найденных видов, десятки элементов, ленивость
                // им ничего не даёт.
                .verticalScroll(rememberScrollState())
                .padding(start = SCREEN_PADDING, end = SCREEN_PADDING, bottom = 24.dp),
        ) {
            FindsMapHero(uiState = uiState, onClick = onOpenFullMap)

            if (filterState.hasDateRange) {
                FilterSliders(
                    filterState = filterState,
                    onDateRangeChanged = filterViewModel::setDateRange,
                    onMonthRangeChanged = filterViewModel::setMonthRange,
                )
            }

            // Полоса занятости стоит между ползунками и сводом — ровно там, где проходит граница
            // между тем, что пользователь только что сдвинул, и тем, что от этого пересчитывается.
            // Место под неё занято всегда, даже когда её не видно: иначе появление полосы толкало
            // бы весь свод вниз на её высоту.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // Отбивка от ползунка сезона: без неё полоса прилипает вплотную к его дорожке
                    // и читается как её часть, а не как отдельный признак занятости.
                    .padding(top = BUSY_INDICATOR_TOP_GAP)
                    .height(BUSY_INDICATOR_HEIGHT),
            ) {
                if (uiState.isRecalculating) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }

            SectionHeader(title = stringResource(StringKey.MapStatsTitle))
            MapMetrics(stats = uiState.stats)

            if (uiState.stats.categoryCounts.isEmpty()) {
                FindsEmptyBlock()
            } else {
                SectionHeader(title = stringResource(StringKey.WalkDetailFindsTitle))
                FindTilesGrid(counts = uiState.stats.categoryCounts)
                MushroomPieChart(
                    counts = uiState.stats.categoryCounts,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    onMushroomClick = { name ->
                        coroutineScope.launch {
                            // showSnackbar принимает только фиксированные Short/Long/Indefinite —
                            // Indefinite плюс ручное снятие ровно через MUSHROOM_TOAST_DURATION и
                            // есть способ получить свою длительность (как в WalkDetailScreen).
                            launch {
                                delay(MUSHROOM_TOAST_DURATION)
                                snackbarHostState.currentSnackbarData?.dismiss()
                            }
                            snackbarHostState.showSnackbar(message = name, duration = SnackbarDuration.Indefinite)
                        }
                    },
                )
            }
        }
        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

/**
 * Заставка — живая карта, а не снимок: снимка по всем прогулкам сразу никто не рисует (у каждой
 * прогулки свой, снятый на «Финише»), а рисовать его здесь значило бы заводить целое поколение
 * кешируемых картинок ради одной картинки на экране.
 *
 * Жесты у карты выключены, и поверх неё лежит прозрачная кнопка во всю заставку: без неё карта
 * съедала бы вертикальное перетаскивание, и страница под пальцем на заставке не прокручивалась
 * бы. Компас с линейкой масштаба сняты — они обещают управление, которого здесь нет; логотип и
 * копирайт оставлены, они обязательны.
 *
 * Отмеченных мест на заставке нет, в отличие от полноэкранной карты: каждое место с фотографией
 * — это свой `SymbolLayer` со своей растеризованной картинкой (см. «Стоимость слоя» в
 * `ui/map/CLAUDE.md`), а разглядеть их в поле высотой в треть экрана всё равно нельзя.
 */
@Composable
private fun FindsMapHero(
    uiState: MapUiState,
    onClick: () -> Unit,
) {
    val categoryById = uiState.categories.associateBy { it.id }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .aspectRatio(WALK_THUMBNAIL_ASPECT_RATIO)
            .clip(RoundedCornerShape(HERO_CORNER_RADIUS))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        AggregatedFindsMap(
            tracks = uiState.tracks,
            markers = uiState.findMarks.map { mark ->
                val category = categoryById[mark.categoryId]
                MapMarker(
                    lat = mark.lat,
                    lon = mark.lon,
                    colorHex = category?.colorHex ?: "#808080",
                    icon = category?.iconSource(),
                )
            },
            modifier = Modifier.fillMaxSize(),
            gestureOptions = GestureOptions.AllDisabled,
            ornamentOptions = mapOrnamentOptions.copy(isCompassEnabled = false, isScaleBarEnabled = false),
            showLoadFailedBanner = false,
        )

        Box(modifier = Modifier.fillMaxSize().clickable(onClick = onClick))

        // Подложка у подписи непрозрачная: она ложится на карту, где под ней может оказаться что
        // угодно — от светлого поля до тёмного леса. Ровно как на заставке прогулки.
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(StringKey.WalkDetailViewMap),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun FilterSliders(
    filterState: MapFilterUiState,
    onDateRangeChanged: (Long, Long) -> Unit,
    onMonthRangeChanged: (Int, Int) -> Unit,
) {
    MapDateRangeSlider(
        uiState = filterState,
        onRangeChanged = onDateRangeChanged,
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
    )
    MapMonthRangeSlider(
        uiState = filterState,
        onRangeChanged = onMonthRangeChanged,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
    )
}

/**
 * Четыре показателя по два в ряд, а не четыре в ряд, как три у прогулки: на узком экране четвёртой
 * плашке достаётся около 76dp, и «12.34 км» в ней ломается на две строки уже при системном кегле,
 * не говоря о крупном. Пары подобраны по смыслу: сверху «что нашёл и за сколько выходов», снизу
 * «сколько прошёл и за сколько времени».
 *
 * Значок прогулки — стоковый `Hiking`: своей картинки под «прогулку» у приложения нет, а рисовать
 * её ради одной плашки незачем (см. остальные три — они растровые только потому, что рисовались
 * под шапку «Записи»).
 */
@Composable
private fun MapMetrics(stats: MapStats) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(METRIC_SPACING),
    ) {
        MetricCard(
            icon = painterResource(Res.drawable.ic_mushrooms),
            label = stringResource(StringKey.MapStatsFindsCount),
            value = stats.totalMushroomCount.toString(),
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            icon = rememberVectorPainter(Icons.Filled.Hiking),
            label = stringResource(StringKey.MapStatsWalksCount),
            value = stats.walkCount.toString(),
            modifier = Modifier.weight(1f),
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = METRIC_SPACING),
        horizontalArrangement = Arrangement.spacedBy(METRIC_SPACING),
    ) {
        MetricCard(
            icon = painterResource(Res.drawable.ic_route),
            label = stringResource(StringKey.WalkDetailDistance),
            value = formatDistanceKm(stats.totalDistanceMeters),
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            icon = painterResource(Res.drawable.ic_stopwatch),
            label = stringResource(StringKey.WalkDetailDuration),
            value = formatDurationLabeled(stats.totalDurationMillis),
            modifier = Modifier.weight(1f),
        )
    }
}
