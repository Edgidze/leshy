package leshy.mushrooms.map.data.platform

import leshy.mushrooms.map.domain.model.Category
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.allDrawableResources
import okio.FileSystem
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.getSystemResourceEnvironment

/**
 * Raw bytes of [category]'s own illustration — a user-created species' PNG from disk
 * ([Category.iconFile], via [PhotoStorage]), or a catalog species' bundled `.webp`
 * ([Category.iconRef], via Compose Resources' non-composable byte-reading API). Used by the two
 * `WalkThumbnailRenderer` `writeAnnotated` implementations to bake a real species icon onto a
 * native `Canvas`/`CGContext` map snapshot, where no `@Composable`/`Painter` machinery is
 * reachable — see `ui/map/MushroomMarkerIcon.kt` for the (unrelated, Compose-only) live-map
 * equivalent this deliberately doesn't reuse.
 *
 * `getSystemResourceEnvironment()` is documented as an expensive call — callers resolving several
 * categories in one pass should call it once and pass an environment in, not call this per marker
 * in a loop. Returns null on any failure (missing file, unknown `iconRef`, decode error at the
 * resource layer) — callers must fall back gracefully, not crash.
 */
suspend fun resolveCategoryIconBytes(category: Category, photoStorage: PhotoStorage): ByteArray? = when {
    category.iconFile != null ->
        runCatching { FileSystem.SYSTEM.read(photoStorage.resolvePath(category.iconFile).toPath()) { readByteArray() } }
            .getOrNull()
    category.iconRef != null ->
        Res.allDrawableResources[category.iconRef]?.let { resource ->
            runCatching { getDrawableResourceBytes(getSystemResourceEnvironment(), resource) }.getOrNull()
        }
    else -> null
}
