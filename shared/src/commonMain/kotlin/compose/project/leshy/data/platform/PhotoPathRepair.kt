package compose.project.leshy.data.platform

/**
 * Attempts to recover [storedPath] — a `photoPath`/`thumbnailPath` whose file no longer exists at
 * that exact location — by re-deriving it against this platform's *current* storage root and
 * checking whether a file actually exists there. Returns the corrected absolute path on success,
 * or `null` if nothing can be recovered (see `iosMain/CLAUDE.md` for why a stored absolute path
 * can go stale in the first place).
 */
expect fun repairStalePhotoPath(storedPath: String): String?
