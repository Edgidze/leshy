# i18n/ — своя локализация, не Compose Resources

**`Res.string` не используется** — в `components-resources-1.11.1`
`ResourceEnvironment`/`LocalComposeEnvironment`/`LanguageQualifier`
`internal`/`@InternalResourceApi`, переопределить локаль в рантайме из кода
приложения невозможно. Свой слой:

- `StringKey` — плоский `enum` всех строк.
- `Strings.kt` — `russianStrings(key)`/`englishStrings(key)`, exhaustive
  `when` по `StringKey` (компилятор ловит забытый перевод при добавлении
  нового ключа — **не обходить** `else ->`, смысл конструкции именно в
  принудительной полноте).
- `LocalAppLanguage` (`CompositionLocal`) + `@Composable fun stringResource
  (key)`; для не-composable контекста (например, `RecordViewModel.start()`) —
  чистая функция `string(key, language)`.
- Текущий язык — в DataStore (см. `data/CLAUDE.md`), прокидывается в `App()`
  через `CompositionLocalProvider` — переключение в «Настройках» применяется
  мгновенно во всём приложении без перезапуска.
- `categoryDisplayName(nameKey)` — отдельный хелпер для имён категорий
  (`Category.nameKey` резолвится тем же механизмом).

Добавление новой строки: новый `StringKey` + обе ветки `when` в
`Strings.kt`. Никаких хардкод-строк в UI.
