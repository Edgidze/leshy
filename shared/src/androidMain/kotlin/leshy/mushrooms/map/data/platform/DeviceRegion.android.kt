package leshy.mushrooms.map.data.platform

import java.util.Locale

actual fun currentDeviceRegionCode(): String? = Locale.getDefault().country.takeIf { it.isNotBlank() }
