package klev.fishing.map.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

/**
 * Миграции перечисляются здесь и только здесь. `fallbackToDestructiveMigration` не вызывается
 * нигде — правило проекта: потерять дневник рыбака из-за обновления приложения недопустимо.
 */
fun buildFishingDatabase(builder: RoomDatabase.Builder<FishingDatabase>): FishingDatabase =
    builder
        .addMigrations(MIGRATION_1_2)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
