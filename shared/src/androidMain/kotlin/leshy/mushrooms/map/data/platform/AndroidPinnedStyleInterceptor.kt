package leshy.mushrooms.map.data.platform

import android.content.Context
import leshy.mushrooms.map.ui.map.OPEN_FREE_MAP_STYLE_URL
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.maplibre.android.MapLibre
import org.maplibre.android.module.http.HttpRequestUtil

/**
 * Installs itself into MapLibre Android's native HTTP client on construction — must be created
 * before any `MaplibreMap`/`OfflineManager` first touches the network (Koin registers this
 * `createdAtStart = true`, see `PlatformModule.android.kt`). See [PinnedStyleInterceptor]'s doc for
 * why this exists at all.
 */
class AndroidPinnedStyleInterceptor(context: Context) : PinnedStyleInterceptor {
    @Volatile private var pinnedJson: String? = null

    init {
        // HttpRequestUtil.setOkHttpClient itself triggers HttpRequestImpl's static init, which
        // builds a user-agent string via MapLibre.getApplicationContext() — that throws
        // MapLibreConfigurationException ("requires calling MapLibre.getInstance(...) before
        // inflating or creating the view") unless the native SDK is already initialized. Confirmed
        // as a real crash on-device (FATAL EXCEPTION at app startup, ExceptionInInitializerError):
        // this class is createdAtStart, so it's the very first thing in the app to touch MapLibre,
        // before AndroidOfflineManager's own (normally-first) MapLibre.getInstance() call ever runs.
        // getInstance() is a synchronized singleton getter — safe to call again later from there.
        MapLibre.getInstance(context)
        // HttpRequestUtil.setOkHttpClient replaces the client used for ALL of the SDK's networking
        // (live map tiles/style, ambient cache, offline downloads) — only our exact pinned style URL
        // is special-cased below, everything else must pass through completely unmodified.
        val dispatcher = Dispatcher().apply {
            // Matches org.maplibre.android.module.http.HttpRequestImpl's own default, so bulk tile
            // downloads keep the same parallelism as before this client replaced it.
            maxRequestsPerHost = 20
        }
        val client = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .addInterceptor(::intercept)
            .build()
        HttpRequestUtil.setOkHttpClient(client)
    }

    override fun setPinnedStyle(json: String) {
        pinnedJson = json
    }

    private fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val pinned = pinnedJson
        if (pinned != null && request.url.toString() == OPEN_FREE_MAP_STYLE_URL) {
            // Short-circuit: no real network call for the style resource at all, so the native
            // offline downloader always resolves it from the exact same bytes the live map uses.
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(pinned.toResponseBody("application/json".toMediaType()))
                .build()
        }
        return chain.proceed(request)
    }
}
