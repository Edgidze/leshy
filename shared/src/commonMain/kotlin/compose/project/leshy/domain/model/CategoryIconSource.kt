package compose.project.leshy.domain.model

/**
 * Where a species' illustration comes from — the one thing every icon-rendering call site has to
 * branch on since user-created species arrived (see `.claude/plans/user-mushrooms.md`). Lives in
 * the domain rather than next to the `CategoryIcon` composable because the map's marker layers and
 * `SettingsViewModel` need the same distinction outside any UI-drawing context.
 */
sealed interface CategoryIconSource {
    /** Stable, collision-free token for this icon: groups map markers per species and forms the
     * MapLibre layer/source ids built from them. */
    val key: String

    /** Bundled catalog illustration, resolved through `Res.allDrawableResources[iconRef]`. */
    data class Bundled(val iconRef: String) : CategoryIconSource {
        override val key: String get() = iconRef
    }

    /** The user's own illustration — a *file name* inside this app's photo storage, resolved to an
     * absolute path at read time via `PhotoStorage.resolvePath` (see [Category.iconFile]). */
    data class UserFile(val fileName: String) : CategoryIconSource {
        override val key: String get() = "file_$fileName"
    }
}

/** This species' illustration, or null when it has none (the service `category_misc` row, and
 * user-created species until an image is attached). [Category.iconFile] wins if somehow both are
 * set: an illustration the user picked themselves is never overridden by a catalog one. */
fun Category.iconSource(): CategoryIconSource? = when {
    iconFile != null -> CategoryIconSource.UserFile(iconFile)
    iconRef != null -> CategoryIconSource.Bundled(iconRef)
    else -> null
}
