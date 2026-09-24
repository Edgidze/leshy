# Частотность грибов: Мексика (MX)

Зона партии: **Мексика**. Одна сессия — одна страна.

## Что это за задача

«Леший: карта грибов» — мобильное приложение для тихой охоты: трек прогулки, отметки находок,
архив, карта находок. У каждой страны в приложении своя подборка из полусотни видов — это плитки
на экране записи находки, по которым человек в лесу одним нажатием отмечает, что нашёл.

**Подборка Мексики уже собрана, и менять её состав не нужно.** Нужно одно: отметить, какие
из этих пятидесяти видов в стране **частотные**, чтобы приложение ставило их в начало ленты, а не
сортировало полсотни плиток по алфавиту.

Почему спрашиваем именно вас: у Мексики этот признак сейчас выведен из косвенных
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

Мексика — страна с одной из самых богатых в мире живых этномикологических традиций, и сбор здесь
не хобби, а **часть сельской экономики и кухни**: сезон дождей (июнь—сентябрь) в горных
сосново-дубовых лесах центральной Мексики, Мичоакана, Оахаки, Пуэблы, Тласкалы; грибные рынки —
Озумба, Толука, Акахете, рынки Оахаки; праздники и ярмарки грибов (ferias del hongo).

Набор сильно отличается от европейского: *Amanita basii* (yema, «желток») — один из самых ценимых
видов страны, *Lactarius indigo* (azul), *Russula brevipes* с *Hypomyces lactifluorum* (trompa de
puerco / hongo langosta), *Turbinellus floccosus* (corneta), рамарии (escobetas, patitas), *Ustilago
maydis* (huitlacoche) — кукурузная головня, которую в Мексике едят и продают повсеместно.

**Ловушки именно этой страны:**

- **Список съедобных ≠ частотность.** Мексиканские работы перечисляют сотни съедобных видов;
  вопрос задачи — какие из пятидесяти в подборке человек реально встречает в обычный выход;
- **регион имеет значение**: набор центральной Мексики (Estado de México, Tlaxcala, Puebla)
  отличается от оахакского и от северного. Частотным считается то, что обычно там, где сбор массовый;
- **названия на языках коренных народов есть у 10–17 позиций из 50** — это нормально и на отбор
  не влияет;
- сезон дождей — июнь—сентябрь, пик августа.

## Подборка Мексики — все 50 позиций

Названия даны на языке подборки: испанский (`es`) и языки коренных народов: науатль (`nah`), пурепеча (`tsz`), масатекский (`maa`), уичоль (`hch`), цоциль (`tzo`). Колонка «знач.» — оценка значимости вида в
каталоге приложения (2–5), она общая для всех стран и **не является ответом на вопрос задачи**.
Знак ⚠ — вид, который приложение в любом случае показывает в конце ленты (ядовитый или требующий
осторожности); отмечать его частотным можно, если он в стране действительно массовый.

| № | key | латынь | название | знач. |
|---|---|---|---|---|
| 1 | `amanita_basii` | *Amanita basii* | Amarillo | 5 |
| 2 | `amanita_novinupta` | *Amanita novinupta* | Dieguito | 4 |
| 3 | `amanita_fulva` | *Amanita fulva* | Señoritas | 3 |
| 4 | `amanita_muscaria` | *Amanita muscaria* | Mata moscas | 5 ⚠ |
| 5 | `amanita_bisporigera` | *Amanita bisporigera* | — | 5 ⚠ |
| 6 | `amanita_pantherina` | *Amanita pantherina* | Amanita pantera | 5 ⚠ |
| 7 | `amanita_arocheae` | *Amanita arocheae* | — | 4 ⚠ |
| 8 | `boletus_edulis` | *Boletus edulis* | Boleto comestible | 5 |
| 9 | `neoboletus_luridiformis__2` | *Neoboletus luridiformis* | Galambos | 3 |
| 10 | `suillus_luteus__2` | *Suillus luteus* | Viejitas | 5 |
| 11 | `cantharellus_cibarius` | *Cantharellus cibarius* | Flores | 5 |
| 12 | `turbinellus_floccosus` | *Turbinellus floccosus* | Corneta | 4 |
| 13 | `turbinellus_kauffmanii` | *Turbinellus kauffmanii* | Corneta blanca | 3 |
| 14 | `clavariadelphus_truncatus` | *Clavariadelphus truncatus* | Clarín | 3 |
| 15 | `ramaria_flava` | *Ramaria flava* | Escobeta | 5 ⚠ |
| 16 | `helvella_lacunosa` | *Helvella lacunosa* | Gachupín | 5 |
| 17 | `morchella_esculenta` | *Morchella esculenta* | Colmenilla | 5 |
| 18 | `russula_brevipes` | *Russula brevipes* | Blanco | 5 |
| 19 | `lactarius_indigo` | *Lactarius indigo* | Añil | 5 |
| 20 | `lactarius_deliciosus` | *Lactarius deliciosus* | Chilpan | 5 |
| 21 | `hypomyces_lactifluorum` | *Hypomyces lactifluorum* | Barroso | 5 |
| 22 | `lyophyllum_decastes` | *Lyophyllum decastes* | Cholete | 5 |
| 23 | `hebeloma_aminophilum` | *Hebeloma aminophilum* | Cholete de ocote | 5 ⚠ |
| 24 | `clitocybe_gibba` | *Clitocybe gibba* | Oreja | 4 |
| 25 | `laccaria_laccata` | *Laccaria laccata* | Chocuyul | 4 |
| 26 | `gymnopus_dryophilus` | *Gymnopus dryophilus* | Gringa | 3 |
| 27 | `hygrophorus_chrysodon` | *Hygrophorus chrysodon* | Nixtamal | 3 |
| 28 | `hygrophorus_gliocyclus` | *Hygrophorus gliocyclus* | Dulce | 3 |
| 29 | `agaricus_campestris` | *Agaricus campestris* | Hongo de tierra | 5 ⚠ |
| 30 | `agaricus_silvicola` | *Agaricus silvicola* | Champiñón de monte | 3 |
| 31 | `calvatia_cyathiformis` | *Calvatia cyathiformis* | Bolitas de llano | 4 |
| 32 | `macrolepiota_procera` | *Macrolepiota procera* | Jongo de culebra | 5 |
| 33 | `ustilago_maydis` | *Ustilago maydis* | Cuitlacoche | 5 |
| 34 | `pleurotus_djamor` | *Pleurotus djamor* | Orejas de ochote | 5 |
| 35 | `pleurotus_opuntiae` | *Pleurotus opuntiae* | Oreja de nopal | 4 |
| 36 | `pleurotus_albidus` | *Pleurotus albidus* | Hongo blanco de pata | 3 |
| 37 | `auricularia_americana` | *Auricularia americana* | Oreja de palo | 4 |
| 38 | `schizophyllum_commune` | *Schizophyllum commune* | — | 4 |
| 39 | `laetiporus_sulphureus` | *Laetiporus sulphureus* | — | 5 |
| 40 | `hydnopolyporus_fimbriatus` | *Hydnopolyporus fimbriatus* | — | 3 |
| 41 | `lentinus_crinitus` | *Lentinus crinitus* | — | 3 |
| 42 | `lentinus_sp` | *Lentinus sp.* | — | 3 |
| 43 | `volvariella_bombycina` | *Volvariella bombycina* | Hongo de ochote | 5 |
| 44 | `marasmius_oreades` | *Marasmius oreades* | Corralitos | 5 |
| 45 | `neolentinus_lepideus` | *Neolentinus lepideus* | — | 3 |
| 46 | `ganoderma_oerstedii` | *Ganoderma oerstedii* | Oreja de pino | 3 |
| 47 | `psilocybe_caerulescens` | *Psilocybe caerulescens* | Derrumbe | 4 |
| 48 | `psilocybe_mexicana` | *Psilocybe mexicana* | Pajarito | 4 |
| 49 | `psilocybe_cubensis` | *Psilocybe cubensis* | San Isidro | 4 |
| 50 | `armillaria_mellea` | *Armillaria mellea* | Montoncitos | 5 |

## Где искать

- испаноязычные материалы Мексики: «hongos silvestres comestibles México», «temporada de hongos
  Ozumba/Toluca/Oaxaca», «feria del hongo», «micología tradicional mexicana»;
- этномикологические работы (Garibay-Orijel, Estrada-Torres, Guzmán) — именно они отличают
  «растёт» от «собирают и продают»;
- материалы рынков и муниципалитетов о сезоне и ценах — прямое свидетельство массовости;
- CONABIO, региональные списки съедобных грибов, отчёты об отравлениях (для «встречает, но не берёт»).

Порядок доверия: национальные и региональные работы о сборе → свидетельства сбора (рынки, кухня,
отчёты, форумы, видео) → базы наблюдений (GBIF, iNaturalist) как сигнал встречаемости → общие
определители только для сверки латыни.

## Формат ответа

Сначала — JSON-файл `common_MX.json` ровно такой структуры:

```json
{
  "country": "MX",
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
