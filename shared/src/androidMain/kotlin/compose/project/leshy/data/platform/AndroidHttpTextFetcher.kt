package compose.project.leshy.data.platform

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidHttpTextFetcher : HttpTextFetcher {
    override suspend fun fetchText(url: String): String = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            check(connection.responseCode in 200..299) { "HTTP ${connection.responseCode} for $url" }
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }
}
