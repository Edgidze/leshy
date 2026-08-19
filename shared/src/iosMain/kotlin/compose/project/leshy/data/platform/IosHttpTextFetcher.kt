package compose.project.leshy.data.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSMutableData
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionDataDelegateProtocol
import platform.Foundation.NSURLSessionDataTask
import platform.Foundation.NSURLSessionTask
import platform.Foundation.appendData
import platform.darwin.NSObject
import platform.posix.memcpy
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * `NSURLSession`'s completion-handler `dataTaskWithURL(url:completionHandler:)` overload isn't
 * usable from this project's Kotlin/Native Foundation binding — only the plain, callback-less
 * `dataTaskWithURL(url:)` resolves (confirmed by probing the compiler; the completion-handler
 * selector exists in the klib's string table but isn't exposed as a callable Kotlin overload,
 * likely dropped during header import). So this uses the delegate-based API instead: a custom
 * session with an [NSURLSessionDataDelegateProtocol] that buffers `didReceiveData` chunks and
 * resolves the continuation on `didCompleteWithError`.
 */
class IosHttpTextFetcher : HttpTextFetcher {
    // Class-level var, not a local val inside fetchText — ARC releases an object with no strong
    // references outside a local scope once that scope's last use of it passes, silently breaking
    // delegate callbacks. Same lesson as IosLocationTracker's CLLocationManager delegate — see
    // iosMain/CLAUDE.md.
    private var activeDelegate: Delegate? = null

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun fetchText(url: String): String = suspendCancellableCoroutine { continuation ->
        val delegate = Delegate(continuation)
        activeDelegate = delegate
        val session = NSURLSession.sessionWithConfiguration(
            configuration = NSURLSessionConfiguration.defaultSessionConfiguration(),
            delegate = delegate,
            delegateQueue = NSOperationQueue.mainQueue(),
        )
        val task = session.dataTaskWithURL(NSURL(string = url))
        continuation.invokeOnCancellation { task.cancel() }
        task.resume()
    }

    private class Delegate(
        private val continuation: CancellableContinuation<String>,
    ) : NSObject(), NSURLSessionDataDelegateProtocol {
        private val buffer = NSMutableData()

        override fun URLSession(session: NSURLSession, dataTask: NSURLSessionDataTask, didReceiveData: NSData) {
            buffer.appendData(didReceiveData)
        }

        override fun URLSession(session: NSURLSession, task: NSURLSessionTask, didCompleteWithError: NSError?) {
            if (!continuation.isActive) return
            if (didCompleteWithError != null) {
                continuation.resumeWithException(RuntimeException(didCompleteWithError.localizedDescription))
                return
            }
            continuation.resume(buffer.toByteArray().decodeToString())
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val result = ByteArray(size)
    if (size > 0) {
        result.usePinned { pinned -> memcpy(pinned.addressOf(0), bytes, size.convert()) }
    }
    return result
}
