package leshy.mushrooms.map.data.platform

import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale

actual fun currentDeviceRegionCode(): String? = NSLocale.currentLocale.countryCode
