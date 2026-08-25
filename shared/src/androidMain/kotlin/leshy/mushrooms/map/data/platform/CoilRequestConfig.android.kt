package leshy.mushrooms.map.data.platform

import coil3.request.ImageRequest
import coil3.request.allowHardware

actual fun ImageRequest.Builder.disallowHardwareBitmaps(): ImageRequest.Builder = allowHardware(false)
