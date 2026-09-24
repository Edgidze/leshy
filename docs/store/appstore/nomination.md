# Featuring Nomination — App Launch

Заявка редакции App Store на рассмотрение приложения для подборок.
Путь в консоли: боковая панель → **Featuring → Nominations → «+» → Create Nomination**.

Заявка **необязательна** и на ревью никак не влияет: подавать после отправки версии, не
задерживая её. Поля и лимиты — по справке Apple («Nominations template»), проверены 2026-09-24.

**Текст в блоках ниже намеренно НЕ переносится по строкам.** Поля в консоли однострочные, и
перенос из markdown копируется в форму как жёсткий разрыв — приходится вычищать руками.
Абзацы в описании разделены пустой строкой, это единственные разрывы, которые нужны.

**Честная оговорка про сроки.** Apple рекомендует подавать заявку **не позже чем за три недели**
до запуска. У нас этого запаса нет — релиз готовится сейчас, потому что заканчивается грибной
сезон Северного полушария. Поэтому в заявке указан диапазон дат, а описание построено так, чтобы
приложение оставалось интересным редакции и вне сезонного повода.

**Два решения по формулировкам (2026-09-24).**

- **Пример с именами — английский, а не русский.** Сначала стояли «обабок» и «красный»; владелец
  справедливо заметил, что редактору из другой страны они ничего не говорят. Заменены на
  `porcini / cep / penny bun` — это реальные синонимы `boletus_edulis` из
  `composeResources/files/catalog/aliases/en.json` (основное имя там `Porcini`), то есть пример
  проверяем по данным приложения и понятен без перевода.
- **Сравнение со спортивными трекерами — без имён.** Идея владельца: у бегунов такой дневник есть,
  а у грибников нет. Довод сильный, но конкретное приложение не названо намеренно — фраза работает
  и без бренда, а упоминание конкурента в тексте, уходящем в Apple, лишний риск без выигрыша.
- **В `Helpful Details` нет дисклеймера про съедобность, и это сознательно.** Он там был и убран по
  замечанию владельца: поле спрашивает, чем приложение выделяется, а перечисление того, чего оно не
  делает, тратит место на недостатки, которые читатель и сам найдёт. Дисклеймер при этом никуда не
  делся — он стоит там, где действительно нужен: в `review-notes.md`, которые читает App Review, в
  описании магазина и в самом приложении. Заявка уходит редакции, а не проверяющим.

**Чего в тексте намеренно нет.** Первая версия утверждала, что языки, подборки и иллюстрации
сделаны вручную одним человеком. Это неправда — работа шла с помощью ИИ, и часть результата
владелец не может проверить лично. Заявление убрано целиком, а не смягчено: Apple читает такие
тексты как заявления о факте. Вместо него — то, что проверяемо и при этом сильнее: каталог
устроен по странам и народным именам, а не по таксономии.

---

## Nomination Name (49/60)

```
Mushroom Map from Leshy — App Launch, autumn 2026
```

## Nomination Type

```
App Launch
```

## Nomination Description (986/1000)

```
An offline field notebook for mushroom foraging. It records the walk as you walk it: the GPS track, every find with its species, coordinates and time, and the landmarks worth going back to. By next season the map knows where to go.

Built for a place with no reception. Maps are downloaded in advance, every find is written to storage the instant it is tapped, and a walk survives a flat battery or an app killed mid-forest. Finds can be logged from the notification shade, phone still locked.

No accounts, no ads, no analytics, no trackers. Nothing leaves the device — not to the developer, not to anyone.

The catalogue holds 408 species in 55 collections by country — what people there actually gather, not a taxonomy. Species appear under their local common names in 42 languages, and search knows the informal names foragers use, not just the field-guide ones. Whatever the catalogue lacks, you add yourself: your own name, colour and photo, cropped and cleaned up inside the app.
```

## Publish Date (Start)

```
2026-09-26
```

## Publish Date (End)

```
2026-10-17
```

Диапазон, а не дата: выпуск стоит на «Automatically release after App Review», то есть момент
публикации определяется длительностью ревью, а не нами.

## Relevant Countries or Regions

**Оставлять «All».** Поле про то, где заявка релевантна; приложение доступно везде, а грибники
есть и за пределами тех 55 стран, для которых собраны подборки видов. Сужать охват заявки незачем.

## Do you plan to launch in certain markets first?

```
No
```

## Do you intend to submit a new In-App Event for this nomination?

```
No
```

## Platforms

```
iOS (iPhone)
```

## Localization

Все 42 языка интерфейса (`domain/model/AppLanguage.kt`):

```
az,be,bg,bs,cs,da,de,el,en,es,et,fi,fr,hr,hu,hy,is,it,ja,ka,kk,ko,ky,lt,lv,mk,nb,nl,pl,pt,ro,ru,sk,sl,sq,sr,sv,tg,tk,tr,uk,uz
```

## Supplemental Materials

```
https://leshy-mapper.github.io/mushrooms-map/privacy.html,https://leshy-mapper.github.io/mushrooms-map/support.html
```

## Does this app or game include a pre-order?

```
No
```

## Helpful Details (508/500)

```
Runners and cyclists have had a proper logbook for years. People who go to the forest have had a paper notebook and their memory. That gap is the app. Logging a find is one tap on its tile: species, place and time saved on the spot, and it works from the notification shade with the phone locked. All 408 species come with their own illustration, and the catalogue is organised by country — what people gather there, not a taxonomy — under the names foragers use: porcini, cep and penny bun are one mushroom.
```
