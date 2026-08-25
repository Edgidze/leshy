package leshy.mushrooms.map.data.platform

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val CONNECT_TIMEOUT_MILLIS = 5_000
private const val READ_TIMEOUT_MILLIS = 5_000

class AndroidHttpTextFetcher : HttpTextFetcher {
    override suspend fun fetchText(url: String): String = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            // HttpURLConnection has NO timeout by default (blocks forever) — a silently
            // blackholed connection (ISP dropping packets rather than resetting them) would
            // otherwise hang this blocking call indefinitely. Coroutine cancellation (e.g. an
            // enclosing withTimeoutOrNull) can't interrupt a blocking socket read on its own —
            // it's cooperative, and java.net's blocking I/O doesn't check for it — so the
            // connection's OWN timeout has to be the thing that actually bounds this call.
            connection.connectTimeout = CONNECT_TIMEOUT_MILLIS
            connection.readTimeout = READ_TIMEOUT_MILLIS
            connection.requestMethod = "GET"
            check(connection.responseCode in 200..299) { "HTTP ${connection.responseCode} for $url" }
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }
}
