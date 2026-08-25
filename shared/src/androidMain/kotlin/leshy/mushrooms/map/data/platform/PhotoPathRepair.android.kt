package leshy.mushrooms.map.data.platform

// Android's storage root (Context.filesDir) doesn't relocate across in-place updates the way
// iOS's sandbox container can (see the iOS actual) — if a stored path is missing here, the file
// is genuinely gone, not just moved, so there is nothing to repair.
actual fun repairStalePhotoPath(storedPath: String): String? = null
