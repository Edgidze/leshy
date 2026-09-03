package leshy.mushrooms.map.data.platform

import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.GeoPoint

/** One mushroom find, paired with its species — see [WalkThumbnailRenderer.render]'s `speciesMarkers`. */
data class WalkFindMarker(val location: GeoPoint, val category: Category)

/**
 * Renders a static map snapshot (real tiles + route + find markers) for a finished walk, once,
 * off-screen — not a live map view. Implementations resolve their own on-disk cache location
 * (mirrors how [rememberCameraLauncher] resolves its own "photos" directory rather than taking
 * one from the caller, since only platform code has a [android.content.Context] / `NSFileManager`
 * to resolve it from).
 *
 * [anchor] is the walk's current/last known location (e.g. GPS fix at Finish time) — used as the
 * snapshot region when [track] has too few points to bound a region itself (short walks where GPS
 * hadn't produced a second track point yet). Without it, such walks would have no location to
 * render a real map background around and would fall back to a backgroundless silhouette.
 *
 * [widthPx]/[heightPx]/[variant] let one walk have more than one cached snapshot at different
 * geometry/purpose — the walk's own snapshot (defaults, `walk_$walkId$WALK_THUMBNAIL_VARIANT.png`,
 * plain colored find dots) and a bigger square share-quality render (non-default `variant`, real
 * per-species icons via [speciesMarkers]) don't collide or overwrite each other. [findLocations]
 * is still always used for the dot path — both the default render, and as a per-marker fallback
 * when [speciesMarkers] is supplied but an individual icon fails to resolve/decode.
 *
 * [markerIconSizePx] is the aspect-fit box each [speciesMarkers] icon is baked into — callers
 * should scale it the same way the live map scales `ui/map/MushroomMarkerIcon.kt`'s
 * `mushroomMarkerSize` (`LocalMushroomMarkerSizeScale`), so the exported map's icons match the
 * user's own marker-size preference from Settings instead of a fixed size.
 *
 * Returns the absolute path to the written PNG on success, or `null` on any failure (no network,
 * snapshot timeout, no location known at all, etc.) — callers must fail gracefully, not crash or
 * block.
 */
interface WalkThumbnailRenderer {
    suspend fun render(
        walkId: Long,
        track: List<GeoPoint>,
        findLocations: List<GeoPoint>,
        anchor: GeoPoint?,
        widthPx: Int = WALK_THUMBNAIL_WIDTH_PX,
        heightPx: Int = WALK_THUMBNAIL_HEIGHT_PX,
        variant: String = WALK_THUMBNAIL_VARIANT,
        speciesMarkers: List<WalkFindMarker> = emptyList(),
        markerIconSizePx: Int = 64,
    ): String?
}

/**
 * Пропорция снимка прогулки — одна на оба места, где он показывается: заставку экрана детализации
 * (во всю ширину) и миниатюру карточки архива (ужатую до ширины миниатюры). Ровно поэтому она
 * живёт здесь, рядом с рендерером, а не в каждом из экранов: снимок рисуется один раз, и если
 * показывать его в поле другой пропорции, разница отыграется либо обрезкой маршрута, либо
 * растяжением.
 *
 * 16:9, а не квадрат, каким снимок был раньше. Квадрат достался в наследство от единственного
 * тогдашнего места показа — миниатюры 120dp в списке архива, где пропорция ничего не решала.
 * На заставке во всю ширину квадрат не годится дважды: во весь квадрат он съедает весь первый
 * экран, а вписанный в полосу — обрезается сверху и снизу вместе с маршрутом.
 */
const val WALK_THUMBNAIL_ASPECT_RATIO = 16f / 9f

/**
 * Разрешение снимка. 960×540 — это ×9 по числу точек против прежних 240×240, и цена этого не в
 * скорости отрисовки (снимок делается один раз, на «Финише»), а в весе файла: он лежит на диске
 * у каждой прогулки, пока прогулку не удалят.
 *
 * Ориентир для ширины — заставка на экране детализации: она занимает всю ширину экрана за вычетом
 * полей, то есть около 1200 точек на самых плотных нынешних телефонах (430dp при масштабе 3). До
 * этого числа 960 не дотягивает намеренно: разница вчетверо заметнее разницы вдвое, а между 960 и
 * 1200 на карте, где нет ни мелкого текста, ни тонких контрастных границ, разглядеть уже нечего.
 */
const val WALK_THUMBNAIL_WIDTH_PX = 960
const val WALK_THUMBNAIL_HEIGHT_PX = 540

/**
 * Приписка к имени файла снимка, называющая его геометрию.
 *
 * Нужна затем, чтобы устаревший снимок узнавался по одному имени файла, без чтения самого файла:
 * у прогулок, записанных до этой правки, на диске лежит квадратный `walk_<id>.png`, и показать его
 * в поле 16:9 можно только обрезав — то есть срезав у маршрута верх и низ. Поэтому имя файла
 * называет пропорцию, а [leshy.mushrooms.map.domain.usecase.BackfillWalkThumbnailsUseCase] считает
 * устаревшим всё, что названо иначе, и перерисовывает.
 *
 * Отсюда же следует правило на будущее: **меняешь [WALK_THUMBNAIL_ASPECT_RATIO] — меняй и эту
 * строку**, иначе старые снимки останутся лежать под именем нового формата и никем не будут
 * перерисованы.
 */
const val WALK_THUMBNAIL_VARIANT = "_w16x9"
