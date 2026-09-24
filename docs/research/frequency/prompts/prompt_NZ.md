# Частотность грибов: Новая Зеландия (NZ)

Зона партии: **Австралия и Новая Зеландия**. Одна сессия — одна страна.

## Что это за задача

«Леший: карта грибов» — мобильное приложение для тихой охоты: трек прогулки, отметки находок,
архив, карта находок. У каждой страны в приложении своя подборка из полусотни видов — это плитки
на экране записи находки, по которым человек в лесу одним нажатием отмечает, что нашёл.

**Подборка Новой Зеландии уже собрана, и менять её состав не нужно.** Нужно одно: отметить, какие
из этих пятидесяти видов в стране **частотные**, чтобы приложение ставило их в начало ленты, а не
сортировало полсотни плиток по алфавиту.

Почему спрашиваем именно вас: у Новой Зеландии этот признак сейчас выведен из косвенных
данных и помечен как ненадёжный. У большинства других стран он взят из исследований с источниками.

## Критерий: что считается частотным

**«Грибник этой страны встретит и отметит это в обычный сезонный выход, не охотясь специально»** —
формулировка владельца приложения, и она узкая намеренно.

- **да** — то, что попадается регулярно и в обычных местах: массовый вид, привычная добыча,
  то, что человек назовёт, вспоминая прошлый сезон;
- **да** — заметный и легко узнаваемый вид, который в этой стране **встречают и отмечают**, даже
  если не едят: в странах с натуралистической, а не заготовительной культурой сбора это половина
  того, что человек фиксирует за прогулку;
- **нет** — вид, за которым едут специально в известное место (трофейные и дорогие виды, подземные
  грибы), даже если он культово важен;
- **нет** — редкий, локальный, приуроченный к одной горной системе или к одному типу леса,
  который есть не везде;
- **нет** — вид, попавший в подборку только как опасный двойник, которого надо знать в лицо, если
  сам он при этом не массовый.

**Отрицательный ответ — полноценный результат.** Если культово известный вид в стране на самом деле
редок, так и напиши: это ровно та поправка, ради которой исследование и проводится.

## Сколько позиций

**Не больше 20. Ориентир — 10–20 из пятидесяти**, то есть верхние два-три ряда плиток на экране.
Меньше — нормально и часто честнее; больше двадцати не примут: если частотна половина подборки,
сортировка перестаёт что-либо значить.

## Про страну

В Новой Зеландии две традиции, и обе живые. Первая — **сбор в посадках интродуцированной
сосны и под экзотическими деревьями**: рыжик, маслёнок, подберёзовик под берёзой, луговой шампиньон
на пастбищах. Вторая — **маорийская**: hakeke (*Auricularia cornea*), harore (*Armillaria
novae-zelandiae*), tawaka (*Cyclocybe parasitica*), pekepeke-kiore (*Hericium novae-zealandiae*);
hakeke в XIX веке был предметом экспорта в Китай, то есть собирался промышленно.

Нативные леса (подокарповые, южный бук) дают массу видов, которые замечают и определяют, но не
едят, — как и в Австралии, поэтому критерий ниже про «встретит и отметит».

**Ловушки именно этой страны:**

- **Названия на маори (`mi`) есть только у 10 позиций из 50** — это нормально и не влияет на отбор:
  частотность определяется по факту, а не по наличию названия;
- **не смешивай нативные и экзотические стации**: рыжик и маслёнок бывают только под соснами,
  harore и tawaka — в нативном лесу и на садовых пнях;
- сезон осенний, март—июнь.

## Подборка Новой Зеландии — все 50 позиций

Названия даны на языке подборки: английский (`en`) и маори (`mi`). Колонка «знач.» — оценка значимости вида в
каталоге приложения (2–5), она общая для всех стран и **не является ответом на вопрос задачи**.
Знак ⚠ — вид, который приложение в любом случае показывает в конце ленты (ядовитый или требующий
осторожности); отмечать его частотным можно, если он в стране действительно массовый.

| № | key | латынь | название | знач. |
|---|---|---|---|---|
| 1 | `cyclocybe_parasitica` | *Cyclocybe parasitica* | Tawaka | 5 |
| 2 | `auricularia_cornea` | *Auricularia cornea* | Wood ear | 5 |
| 3 | `agaricus_subrufescens` | *Agaricus subrufescens* | Field mushrooms | 5 |
| 4 | `boletus_edulis` | *Boletus edulis* | Porcini | 5 |
| 5 | `lactarius_deliciosus` | *Lactarius deliciosus* | Saffron milk cap | 5 |
| 6 | `suillus_luteus` | *Suillus luteus* | Slippery Jack | 5 |
| 7 | `leccinum_scabrum` | *Leccinum scabrum* | Birch bolete | 5 |
| 8 | `coprinus_comatus` | *Coprinus comatus* | Shaggy mane | 5 |
| 9 | `calvatia_gigantea` | *Calvatia gigantea* | Giant puffball | 5 |
| 10 | `hericium_novae_zealandiae` | *Hericium novae-zealandiae* | New Zealand lion’s mane | 5 |
| 11 | `clitocybe_nuda` | *Clitocybe nuda* | Wood blewit | 4 |
| 12 | `morchella_esculenta` | *Morchella esculenta* | Morel | 5 |
| 13 | `pleurotus_pulmonarius` | *Pleurotus pulmonarius* | Native phoenix oyster | 4 |
| 14 | `pleurotus_parsonsiae` | *Pleurotus parsonsiae* | Velvet oyster | 4 |
| 15 | `pleurotus_australis` | *Pleurotus australis* | Brown oyster | 4 |
| 16 | `lentinula_novae_zelandiae` | *Lentinula novae-zelandiae* | Native shiitake | 4 |
| 17 | `stropharia_rugosoannulata` | *Stropharia rugosoannulata* | Wine cap | 3 |
| 18 | `flammulina_velutipes` | *Flammulina velutipes* | Velvet shank | 5 |
| 19 | `marasmius_oreades` | *Marasmius oreades* | Fairy ring mushroom | 5 |
| 20 | `trametes_versicolor` | *Trametes versicolor* | Turkey tail | 4 |
| 21 | `chlorophyllum_rhacodes` | *Chlorophyllum rhacodes* | Shaggy parasol | 4 ⚠ |
| 22 | `armillaria_novae_zelandiae` | *Armillaria novae-zelandiae* | Austral honey mushroom | 4 |
| 23 | `entoloma_hochstetteri` | *Entoloma hochstetteri* | Blue pinkgill | 5 |
| 24 | `ileodictyon_cibarium` | *Ileodictyon cibarium* | Basket fungus | 5 |
| 25 | `aseroe_rubra` | *Aseroe rubra* | Anemone stinkhorn | 4 |
| 26 | `clavogaster_virescens` | *Clavogaster virescens* | Blue pouch fungus | 3 |
| 27 | `leratiomyces_erythrocephalus` | *Leratiomyces erythrocephalus* | Scarlet pouch | 3 |
| 28 | `cortinarius_porphyroideus` | *Cortinarius porphyroideus* | Purple pouch fungus | 4 |
| 29 | `entoloma_canoconicum` | *Entoloma canoconicum* | Grey pinkgill | 3 |
| 30 | `clavulinopsis_sulcata` | *Clavulinopsis sulcata* | Flame fungus | 3 |
| 31 | `crucibulum_simile` | *Crucibulum simile* | Common bird’s nest fungus | 3 |
| 32 | `nidula_niveotomentosa` | *Nidula niveotomentosa* | Woolly bird’s nest fungus | 3 |
| 33 | `fomitiporia_robusta` | *Fomitiporia robusta* | Robust bracket | 2 |
| 34 | `ganoderma_tsugae` | *Ganoderma tsugae* | Artist’s bracket | 4 |
| 35 | `rossbeevera_pachydermis` | *Rossbeevera pachydermis* | Potato fungus | 3 |
| 36 | `phellodon_sinclairii` | *Phellodon sinclairii* | Black tooth | 3 |
| 37 | `anthracophyllum_archeri` | *Anthracophyllum archeri* | Orange fan | 4 |
| 38 | `anthurus_archeri` | *Anthurus archeri* | Octopus stinkhorn | 4 |
| 39 | `favolaschia_claudopus` | *Favolaschia claudopus* | Orange pore fungus | 4 |
| 40 | `geastrum_fimbriatum` | *Geastrum fimbriatum* | Earthstar | 3 |
| 41 | `trichoglossum_sp` | *Trichoglossum sp.* | Earth tongue | 3 |
| 42 | `gliophorus_sp` | *Gliophorus sp.* | Waxgills | 4 |
| 43 | `amanita_australis` | *Amanita australis* | Far south Amanita | 4 ⚠ |
| 44 | `amanita_phalloides` | *Amanita phalloides* | Death cap | 5 ⚠ |
| 45 | `amanita_muscaria` | *Amanita muscaria* | Fly agaric | 5 ⚠ |
| 46 | `paxillus_involutus` | *Paxillus involutus* | Brown roll-rim | 5 ⚠ |
| 47 | `agaricus_campestris` | *Agaricus campestris* | Yellow stainers | 5 ⚠ |
| 48 | `psilocybe_subaeruginosa` | *Psilocybe subaeruginosa* | Gold tops | 4 ⚠ |
| 49 | `volvariella_volvacea` | *Volvariella volvacea* | Paddy straw mushroom | 4 ⚠ |
| 50 | `laetiporus_portentosus` | *Laetiporus portentosus* | Bracket fungus | 4 |

## Где искать

- Manaaki Whenua — Landcare Research, New Zealand Fungarium, база NZ Organisms Register;
- маорийская этномикология: работы по traditional Māori use of fungi, материалы Te Ara
  (Энциклопедия Новой Зеландии) о hakeke/harore/tawaka;
- «foraging New Zealand mushrooms», сезонные материалы о сборе в pine plantations
  (Kaingaroa, Canterbury);
- iNaturalist NZ — сигнал встречаемости по стране.

Порядок доверия: национальные и региональные работы о сборе → свидетельства сбора (рынки, кухня,
отчёты, форумы, видео) → базы наблюдений (GBIF, iNaturalist) как сигнал встречаемости → общие
определители только для сверки латыни.

## Формат ответа

Сначала — JSON-файл `common_NZ.json` ровно такой структуры:

```json
{
  "country": "NZ",
  "method": "research-session",
  "common": ["<key>", "<key>"],
  "evidence": { "<key>": "одна строка: почему частотный + источник" },
  "not_common": { "<key>": "для видов, которые кажутся очевидными, но частотными НЕ являются — почему" },
  "sources": ["<ссылка>"]
}
```

- в `common` — **ключи из колонки `key` таблицы выше, скопированные дословно**. Выдуманный или
  переписанный ключ молча сломает импорт;
- `evidence` — по строке на каждый ключ из `common`;
- `not_common` — 3–8 позиций: те, которые при переносе чужого набора попали бы в частотные
  ошибочно. Это не менее ценная часть ответа, чем сам список;
- после JSON — короткий разбор в свободной форме: на чём держится уверенность, где источники
  слабые, чем набор этой страны отличается от набора соседей.

## Бюджет и порядок работы

Ориентир — **12–18 поисковых запросов**. Это узкая задача: состав подборки уже собран, названия уже
есть, новых видов добавлять не нужно. Сначала ищи общенациональный материал о сезоне и о том, что
собирают (отчёт, путеводитель, статья о рынке или о сезоне), и только потом добирай по отдельным
видам.

## Самопроверка перед выдачей

- [ ] все ключи в `common` дословно совпадают с колонкой `key` таблицы, дубликатов нет;
- [ ] в `common` не больше 20 позиций;
- [ ] у каждого ключа из `common` есть строка в `evidence` со ссылкой или названием источника;
- [ ] заполнен `not_common` — то, что ошибочно выглядит частотным;
- [ ] ни одна позиция не попала в список только потому, что она частотна в Европе или у соседа;
- [ ] JSON валиден и парсится.
