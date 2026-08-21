package compose.project.leshy.data.platform

import compose.project.leshy.ui.map.OPEN_FREE_MAP_STYLE_URL
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCachedURLResponse
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSString
import platform.Foundation.NSURLCacheStoragePolicy
import platform.Foundation.NSURLProtocol
import platform.Foundation.NSURLProtocolClientProtocol
import platform.Foundation.NSURLProtocolMeta
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import MapLibre.MLNNetworkConfiguration

/**
 * Installs itself into MapLibre iOS's native HTTP client on construction — must be created before
 * any `MLNMapView`/`MLNOfflineStorage` first touches the network (Koin registers this
 * `createdAtStart = true`, see `PlatformModule.ios.kt`), per `MLNNetworkConfiguration.h`'s own
 * instruction to assign `sessionConfiguration` "before instantiating any MLNMapView, or using
 * MLNOfflineStorage". See [PinnedStyleInterceptor]'s doc for why this exists at all.
 */
@OptIn(ExperimentalForeignApi::class)
class IosPinnedStyleInterceptor : PinnedStyleInterceptor {
    init {
        // Apple docs: every access to .defaultSessionConfiguration returns a fresh, independently
        // mutable configuration object, not a shared singleton — safe to mutate in place, no need
        // to copy() it first.
        val configuration = NSURLSessionConfiguration.defaultSessionConfiguration
        configuration.setProtocolClasses(
            listOf(PinnedStyleURLProtocol) + configuration.protocolClasses.orEmpty(),
        )
        MLNNetworkConfiguration.sharedManager.sessionConfiguration = configuration
    }

    override fun setPinnedStyle(json: String) {
        pinnedStyleJson = json
    }
}

// Backing state for PinnedStyleURLProtocol lives at file (companion) scope, not on an
// IosPinnedStyleInterceptor instance — NSURLProtocol subclasses are instantiated internally by the
// URL Loading System, never by our own code, so there's no instance of ours to read from.
@OptIn(ExperimentalForeignApi::class)
private var pinnedStyleJson: String? = null

@OptIn(ExperimentalForeignApi::class)
private class PinnedStyleURLProtocol : NSURLProtocol {
    // NSURLProtocol has no zero-arg initializer — the URL Loading System always constructs
    // subclasses through this designated one; nothing custom to add, just forward to super.
    @OptIn(BetaInteropApi::class)
    @Suppress("CONFLICTING_OVERLOADS")
    @OverrideInit
    constructor(
        request: NSURLRequest,
        cachedResponse: NSCachedURLResponse?,
        client: NSURLProtocolClientProtocol?,
    ) : super(request, cachedResponse, client)

    companion object : NSURLProtocolMeta() {
        override fun canInitWithRequest(request: NSURLRequest): Boolean =
            pinnedStyleJson != null && request.URL?.absoluteString == OPEN_FREE_MAP_STYLE_URL

        override fun canonicalRequestForRequest(request: NSURLRequest): NSURLRequest = request
    }

    // Short-circuit: no real network call for the style resource at all, so the native offline
    // downloader always resolves it from the exact same bytes the live map uses.
    override fun startLoading() {
        val json = pinnedStyleJson ?: return
        val url = request.URL ?: return
        @Suppress("USELESS_CAST") // kotlin.String and NSString are toll-free bridged, but the
        // dataUsingEncoding overload set only resolves against the NSString-typed receiver.
        val data = (json as NSString).dataUsingEncoding(NSUTF8StringEncoding)
        val response = NSHTTPURLResponse(
            uRL = url,
            statusCode = 200,
            HTTPVersion = "HTTP/1.1",
            headerFields = mapOf("Content-Type" to "application/json"),
        )
        client?.URLProtocol(
            this,
            didReceiveResponse = response,
            cacheStoragePolicy = NSURLCacheStoragePolicy.NSURLCacheStorageNotAllowed,
        )
        if (data != null) {
            client?.URLProtocol(this, didLoadData = data)
        }
        client?.URLProtocolDidFinishLoading(this)
    }

    override fun stopLoading() = Unit
}
