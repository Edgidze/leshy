package compose.project.leshy.data.platform

import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale

actual fun currentDeviceRegionCode(): String? = NSLocale.currentLocale.countryCode
