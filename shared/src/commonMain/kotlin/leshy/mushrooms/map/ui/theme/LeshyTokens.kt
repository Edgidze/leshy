package leshy.mushrooms.map.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.domain.model.Edition
import leshy.mushrooms.map.ui.components.MUSHROOM_PHOTO_ASPECT_RATIO
import org.jetbrains.compose.resources.DrawableResource
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.badge_wood
import leshy.shared.generated.resources.button_wood
import leshy.shared.generated.resources.card_dark
import leshy.shared.generated.resources.card_light
import leshy.shared.generated.resources.gribnye_icon
import leshy.shared.generated.resources.ground_dark
import leshy.shared.generated.resources.ground_light
import leshy.shared.generated.resources.leshy_icon

/**
 * Оформительские величины интерфейса, вынесенные из мест применения в один набор на редакцию.
 *
 * ## Зачем это существует
 *
 * Замер на 2026-09-25: цвета в проекте централизованы (три файла), типографика не задана вовсе,
 * а формы **захардкожены — сотня обращений к `RoundedCornerShape`/`CircleShape` против одного
 * обращения к `MaterialTheme.shapes`**. Значит вторая цветовая схема почти бесплатна, а «другая
 * форма» через подмену темы не достигается в принципе: подмена темы этой сотни не видит.
 *
 * Без такого набора различие редакций неизбежно превратилось бы в `if (edition == RUSSIA)`
 * внутри общих composable — ровно то, что запрещено правилом мержабельности
 * (`docs/russia-edition/README.md`, «Правки общих экранов»): добавленный файл при мердже не
 * конфликтует, отредактированный общий — конфликтует всегда, и проверять придётся обе ветки
 * каждого экрана.
 *
 * ## Токен называется по роли, а не по величине
 *
 * `shapeWalkCard`, а не `corner12`. Иначе получился бы переименованный хардкод, который ничего
 * не даёт: у мировой редакции одно и то же число 12 стоит у карточки прогулки, у плитки вида, у
 * миниатюры маршрута и у площадки фото — а меняться при второй редакции они обязаны
 * по-разному. Совпадение значений здесь — случайность истории, а не общее решение, и имя обязано
 * это различать.
 *
 * Следствие, которое стоит понимать заранее: **набор намеренно подробный**. Слить два токена в
 * один можно в любой момент, разделить обратно — только перечитав все места применения.
 *
 * ## Значения мировой редакции равны тем, что стояли в коде
 *
 * До единицы. Введение набора не меняет мировое приложение ни на пиксель — это условие, при
 * котором работа имеет смысл, а не пожелание. Единственное место, где число не переносилось
 * механически, — [shapeMapChrome]: там стояло `MaterialTheme.shapes.large`, и 16.dp — это
 * дефолт Material3 для `large`, который тема нигде не переопределяет.
 *
 * ## Чего здесь нет
 *
 * Цветов и типографики. Цвета живут в `ColorScheme` (`Theme.kt`), и Material умеет подменять их
 * сам; дублировать их сюда значило бы завести второй источник правды. Типографика будет заведена
 * вместе с российским шрифтовым набором; здесь от неё только [typeScaleStep] — множитель, на
 * который редакция двигает всю шкалу кегля разом.
 */
@Immutable
data class LeshyTokens(
    // ---- Скругления: диалоги и карта -------------------------------------------------------
    /** Диалог. Десять мест — все диалоги проекта, включая полноэкранные. */
    val shapeDialog: Shape,
    /** Заставка карты: шапка экрана прогулки и карточка «последняя прогулка» на карте находок. */
    val shapeHeroMap: Shape,
    /**
     * Плашки поверх полноэкранной карты (кнопка «назад», заголовок). Единственное место в
     * проекте, где стояло `MaterialTheme.shapes.large`; 16.dp — его дефолт в Material3.
     */
    val shapeMapChrome: Shape,
    /** Подложка подписи, лежащая НА карте: снапшот прогулки и карточка на карте находок. */
    val shapeMapBadge: Shape,

    // ---- Скругления: карточки, плитки, площадки ---------------------------------------------
    /** Обложка карточки прогулки в архиве. */
    val shapeWalkCard: Shape,
    /** Нарисованная миниатюра маршрута — та, что заменяет снимок, когда снимка нет. */
    val shapeRouteThumbnail: Shape,
    /** Плитка вида в поиске и в выборе каталожного фото — та, что с рамкой цвета вида. */
    val shapeSpeciesTile: Shape,
    /** Площадка фотографии в форме вида и в редакторе значка. */
    val shapePhotoPreview: Shape,
    /** Маленькая квадратная миниатюра фото места в списке прогулки. */
    val shapePlaceThumbnail: Shape,
    /** Кликабельная строка-список без собственной подложки (ссылка на политику, строка согласия). */
    val shapeListRow: Shape,
    /**
     * Значок приложения, нарисованный внутри интерфейса (приветственный экран). 25.dp — примерно
     * та же доля стороны (≈22%), с какой iOS скругляет иконки: значок там показан размером 112dp.
     */
    val shapeAppIcon: Shape,

    // ---- Скругления: кнопки -----------------------------------------------------------------
    /** Крупная кнопка действия на «Записи» («Старт», «Пауза», «Завершить»). */
    val shapeActionButton: Shape,
    /**
     * Обычная кнопка — заливная, контурная, текстовая.
     *
     * Единственный токен, чьё мировое значение **не** было написано в коде: кнопки брали форму из
     * Material (`ButtonDefaults.shape`, то есть `CircleShape` — пилюля), и потому в подсчёте ста
     * захардкоженных мест не участвовали. Токен заведён вместе с российским набором: подменой
     * темы кнопку не расквадратить, `ButtonDefaults.shape` приходит не из `MaterialTheme.shapes`,
     * а из жёстко зашитого `CornerFull`.
     */
    val shapeButton: Shape,
    /**
     * Основа формы ряда-переключателя (`SingleChoiceSegmentedButtonRow`): из неё Material
     * собирает скругления крайних сегментов.
     *
     * Второй токен формы кнопки нужен из-за типа: `SegmentedButtonDefaults.itemShape` принимает
     * не `Shape`, а `CornerBasedShape` — ему нужно уметь обнулить внутренние углы. Мировое
     * значение — `CircleShape`, то же, что отдаёт `SegmentedButtonDefaults.baseShape`
     * (`CornerFull`).
     */
    val shapeSegmentBase: CornerBasedShape,
    /**
     * Подсветка выбранного пункта выдвижного меню. Мировое значение — `CircleShape`, дефолт
     * параметра `NavigationDrawerItem`; из темы эта форма тоже не приходит.
     */
    val shapeDrawerItem: Shape,
    /** Круглая кнопка-значок (навигация по карте, мокапы кнопок в подсказках). */
    val shapeRoundButton: Shape,
    /** Кнопка счётчика «+»/«−» на плитке вида. Главная круглая форма приложения. */
    val shapeCountButton: Shape,
    /** Пилюля: дорожка ползунка, кнопка-пилюля и переключатель в иллюстрациях. */
    val shapePill: Shape,

    // ---- Скругления: частные случаи ---------------------------------------------------------
    /** Выдвижная панель навигации: скруглён только нижний левый угол, остальные — на краях экрана. */
    val shapeNavigationOverlay: Shape,
    /** Образец цвета и бегунок спектра в форме вида. */
    val shapeColorSwatch: Shape,
    /** Лупа редактора значка. Оптический прибор, а не элемент управления — круг здесь по смыслу. */
    val shapeMagnifier: Shape,

    // ---- Скругления: иллюстрации ------------------------------------------------------------
    //
    // Иллюстрации подсказок и приветствия — это НАРИСОВАННЫЙ интерфейс: уменьшенные копии
    // настоящих экранов. Их величины живут отдельно от настоящих намеренно: они подобраны под
    // масштаб рисунка (в четверть натуральной величины 12dp выглядят как 3dp), и приравнивание
    // их к боевым токенам сделало бы рисунок неузнаваемым, а не согласованным.
    /** Рама иллюстрации в подсказках. */
    val shapeIllustrationFrame: Shape,
    /**
     * Рама виньетки на приветственном экране. Заметно меньше, чем у настоящих карточек, — ровно
     * ради ощущения макета, а не второго интерфейса поверх первого.
     */
    val shapeVignetteFrame: Shape,
    /** Нарисованная панель во всю ширину иллюстрации (список фильтров). */
    val shapeIllustrationPanel: Shape,
    /** Нарисованная карточка внутри иллюстрации. */
    val shapeIllustrationCard: Shape,
    /** Нарисованная плитка, мини-карта, мини-строка меню. */
    val shapeIllustrationTile: Shape,
    /** Самый мелкий нарисованный элемент — миниатюра маршрута внутри мини-карточки. */
    val shapeIllustrationThumbnail: Shape,

    // ---- Толщины и отступы ------------------------------------------------------------------
    /** Рамка цвета вида вокруг плитки — единственная содержательная рамка мировой редакции. */
    val widthSpeciesTileBorder: Dp,
    /**
     * Рама вокруг карточки. **У мировой редакции её нет — 0.dp**, и это не заглушка, а факт:
     * её элементы «растворяются» в фоне. Российская редакция обрамляет каждый элемент, и
     * различие силуэта делает именно рама вместе с малыми радиусами.
     *
     * Рамы вокруг ФОТОГРАФИИ здесь нет, хотя `design.md` (раздел 6) её описывал: собрана и
     * отвергнута владельцем на устройстве 2026-09-26 — на плитке 120dp алая линия шла в паре
     * миллиметров от рамки цвета вида и спорила с ней. Вместе с ней отвергнут белый мат вокруг
     * фотографии и «табличка» под подписями. Токенов под них поэтому нет: величина, которую не
     * читает ни один composable, — это не задел, а обещание, которое некому исполнить.
     */
    val frameWidth: Dp,

    // ---- Не-геометрические токены -----------------------------------------------------------
    /** Как рисуется поверхность под содержимым. */
    val surfaceStyle: SurfaceStyle,
    /**
     * Текстура земли под карточками. `null` выключает её целиком и оставляет сплошной цвет —
     * заранее предусмотренный аварийный выход, если на устройстве результат не понравится:
     * остальная часть оформления при этом продолжает работать.
     */
    val groundTexture: DrawableResource?,
    /**
     * Подложка под стоковый глиф Material — «жетон» из `design.md`, разделы 3 и 9.
     *
     * `null` — глиф рисуется сам по себе, как в мировой редакции. Своего набора глифов ни одна
     * редакция не рисует (решение владельца): меняется не значок, а то, на чём он лежит, и это
     * даёт сдвиг восприятия ценой одного файла на всё приложение.
     */
    val iconBadge: DrawableResource?,
    /**
     * Значок приложения, нарисованный ВНУТРИ интерфейса — приветственный экран и заставка
     * холодного старта.
     *
     * Токен, а не константа в экране: значок — это первое, по чему человек узнаёт, какое
     * приложение он открыл, и «Леший» на первой странице продукта, поданного как другое
     * приложение, — ровно тот остаток мировой редакции, который ищет задача 14
     * (`docs/russia-edition/track-app.md`).
     *
     * Растр каждой редакции несёт СОБСТВЕННЫЙ силуэт: у мирового значка углы залиты его же
     * фоном и обрезаются клипом [shapeAppIcon], у российского они прозрачны, и клипу там резать
     * нечего. Поэтому форма идёт отдельным токеном, а не выводится из картинки.
     */
    val appIcon: DrawableResource,
    /**
     * Чем накрыт кадр холодного старта, пока из хранилища ещё не прочитано, показывать
     * онбординг или граф навигации (`App.kt`, `onboardingCompleted == null`).
     *
     * `null` — не рисуется ничего, и кадр остаётся пустым, как был всегда: **мировая редакция
     * заставки не получает**, это её прежнее поведение, а не упущение. У обрамлённой редакции
     * заставка осмысленна ровно потому, что ей есть что показать — землю, на которой лежит весь
     * остальной интерфейс.
     *
     * Второй токен на ту же картинку, что [appIcon], не дублирование: один отвечает на вопрос
     * «какой значок у этого продукта», второй — «показывает ли редакция заставку вообще».
     */
    val splashLogo: DrawableResource?,
    /**
     * Доска карточек — плитки видов, карточки прогулок, полка под лентой «Записи».
     *
     * Отдельная от [groundTexture] не ради разнообразия: карточка обязана читаться как предмет,
     * ЛЕЖАЩИЙ на земле. Различают их тон (карточка светлее) и направление волокна (у земли доски
     * вертикальные, у карточек поперёк) — одного тона мало, на стыке двух кусков с одинаковым
     * рисунком глаз видит пятно, а не край.
     */
    val cardTexture: DrawableResource?,
    /**
     * Доска заливных кнопок — та же древесина, что у [iconBadge], и это условие, а не совпадение:
     * ряд кнопок и ряд значков на жетонах стоят на одном экране, и разный материал прочитался бы
     * как два разных приложения.
     */
    val buttonTexture: DrawableResource?,
    /**
     * Нижняя граница числа колонок в сетке плиток находок — оно же то, что получается на телефоне
     * вертикально. Верхняя граница считается от ширины экрана, см. `FindTilesGrid`.
     *
     * Две, а не три. Плитка собрана из `MushroomPhoto`, а у той подпись с названием вида лежит
     * поверх картинки блоком постоянной высоты (54dp, две строки по 20sp — размер выбран под ленту
     * «Записи», где плитка шириной 120dp). При трёх колонках плитке достаётся 90–104dp, картинка
     * становится 72–83dp высотой, и подпись съедает три четверти её высоты. При двух колонках
     * плитка выходит 140–160dp, то есть не уже той, под которую подпись и рисовалась.
     */
    val findTileMinColumns: Int,
    /**
     * Пропорции площадки фотографии вида.
     *
     * **Осторожно при смене в редакции.** Это же число участвует в `RECORD_TILE_HEIGHT` —
     * верхнеуровневом `val` в `MushroomTile.kt`, который считается ВНЕ композиции и потому
     * токена не видит. Пока значение равно [MUSHROOM_PHOTO_ASPECT_RATIO], расхождения нет;
     * стоит их развести — и высота ленты на «Записи» перестанет соответствовать высоте плиток.
     * Разводить редакции по этому токену можно только вместе с переносом `RECORD_TILE_HEIGHT`
     * в композицию.
     */
    val photoAspectRatio: Float,
    /** Множитель, на который редакция двигает всю шкалу кегля разом. */
    val typeScaleStep: Float,
)

/** Как редакция рисует поверхность под содержимым. */
enum class SurfaceStyle {
    /** Плоская заливка цветом схемы, без рамы. Мировая редакция. */
    FLAT,

    /** Обрамлённая поверхность: рама, мат, текстура под ней. */
    FRAMED,
}

/**
 * Набор мировой редакции. **Каждое значение здесь равно тому, что стояло в коде до появления
 * этого файла** — таблица соответствия «старое значение → токен → где стояло» приложена к
 * коммиту, которым заведён набор.
 */
private val WorldTokens = LeshyTokens(
    shapeDialog = RoundedCornerShape(24.dp),
    shapeHeroMap = RoundedCornerShape(16.dp),
    shapeMapChrome = RoundedCornerShape(16.dp),
    shapeMapBadge = CircleShape,
    shapeWalkCard = RoundedCornerShape(12.dp),
    shapeRouteThumbnail = RoundedCornerShape(12.dp),
    shapeSpeciesTile = RoundedCornerShape(12.dp),
    shapePhotoPreview = RoundedCornerShape(12.dp),
    shapePlaceThumbnail = RoundedCornerShape(8.dp),
    shapeListRow = RoundedCornerShape(8.dp),
    shapeAppIcon = RoundedCornerShape(25.dp),
    shapeActionButton = RoundedCornerShape(20.dp),
    // Ровно то, что отдаёт ButtonDefaults.shape, — проверено скриншот-дифом мировой сборки.
    shapeButton = CircleShape,
    shapeSegmentBase = CircleShape,
    shapeDrawerItem = CircleShape,
    shapeRoundButton = CircleShape,
    shapeCountButton = CircleShape,
    shapePill = CircleShape,
    shapeNavigationOverlay = RoundedCornerShape(bottomStart = 20.dp),
    shapeColorSwatch = CircleShape,
    shapeMagnifier = CircleShape,
    shapeIllustrationFrame = RoundedCornerShape(12.dp),
    shapeVignetteFrame = RoundedCornerShape(10.dp),
    shapeIllustrationPanel = RoundedCornerShape(10.dp),
    shapeIllustrationCard = RoundedCornerShape(8.dp),
    shapeIllustrationTile = RoundedCornerShape(6.dp),
    shapeIllustrationThumbnail = RoundedCornerShape(4.dp),
    widthSpeciesTileBorder = 2.dp,
    frameWidth = 0.dp,
    surfaceStyle = SurfaceStyle.FLAT,
    groundTexture = null,
    iconBadge = null,
    appIcon = Res.drawable.leshy_icon,
    splashLogo = null,
    cardTexture = null,
    buttonTexture = null,
    findTileMinColumns = 2,
    photoAspectRatio = MUSHROOM_PHOTO_ASPECT_RATIO,
    typeScaleStep = 1f,
)

/**
 * Набор «Грибных прогулок»: **малые радиусы и прямые рамы** вместо 12–24dp и пилюль.
 *
 * Именно квадратность вместе с рамой даёт главный сдвиг силуэта. У мирового приложения элементы
 * растворяются в фоне; у российского каждый обрамлён — `docs/russia-edition/design.md`, раздел 6.
 *
 * Круглые формы уходят в квадратные все, включая кнопку счётчика «+»/«−» — это самая заметная
 * круглая форма приложения, и оставить её круглой значило бы сохранить прежний силуэт там, где он
 * виднее всего. Исключение ровно одно, [shapeMagnifier]: лупа редактора значка — оптический
 * прибор, а не элемент управления, и квадратная лупа читалась бы как дефект.
 *
 * Иллюстрации следуют за интерфейсом, но вдвое мельче: они нарисованы в масштабе примерно
 * четверти натуральной величины, и 4dp там выглядели бы как 16dp на настоящем экране.
 *
 * **Рамы и мат заданы, но пока нигде не рисуются** — обёртки поверхностей появятся отдельно
 * (шаг 3 порядка работ раздела 15). Значения проставлены сразу, чтобы обёртке не пришлось
 * приносить их с собой.
 */
private val RussiaTokensLight = WorldTokens.copy(
    shapeDialog = RoundedCornerShape(6.dp),
    shapeHeroMap = RoundedCornerShape(4.dp),
    shapeMapChrome = RoundedCornerShape(4.dp),
    shapeMapBadge = RoundedCornerShape(4.dp),
    shapeWalkCard = RoundedCornerShape(4.dp),
    shapeRouteThumbnail = RoundedCornerShape(4.dp),
    shapeSpeciesTile = RoundedCornerShape(4.dp),
    shapePhotoPreview = RoundedCornerShape(4.dp),
    shapePlaceThumbnail = RoundedCornerShape(4.dp),
    shapeListRow = RoundedCornerShape(4.dp),
    // Свой значок несёт собственный силуэт: поле вокруг рамы снято в прозрачность
    // (`tools/generate_russia_app_icons.py`, `in_app_icon`), скруглены сами бруски рамы. Клипу
    // здесь резать нечего, а клип с любым другим радиусом срезал бы у рамы углы.
    shapeAppIcon = RectangleShape,
    appIcon = Res.drawable.gribnye_icon,
    // Заставка холодного старта — та же картинка на земле; разбор, зачем она редакции нужна и
    // почему мировая остаётся без неё, — в KDoc токена.
    splashLogo = Res.drawable.gribnye_icon,
    shapeActionButton = RoundedCornerShape(4.dp),
    shapeButton = RoundedCornerShape(4.dp),
    shapeSegmentBase = RoundedCornerShape(4.dp),
    shapeDrawerItem = RoundedCornerShape(4.dp),
    shapeRoundButton = RoundedCornerShape(4.dp),
    shapeCountButton = RoundedCornerShape(4.dp),
    shapePill = RoundedCornerShape(4.dp),
    shapeNavigationOverlay = RoundedCornerShape(bottomStart = 6.dp),
    shapeColorSwatch = RoundedCornerShape(4.dp),
    shapeIllustrationFrame = RoundedCornerShape(3.dp),
    shapeVignetteFrame = RoundedCornerShape(3.dp),
    shapeIllustrationPanel = RoundedCornerShape(2.dp),
    shapeIllustrationCard = RoundedCornerShape(2.dp),
    shapeIllustrationTile = RoundedCornerShape(2.dp),
    shapeIllustrationThumbnail = RoundedCornerShape(1.dp),
    frameWidth = 2.dp,
    surfaceStyle = SurfaceStyle.FRAMED,
    iconBadge = Res.drawable.badge_wood,
    buttonTexture = Res.drawable.button_wood,
    // Шкала кегля на ступень выше мировой: аудитория сбора грибов смещена к старшему возрасту, и
    // это совпадает с требованием читаемости на солнце. Пока не читается никем — типографику
    // редакция получит вместе со шрифтами (раздел 5 design.md), — но величина решена здесь же.
    typeScaleStep = 1.1f,
    groundTexture = Res.drawable.ground_light,
    cardTexture = Res.drawable.card_light,
)

/** Тот же набор в мореной гамме: тёмная тема отличается на уровне величин только досками. */
private val RussiaTokensDark = RussiaTokensLight.copy(
    groundTexture = Res.drawable.ground_dark,
    cardTexture = Res.drawable.card_dark,
)

/**
 * Набор редакции.
 *
 * [dark] нужен доскам ([LeshyTokens.groundTexture], [LeshyTokens.cardTexture]): это растры, и в
 * тёмной теме они не перекрашиваются, а заменяются на мореные (`design.md`, раздел 7). Жетон и
 * доска кнопок темны в обеих темах, поэтому от [dark] не зависят. Остальные токены тоже: цвета
 * живут в `colorScheme`, формы и отступы одинаковы в обеих темах.
 *
 * Оба варианта российского набора собраны заранее, а не через `copy` на каждый вызов: набор
 * раздаётся через `staticCompositionLocalOf`, и новый экземпляр на каждую рекомпозицию темы
 * означал бы перезапуск всей композиции под ней.
 */
fun leshyTokensFor(edition: Edition, dark: Boolean = false): LeshyTokens = when (edition) {
    Edition.WORLD -> WorldTokens
    Edition.RUSSIA -> if (dark) RussiaTokensDark else RussiaTokensLight
}

/**
 * `staticCompositionLocalOf`, а не `compositionLocalOf`: набор меняется ровно один раз за
 * запуск — при выборе редакции хостом, — и отслеживать чтения поимённо незачем, это только
 * стоило бы лишней работы на каждом чтении.
 *
 * Дефолта нет намеренно: композиция без [LeshyTheme] обязана падать на месте, а не рисовать
 * втихую чужие величины.
 */
val LocalLeshyTokens = staticCompositionLocalOf<LeshyTokens> {
    error("LocalLeshyTokens не предоставлен — композиция должна быть внутри LeshyTheme")
}
