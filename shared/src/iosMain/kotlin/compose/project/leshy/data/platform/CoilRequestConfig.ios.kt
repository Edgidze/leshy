package compose.project.leshy.data.platform

import coil3.request.ImageRequest

actual fun ImageRequest.Builder.disallowHardwareBitmaps(): ImageRequest.Builder = this
