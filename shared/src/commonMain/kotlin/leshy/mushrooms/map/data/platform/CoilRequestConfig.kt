package leshy.mushrooms.map.data.platform

import coil3.request.ImageRequest

/**
 * Disables Android hardware bitmaps (GPU-only textures — Coil3's default on Android whenever
 * available) for this request. MapLibre bakes marker
 * [Painter][androidx.compose.ui.graphics.painter.Painter]s onto a *software*
 * `Canvas`/`ImageBitmap` (`ImageManager.drawToBitmap`, `maplibre-compose`) — drawing a hardware
 * bitmap onto a software canvas throws `IllegalArgumentException: Software rendering doesn't
 * support hardware bitmaps`, crashing the app the instant a place with a photo is saved (confirmed
 * via on-device logcat). No-op on iOS, where Coil3 has no hardware-bitmap concept.
 */
expect fun ImageRequest.Builder.disallowHardwareBitmaps(): ImageRequest.Builder
