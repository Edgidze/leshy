package leshy.mushrooms.map.data.platform

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.view.Surface
import android.view.WindowManager
import leshy.mushrooms.map.domain.util.angleDeltaDegrees
import leshy.mushrooms.map.domain.util.normalizeDegrees
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Курс с `TYPE_ROTATION_VECTOR` — сведённой платформой ориентации по магнитометру, гироскопу и
 * акселерометру, а не с голого `TYPE_MAGNETIC_FIELD`: сырое магнитное поле пришлось бы сводить с
 * гравитацией самим, и получилось бы заметно хуже того, что система уже посчитала.
 *
 * **Разрешений не требует никаких** — ни манифестного, ни рантайм: датчики ориентации в Android их
 * не имеют. `HIGH_SAMPLING_RATE_SENSORS` относится к частотам выше 200 Гц, здесь стоит
 * `SENSOR_DELAY_UI` (порядка 60 мс).
 */
class AndroidHeadingProvider(private val context: Context) : HeadingProvider {

    override fun heading(): Flow<Double> = callbackFlow {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Компаса может не быть физически (планшеты, дешёвые телефоны). Тогда поток закрывается
        // сразу и пустым — договор [HeadingProvider.heading], вызывающий останется на GPS-курсе.
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) ?: run {
            close()
            return@callbackFlow
        }

        // Склонение считается один раз на подписку, а не на каждое событие датчика: оно меняется
        // на градус примерно на сотню километров и на сутки — за одну прогулку измениться не
        // может, а `GeomagneticField` — не бесплатный расчёт по модели поля Земли.
        val declination = currentDeclinationDegrees()

        val rotationMatrix = FloatArray(MATRIX_SIZE)
        val remappedMatrix = FloatArray(MATRIX_SIZE)
        val orientation = FloatArray(3)

        val listener = object : SensorEventListener {
            private var lastSent: Double? = null

            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                val (axisX, axisY) = displayAxes()
                SensorManager.remapCoordinateSystem(rotationMatrix, axisX, axisY, remappedMatrix)
                SensorManager.getOrientation(remappedMatrix, orientation)
                val magnetic = Math.toDegrees(orientation[0].toDouble())
                val value = normalizeDegrees(magnetic + declination)
                // Порог — здесь, а не у подписчика: смысл в том, чтобы неподвижный телефон вообще
                // не порождал событий, а не в том, чтобы их потом отфильтровывали.
                val previous = lastSent
                if (previous != null && angleDeltaDegrees(previous, value) < HEADING_MIN_CHANGE_DEGREES) return
                lastSent = value
                trySend(value)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        awaitClose { sensorManager.unregisterListener(listener) }
    }

    /**
     * Магнитное склонение в точке, где человек сейчас находится, — то, что превращает магнитный
     * курс в истинный (зачем это обязательно — см. KDoc [HeadingProvider]).
     *
     * Берётся из последнего известного фикса СИСТЕМЫ, а не из потока `LocationTracker`: провайдеру
     * курса не нужна свежая координата, ему нужна любая с точностью до сотни километров, и
     * протаскивать ради этого местоположение через весь слой было бы дороже пользы. Фикса может не
     * быть вовсе (первый запуск в помещении) — тогда склонение считается нулевым, то есть курс
     * остаётся магнитным. Это на порядок лучше, чем не показывать курс совсем, и само исправится
     * на следующей подписке, когда фикс появится.
     */
    private fun currentDeclinationDegrees(): Double {
        if (!hasFineLocationPermission(context)) return 0.0
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return 0.0
        val location = runCatching {
            locationManager.allProviders.asSequence()
                .mapNotNull { provider -> runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull() }
                .maxByOrNull { it.time }
        }.getOrNull() ?: return 0.0
        return GeomagneticField(
            location.latitude.toFloat(),
            location.longitude.toFloat(),
            location.altitude.toFloat(),
            location.time,
        ).declination.toDouble()
    }

    /**
     * Пересчёт осей под текущий поворот экрана. Без него курс уезжал бы на 90° у того, кто держит
     * телефон боком.
     *
     * Ограничение приёма, которое стоит знать: азимут из `getOrientation` определён для телефона,
     * лежащего плашмя, и вырождается, когда экран поднят почти вертикально (верхнее ребро смотрит
     * в небо — классический gimbal lock). Здесь это приемлемо: на «Записи» человек смотрит в
     * карту, то есть держит телефон примерно горизонтально — это и есть тот случай, для которого
     * соглашение и рассчитано.
     */
    @Suppress("DEPRECATION")
    private fun displayAxes(): Pair<Int, Int> {
        // `Context.display` — с API 30, а minSdk 24, поэтому через WindowManager. Ветка на версию
        // здесь была бы двумя путями к одному значению; путь через WindowManager работает на всех
        // поддерживаемых версиях.
        val rotation = (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)
            ?.defaultDisplay?.rotation ?: Surface.ROTATION_0
        return when (rotation) {
            Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
            Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
            Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
            else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
        }
    }
}

private const val MATRIX_SIZE = 9
