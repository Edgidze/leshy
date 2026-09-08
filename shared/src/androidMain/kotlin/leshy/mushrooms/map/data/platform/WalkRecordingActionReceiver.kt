package leshy.mushrooms.map.data.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

internal const val ACTION_ADD_MUSHROOM = "leshy.mushrooms.map.ADD_MUSHROOM"
internal const val ACTION_REMOVE_MUSHROOM = "leshy.mushrooms.map.REMOVE_MUSHROOM"
internal const val ACTION_TOGGLE_PAUSE = "leshy.mushrooms.map.TOGGLE_PAUSE"
internal const val EXTRA_CATEGORY_ID = "categoryId"

/**
 * Приёмник нажатий «+»/«−» в уведомлении идущей записи.
 *
 * Broadcast, а не запуск Activity, — и это главное, ради чего он существует: система требует
 * разблокировки экрана только для `PendingIntent`'ов на Activity, а broadcast с замка проходит как
 * есть. Именно поэтому находку можно отметить, не разблокируя телефон.
 *
 * Сам ничего не пишет — перекладывает команду в [RecordingNotificationBus], откуда её забирает
 * `RecordViewModel` и проводит теми же методами, что и нажатие плитки на экране. Дублировать здесь
 * путь находки в Room было бы вторым источником истины: у находки есть лимит на прогулку, привязка
 * к последнему GPS-фиксу и порядок плиток, и всё это уже решено там.
 */
class WalkRecordingActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Пауза приходит без вида — она про прогулку целиком, поэтому разбирается до categoryId.
        if (intent.action == ACTION_TOGGLE_PAUSE) {
            RecordingNotificationBus.commands.tryEmit(RecordingCommand.TogglePause)
            return
        }
        val categoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, -1L).takeIf { it >= 0 } ?: return
        val command = when (intent.action) {
            ACTION_ADD_MUSHROOM -> RecordingCommand.AddMushroom(categoryId)
            ACTION_REMOVE_MUSHROOM -> RecordingCommand.RemoveMushroom(categoryId)
            else -> return
        }
        RecordingNotificationBus.commands.tryEmit(command)
    }
}
