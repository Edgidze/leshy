package compose.project.leshy.data.platform

import compose.project.leshy.domain.model.GeoPoint

/**
 * Renders a small static map snapshot (real tiles + route + find dots) for a finished walk,
 * once, off-screen — not a live map view. Implementations resolve their own on-disk cache
 * location (mirrors how [rememberCameraLauncher] resolves its own "photos" directory rather than
 * taking one from the caller, since only platform code has a [android.content.Context] /
 * `NSFileManager` to resolve it from).
 *
 * Returns the absolute path to the written PNG on success, or `null` on any failure (no network,
 * snapshot timeout, degenerate track, etc.) — callers must fail gracefully, not crash or block.
 */
interface WalkThumbnailRenderer {
    suspend fun render(walkId: Long, track: List<GeoPoint>, findLocations: List<GeoPoint>): String?
}
