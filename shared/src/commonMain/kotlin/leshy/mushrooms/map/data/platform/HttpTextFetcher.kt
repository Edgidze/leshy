package leshy.mushrooms.map.data.platform

/**
 * Plain HTTPS GET-and-read-body-as-text, for [leshy.mushrooms.map.data.repository.
 * MapStyleCacheRepository]'s one-off `style.json` fetch. Deliberately not a KMP HTTP client
 * library (e.g. Ktor) — this project's `minSdk = 24` is incompatible with `ktor-client-core`
 * 3.2.0's Android artifact (it contains a method named `` `use streaming syntax` ``, and DEX
 * versions below 040 reject space characters in names — see `ui/map/CLAUDE.md`). Both platforms
 * already have native capability for a single GET request with zero extra dependencies (Android:
 * `HttpURLConnection`; iOS: `NSURLSession`), so `expect`/`actual` is the right call here despite
 * the project's usual cross-platform-library-first rule — that rule's whole premise (a ready
 * library exists and fits) doesn't hold for this specific dependency.
 */
interface HttpTextFetcher {
    suspend fun fetchText(url: String): String
}
