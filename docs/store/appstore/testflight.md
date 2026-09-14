# TestFlight — тексты для раздачи тестировщикам

Заполняется в App Store Connect → вкладка **TestFlight**. Поля разные у двух мест:

| Где | Поле | Что вставлять |
|---|---|---|
| TestFlight → **Test Information** (уровень приложения, заполняется один раз) | Beta App Description, Feedback Email, Privacy Policy URL | разделы «Beta App Description» ниже, почта и URL из таблицы |
| TestFlight → конкретный билд → **What to Test** | What to Test | раздел «What to Test» ниже |

Локализации те же, что у листинга, — `en-US` и `ru-RU`. Лимит каждого поля — 4000 символов,
оба текста ниже укладываются с большим запасом.

| Поле | Значение |
|---|---|
| Feedback Email | `fefradkin@gmail.com` |
| Privacy Policy URL | `https://leshy-mapper.github.io/mushrooms-map/privacy.html` |

**Зачем именно такие просьбы в What to Test.** Первая — прицельно про сворачивание
приложения на активной записи: это единственный известный сценарий, который трижды убивал
приложение на iPhone SE 2016 (`.claude/investigations/ios-maplibre-background-watchdog/`).
Вторая — про модель и версию iOS в каждом отчёте: без модели репорт невозможно отличить от
того же класса инцидентов, ограниченного эффективно двухъядерным железом, и решение из задачи
10 плана принимать будет не по чему.

---

## Beta App Description — en-US

Leshy is a field notebook for mushroom picking. It records your walk while you walk it: the
track, every find with coordinates and time, photos, and landmarks worth remembering. Past
walks stay in the archive, and all finds together build a map you can come back to next
season.

Everything stays on the device. There is no account, no analytics, no crash reporting, and
nothing is uploaded anywhere. The only network request the app makes is for map tiles.

The app does not identify mushrooms and never tells you whether something is edible.

## Beta App Description — ru-RU

«Леший» — полевой блокнот тихой охоты. Записывает прогулку, пока вы идёте: трек, каждую
находку с координатами и временем, фотографии и ориентиры, которые стоит запомнить. Прошлые
прогулки остаются в архиве, а все находки вместе складываются в карту, к которой можно
вернуться на следующий сезон.

Всё хранится на устройстве. Нет ни учётной записи, ни аналитики, ни сбора отчётов о сбоях,
никуда ничего не отправляется. Единственный сетевой запрос приложения — тайлы карты.

Приложение не определяет виды грибов и никогда не говорит, съедобна ли находка.

---

## What to Test — en-US

Thank you for testing. Three things matter most:

1. **Background and foreground during an active walk.** Start a walk, open the map screen,
   then switch to another app and come back — several times in a row. Lock the screen and
   unlock it. This is the scenario that has frozen the app on an older iPhone, and the point
   of this round is to find out whether it happens on current devices at all.

2. **A long walk with the screen locked.** Start a walk, put the phone in your pocket for
   half an hour or more, then come back and check that the track is continuous and the finds
   are all there.

3. **Everyday use:** add finds with "+", attach photos, finish the walk, look at the archive
   and at the map of all finds.

**In any report, please include the phone model and the iOS version** (Settings → General →
About). Without the model a report cannot be told apart from a problem that only affects
older hardware.

Write to fefradkin@gmail.com or use the Send Beta Feedback button in TestFlight. Screenshots
help; the exact time of day the problem happened helps even more.

The app does not identify mushrooms and never tells you whether a find is edible — that is
deliberate, not a missing feature.

## What to Test — ru-RU

Спасибо, что тестируете. Важнее всего три вещи:

1. **Сворачивание и разворачивание на активной прогулке.** Начните прогулку, откройте экран
   карты, переключитесь на другое приложение и вернитесь — несколько раз подряд.
   Заблокируйте и разблокируйте экран. Именно этот сценарий подвешивал приложение на старом
   iPhone, и смысл этого круга — выяснить, случается ли такое на современных телефонах
   вообще.

2. **Долгая прогулка с заблокированным экраном.** Начните запись, уберите телефон в карман
   на полчаса и дольше, потом проверьте, что трек не разорван и все находки на месте.

3. **Обычное использование:** отмечайте находки кнопкой «+», прикладывайте фотографии,
   завершите прогулку, посмотрите архив и карту всех находок.

**В любом сообщении указывайте, пожалуйста, модель телефона и версию iOS** (Настройки →
Основные → Об этом устройстве). Без модели отчёт невозможно отличить от проблемы, которая
касается только старого железа.

Пишите на fefradkin@gmail.com или через кнопку отправки отзыва в TestFlight. Скриншоты
помогают; точное время, когда это случилось, помогает ещё больше.

Приложение не определяет виды грибов и никогда не говорит, съедобна ли находка, — это
сознательное решение, а не недоделка.
