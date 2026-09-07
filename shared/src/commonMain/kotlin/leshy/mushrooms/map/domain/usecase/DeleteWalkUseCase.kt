package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import kotlinx.coroutines.flow.first
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Удаление прогулки целиком: строка в Room плюс файлы, на которые она ссылалась.
 *
 * **Файлы приходится подчищать руками.** `ON DELETE CASCADE` по `walkId` сносит строки
 * `objects`/`track_points`, но ничего не знает о фотографиях находок и миниатюре прогулки, которые
 * лежат отдельными файлами в хранилище приложения; каскадный `DELETE` к тому же не возвращает
 * удалённые строки, поэтому пути нужно собрать ДО удаления.
 *
 * **Почему это отдельный use case, а не метод репозитория и не код экрана.** Раньше эта логика
 * жила прямо в `WalkDetailViewModel.onDeleteConfirm`, а массовое удаление из «Архива»
 * (`ArchiveViewModel.onDeleteConfirm`) звало голый `walkRepository.delete` и файлы не трогало —
 * то есть от способа удаления зависело, останутся ли на устройстве фотографии. Пользователь эти
 * два пути не различает, а политика конфиденциальности прямо обещает, что удаление прогулки
 * удаляет и снимки (`site/privacy.html`, раздел 6). Один вход для обоих экранов — единственный
 * способ, которым это обещание держится само.
 *
 * Удаление файлов — best-effort: оставшийся файл безвреден (его подберёт `RepairPhotoPathsUseCase`
 * как висячий путь), а упавшая уборка не должна превращаться в ошибку удаления прогулки, которая
 * из базы уже ушла.
 */
class DeleteWalkUseCase(
    private val walkRepository: WalkRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    suspend operator fun invoke(walk: Walk) {
        val orphanedPaths = fieldMarkRepository.observeByWalkId(walk.id).first()
            .mapNotNull { it.photoPath } + listOfNotNull(walk.thumbnailPath)
        walkRepository.delete(walk)
        for (path in orphanedPaths) {
            runCatching { fileSystem.delete(path.toPath()) }
        }
    }
}
