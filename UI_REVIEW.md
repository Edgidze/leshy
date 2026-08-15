# UI/UX Review — «Леший: карта грибов»

> Review from 2026-08-14, based on live Android screenshots (Medium_Phone emulator).
> Prioritized against the core value: **the app's main worth is the history of finds**
> (where / when / what / how much), Strava-style. Work through top to bottom.
>
> Screenshot references (`NN_*`) correspond to the captured set:
> 01 record · 02 record active · 03 live map · 04 drawer · 05 archive list ·
> 06 walk detail · 07 aggregated map · 08 statistics · 09 settings · 10 help dialog.

---

## Tier 1 — Biggest misses (these fight the core value prop)

- [ ] **1. Archive list shows no date.** The whole premise is *history*, yet the archive
      card (`05`) shows name, distance, duration, count — but not *when* the walk happened.
      Date is the most important field for scanning/recalling a trip and currently only
      appears after tapping into detail. Add it as the second line of every card.

- [ ] **2. Walk-detail screen renders finds as plain text, not photos.** Detail (`06`)
      and stats (`08`) list finds as label/value rows (`Белый гриб — 3`). You have 30
      mushroom photos in the app; this screen is the emotional payoff of a walk. Show the
      finds *as their photos* (with counts) instead of a debug-style list.

- [ ] **3. No route preview in the archive.** Strava cards live or die on the little
      route thumbnail. The archive card is text-only. Add a mini static map (route +
      find dots) to each card — the same map is already rendered elsewhere.

## Tier 2 — Cross-cutting problems

- [ ] **4. Units aren't localized, and one is imperial.** Everywhere shows `0.05 km` in
      a Russian UI (should be `км`). The map scale bar (`07`) reads **`25 ft / 50 ft /
      75 ft`** — feet, on a Russian app. Set the MapLibre scale control to metric and
      localize the distance unit string.

- [x] **5. The theme fights the brand.** Default Material3 lavender/pale-purple surface
      reads "productivity app," not "into the forest." The logo is dark forest-green and
      the best accents (Старт button, switches) are already green. Retheme surfaces/
      background to an earthy green-brown palette anchored on the logo color. Cheap,
      high-impact.

- [ ] **6. Doubled headers on nested screens waste ~15% vertical space.** Live map (`03`)
      and walk detail (`06`) stack the global "Леший" app bar on top of a second header
      (with its own back arrow). The global app bar earns its place on the four top-level
      screens but is dead weight on nested ones — hide it there and let the inner header
      stand alone.

- [ ] **7. Home-glyph "menu" button is a false affordance.** The 🏠 top-left opens the
      drawer, but a house icon conventionally means "go home/record." A hamburger (☰) is
      the universal drawer signal. (Note: current glyph was a deliberate past choice —
      revisit only if desired.)

## Tier 3 — Record screen (the fast-logging loop)

- [ ] **8. Tiles are large; finding 1 of 30 species mid-forage is slow.** Only ~2.5 rows
      are visible (`01`); reaching a specific mushroom means a long scroll — exactly when
      you're crouched over a find wanting to log in two seconds. Options: search/filter
      bar, "recently/most-found floats to top," or a compact-layout toggle.

- [ ] **9. Edibility dot has no legend.** The green/yellow/red circle (top-right of each
      tile) is opaque to a first-timer. Add a one-line legend (or put it in a real help
      screen).

## Tier 4 — Smaller things

- [ ] **10. Help dialog ships a visible "?" that admits it does nothing** ("appears in a
      future update", `10`). A button announcing its own emptiness feels more unfinished
      than no button. Hide it until real, or add minimal per-screen text.

- [ ] **11. Settings mushroom list:** 30 switches, no search/grouping, and — inconsistent
      with the photo-rich Record screen — no thumbnails (`09`). Add small photos beside
      each switch; consider grouping by edibility or a select-all/none.

- [ ] **12. Walk-detail row inconsistency:** `Километраж: 0.05 km` is labeled, but the
      duration `04:59` on the same row has no label.

- [ ] **13. Stats screen** is a candidate for a tiny finds-by-type bar chart — you have
      the data, and it'd add visual interest to an otherwise flat list.

---

## What's genuinely good (don't regress these)

- Mushroom tile design (photo + species-keyed colored border + edibility indicator) is
  distinctive and appealing.
- Live map with the photo-marker overlapping the location dot (`03`) is lovely.
- Primary/secondary button hierarchy (green Старт vs outlined Смотреть карту) is correct.
- Drawer with "Выберите раздел" header and per-item icons is clean.
- Empty state ("Прогулок пока нет") is handled.

---

## Suggested attack order

The Tier-1 cluster plus units + retheme move the app closest to the "history is the value"
target:

1. Metric units everywhere incl. scale bar (#4) — small, isolated, unblocks nothing but
   quick confidence.
2. Archive dates (#1) — small, high value.
3. Photo-based finds on detail screen (#2).
4. Route thumbnails on archive cards (#3).
5. Forest-green retheme (#5).

…then Tier 2 remainder, Tier 3, Tier 4.
