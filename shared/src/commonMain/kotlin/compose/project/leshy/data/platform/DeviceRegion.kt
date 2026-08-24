package compose.project.leshy.data.platform

/** ISO 3166-1 alpha-2 code of the device's current region setting (Android: `Locale`, iOS:
 * `NSLocale`), or `null` when the platform can't report one. Used only to pre-select a matching
 * country collection on first launch (`OnboardingViewModel`,
 * `.claude/plans/countries-and-languages.md`, Phase 3) — not validated against `countries.json`
 * here, the caller treats "no matching collection" the same as "no region". */
expect fun currentDeviceRegionCode(): String?
