package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.data.catalog.catalogKeyForLegacy
import leshy.mushrooms.map.data.export.dto.CATEGORIES_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.COLLECTIONS_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.CategoryExportDto
import leshy.mushrooms.map.data.export.dto.CollectionExportDto
import leshy.mushrooms.map.data.export.dto.ExportJson
import leshy.mushrooms.map.data.export.dto.OBJECTS_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.ObjectExportDto
import leshy.mushrooms.map.data.export.dto.TRACK_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.TrackPointExportDto
import leshy.mushrooms.map.data.export.dto.WALK_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.WalkExportDto
import leshy.mushrooms.map.data.export.dto.categoryIconEntryName
import leshy.mushrooms.map.data.export.zip.ZipReader
import leshy.mushrooms.map.data.platform.PhotoStorage
import leshy.mushrooms.map.data.platform.currentTimeMillis
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.CategorySource
import leshy.mushrooms.map.domain.model.Collection
import leshy.mushrooms.map.domain.model.CollectionSource
import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.model.TrackPoint
import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.CollectionRepository
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.TrackPointRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Reads an archive written by [ExportDataUseCase] and inserts its walks as brand-new rows (new
 * ids) — never merge/overwrite. [walkNameTag], if non-blank, is appended to every imported walk's
 * name so they're visually distinguishable from walks already on this device. Each walk is
 * imported independently — there's no cross-walk Room transaction (no precedent for one anywhere
 * else in this codebase, and the domain layer only sees repositories, not the raw database), so a
 * walk whose entries are malformed is skipped rather than aborting the whole import; [Result]
 * reports how many succeeded/failed so the caller can tell the user.
 *
 * Non-catalog species (`categories/categories.json`, `.claude/plans/user-mushrooms.md` Phase 6)
 * are the one exception to "always insert new, never merge": they're merged by [Category.nameKey]
 * — the globally-unique identifier finds are keyed on — *before* any walk is parsed, so
 * [ObjectExportDto.categoryNameKey] resolves against them. A missing/malformed `categories/`
 * section (older archive, or corrupt entry) is skipped rather than failing the import; any find
 * whose species didn't come through falls back to `category_misc`, same as an unknown
 * `categoryNameKey` always has.
 */
class ImportDataUseCase(
    private val validateArchive: ValidateImportArchiveUseCase,
    private val walkRepository: WalkRepository,
    private val trackPointRepository: TrackPointRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository,
    private val photoStorage: PhotoStorage,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
    data class Result(val importedWalkCount: Int, val failedWalkCount: Int)

    class RejectedException(val problem: ImportArchiveProblem) : IllegalArgumentException(problem.name)

    suspend operator fun invoke(archiveBytes: ByteArray, walkNameTag: String): Result {
        // Nothing is written until the whole archive has been checked — the species merge below
        // runs before the first walk is even parsed, so a file that only falls apart halfway would
        // otherwise still leave new category rows behind. The caller (DataViewModel) normally
        // validates at file-pick time and never gets here with a bad archive; this is the barrier
        // that makes that a guarantee rather than a convention.
        validateArchive(archiveBytes)?.let { throw RejectedException(it) }
        val reader = ZipReader(archiveBytes)

        importCollections(reader, importCategories(reader))

        val categoryIdByNameKey = categoryRepository.observeAll().first().associate { it.nameKey to it.id }
        val miscCategoryId = requireNotNull(categoryIdByNameKey[MISC_CATEGORY_NAME_KEY]) {
            "Misc category must exist before importing"
        }
        val importBatchId = currentTimeMillis()

        val walkDirs = reader.entries.map { it.name }
            .filter { it.endsWith("/$WALK_ENTRY_NAME") }
            .map { it.removeSuffix("/$WALK_ENTRY_NAME") }
            .distinct()

        var imported = 0
        var failed = 0
        for (dir in walkDirs) {
            val ok = runCatching {
                importWalk(reader, dir, walkNameTag, categoryIdByNameKey, miscCategoryId, importBatchId)
            }.isSuccess
            if (ok) imported++ else failed++
        }
        return Result(imported, failed)
    }

    private suspend fun importWalk(
        reader: ZipReader,
        dir: String,
        walkNameTag: String,
        categoryIdByNameKey: Map<String, Long>,
        miscCategoryId: Long,
        importBatchId: Long,
    ) {
        val walkDto = ExportJson.decodeFromString<WalkExportDto>(
            requireNotNull(reader.readEntry("$dir/$WALK_ENTRY_NAME")) { "Missing $dir/$WALK_ENTRY_NAME" }
                .decodeToString(),
        )
        val trackDtos = reader.readEntry("$dir/$TRACK_ENTRY_NAME")?.decodeToString()?.let {
            ExportJson.decodeFromString(ListSerializer(TrackPointExportDto.serializer()), it)
        } ?: emptyList()
        val objectDtos = reader.readEntry("$dir/$OBJECTS_ENTRY_NAME")?.decodeToString()?.let {
            ExportJson.decodeFromString(ListSerializer(ObjectExportDto.serializer()), it)
        } ?: emptyList()

        val newWalkId = walkRepository.insert(walkDto.toDomain(walkNameTag))

        trackDtos.forEach { point -> trackPointRepository.addPoint(point.toDomain(newWalkId)) }

        objectDtos.forEachIndexed { index, obj ->
            // An archive written before the v10->v11 re-keying records catalog species under their
            // old key (`category_boletus_edulis`); without the second lookup every find in it would
            // land on `category_misc` and lose its species. `catalogKeyForLegacy` is the same
            // mapping the migration itself uses, and a no-op for anything already current.
            val categoryId = categoryIdByNameKey[obj.categoryNameKey]
                ?: categoryIdByNameKey[catalogKeyForLegacy(obj.categoryNameKey)]
                ?: miscCategoryId
            val photoPath = obj.photoFile?.let { relativePath ->
                copyPhoto(reader, "$dir/$relativePath", importBatchId, walkDto.originalId, index)
            }
            fieldMarkRepository.addMark(obj.toDomain(newWalkId, categoryId, photoPath))
        }
    }

    private fun copyPhoto(
        reader: ZipReader,
        entryPath: String,
        importBatchId: Long,
        originalWalkId: Long,
        objectIndex: Int,
    ): String? {
        val bytes = reader.readEntry(entryPath) ?: return null
        val extension = entryPath.substringAfterLast('.', "jpg")
        val fileName = "imported_${importBatchId}_${originalWalkId}_$objectIndex.$extension"
        val destination = photoStorage.resolvePath(fileName)
        fileSystem.write(destination.toPath()) { write(bytes) }
        return destination
    }

    /** A category entry that fails to parse must not abort the rest — same "best effort" contract
     * as [importWalk]'s [runCatching] in [invoke]. Order matters here: no `runCatching` at the list
     * level, each row is wrapped individually so one bad row doesn't take its siblings down with it.
     *
     * Возвращает соответствие «ключ вида в архиве → id вида на этом устройстве»: по нему
     * [importCollections] раскладывает приехавшие виды по подборкам. Вид, который слился с местным,
     * отдаёт id местного — именно поэтому карту строит эта функция, а не вызывающий её код. */
    private suspend fun importCategories(reader: ZipReader): Map<String, Long> {
        val dtos = reader.readEntry(CATEGORIES_ENTRY_NAME)?.decodeToString()?.let { text ->
            runCatching {
                ExportJson.decodeFromString(ListSerializer(CategoryExportDto.serializer()), text)
            }.getOrNull()
        } ?: return emptyMap()

        // Один снимок на весь импорт: список нужен только для поиска совпадений по названию, а
        // виды, создаваемые ниже, совпасть сами с собой не могут — у них уникальный `nameKey`.
        val local = categoryRepository.observeNonCatalog().first()
        val resolved = mutableMapOf<String, Long>()
        for (dto in dtos) {
            runCatching { importCategory(reader, dto, local) }.getOrNull()?.let { resolved[dto.nameKey] = it }
        }
        return resolved
    }

    /** Merge-by-[Category.nameKey], per the three-way table in `.claude/plans/user-mushrooms.md`
     * (Phase 6): no local row → create as [CategorySource.IMPORTED] with the icon; local row
     * without an icon → attach the archive's icon, touch nothing else; local row with an icon
     * already → the local species wins outright, nothing to do. Возвращает id вида, под которым
     * находки этого архива будут жить дальше, или `null`, если вид не взят вовсе.
     *
     * Второй ключ слияния — [matchesByNameAndScientificName] — добавлен вместе с подборками
     * (`.claude/plans/user-collections.md`) и намеренно требует совпадения И названия, И латыни.
     * Одного названия мало: «Белый» у двух разных людей запросто окажется разными грибами, и
     * склеить их — необратимая порча чужих находок. Латынь у пользовательского вида заполнена почти
     * всегда (пустую подставляет `scientificNameFallback`), так что пара «название + латынь» —
     * настоящая улика, а не совпадение слова. */
    private suspend fun importCategory(reader: ZipReader, dto: CategoryExportDto, local: List<Category>): Long? {
        val existing = categoryRepository.getByNameKey(dto.nameKey)
            ?: local.firstOrNull { matchesByNameAndScientificName(it, dto) }
        // Catalog rows are never a merge target. ExportDataUseCase only ever writes non-APP
        // species, so this can't happen for an archive this app produced — but `nameKey` is just a
        // string in a JSON file, and a hand-edited or corrupted one naming `boletus_edulis` (or
        // `category_misc`) would otherwise attach a foreign icon to a catalog species, which
        // EnsureDefaultCategoriesUseCase would then keep re-seeding around forever.
        if (existing != null && existing.source == CategorySource.APP) return null
        val target = existing ?: dto.toDomain().let { created -> created.copy(id = categoryRepository.upsert(created)) }
        if (!dto.hasIcon || target.iconFile != null) return target.id
        val bytes = reader.readEntry(categoryIconEntryName(dto.nameKey)) ?: return target.id
        // Deterministic (not timestamped like SaveCategoryIconUseCase's) so a repeat import of the
        // same archive overwrites this file instead of piling up copies.
        val fileName = "catimg_${dto.nameKey}.png"
        fileSystem.write(photoStorage.resolvePath(fileName).toPath()) { write(bytes) }
        categoryRepository.upsert(target.copy(iconFile = fileName))
        return target.id
    }

    /** Названия сравниваются в пределах одного языка: «Rotkappe» по-немецки и «Rotkappe», записанное
     * кем-то в русское поле, — не одно и то же утверждение. Достаточно одного общего языка, в
     * котором названия совпали, — заполнять все 42 никто не обязан. */
    private fun matchesByNameAndScientificName(local: Category, dto: CategoryExportDto): Boolean {
        val archiveLatin = dto.scientificName?.trim().orEmpty()
        val localLatin = local.scientificName?.trim().orEmpty()
        if (archiveLatin.isEmpty() || !archiveLatin.equals(localLatin, ignoreCase = true)) return false
        return local.customNames.any { (language, name) ->
            dto.customNames[language.code]?.trim()?.equals(name.trim(), ignoreCase = true) == true
        }
    }

    /** Пользовательские подборки архива (`.claude/plans/user-collections.md`). Ключ слияния —
     * [Collection.nameKey], затем имя без учёта регистра: подборка, заведённая на двух устройствах
     * с одинаковым названием, обязана остаться одной, иначе на экране появятся два одинаковых
     * заголовка подряд. Не совпало ничего — подборка создаётся как [CollectionSource.IMPORTED].
     *
     * Вид, у которого на этом устройстве СВОЯ подборка уже есть, не переносится: местная раскладка
     * старше приезжей, и импорт архива не повод её пересобирать. Практически это ровно те виды,
     * что слились с местными по [matchesByNameAndScientificName]; заново созданные подборки не
     * имеют, и попадают в приехавшую.
     *
     * Подборка, в которую в итоге ничего не легло, не остаётся пустой строкой в базе — пустых
     * пользовательских подборок в приложении не бывает. */
    private suspend fun importCollections(reader: ZipReader, categoryIdByArchiveKey: Map<String, Long>) {
        if (categoryIdByArchiveKey.isEmpty()) return
        val dtos = reader.readEntry(COLLECTIONS_ENTRY_NAME)?.decodeToString()?.let { text ->
            runCatching {
                ExportJson.decodeFromString(ListSerializer(CollectionExportDto.serializer()), text)
            }.getOrNull()
        } ?: return

        val local = collectionRepository.getAll().filter { it.source != CollectionSource.COUNTRY }
        for (dto in dtos) runCatching { importCollection(dto, local, categoryIdByArchiveKey) }
    }

    private suspend fun importCollection(
        dto: CollectionExportDto,
        local: List<Collection>,
        categoryIdByArchiveKey: Map<String, Long>,
    ) {
        val existing = local.firstOrNull { it.nameKey == dto.nameKey }
            ?: dto.name?.let { name -> local.firstOrNull { it.name.equals(name, ignoreCase = true) } }
        val target = existing ?: Collection(
            id = 0,
            nameKey = dto.nameKey,
            // Приехавшие «Другие» обязаны встать в свою полосу, иначе на экране они окажутся не
            // последними, а вперемешку с именованными подборками.
            order = if (dto.nameKey == OTHER_COLLECTION_NAME_KEY) OTHER_COLLECTION_ORDER else USER_COLLECTION_ORDER,
            source = CollectionSource.IMPORTED,
            name = dto.name,
        ).let { created -> created.copy(id = collectionRepository.upsert(created)) }

        var added = 0
        for (key in dto.memberNameKeys) {
            val categoryId = categoryIdByArchiveKey[key] ?: continue
            val alreadyGrouped = collectionRepository.getMemberCollectionIds(categoryId)
                .mapNotNull { collectionRepository.getById(it) }
                .any { it.source != CollectionSource.COUNTRY }
            if (alreadyGrouped) continue
            collectionRepository.addMember(categoryId, target.id)
            added++
        }
        if (existing == null && added == 0) collectionRepository.delete(target)
    }
}

private fun WalkExportDto.toDomain(nameTag: String) = Walk(
    id = 0,
    name = if (nameTag.isBlank()) name else "$name $nameTag",
    startTime = startTime,
    endTime = endTime,
    distanceMeters = distanceMeters,
    avgSpeed = avgSpeed,
    startLat = startLat,
    startLon = startLon,
    endLat = endLat,
    endLon = endLon,
    mushroomCount = mushroomCount,
    thumbnailPath = null, // re-derived by BackfillWalkThumbnailsUseCase next time Archive opens
    description = description,
)

private fun TrackPointExportDto.toDomain(walkId: Long) = TrackPoint(
    id = 0,
    walkId = walkId,
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    elevation = elevation,
    sequence = sequence,
)

private fun ObjectExportDto.toDomain(walkId: Long, categoryId: Long, photoPath: String?) = FieldMark(
    id = 0,
    walkId = walkId,
    categoryId = categoryId,
    lat = lat,
    lon = lon,
    timestamp = timestamp,
    type = MarkType.valueOf(type),
    photoPath = photoPath,
    name = name,
    description = description,
)

private fun CategoryExportDto.toDomain() = Category(
    id = 0,
    nameKey = nameKey,
    colorHex = colorHex,
    iconRef = null,
    order = USER_SPECIES_ORDER,
    isActive = true,
    isPicked = true,
    isFilterEligible = true,
    source = CategorySource.IMPORTED,
    customNames = customNames.mapNotNull { (code, name) ->
        AppLanguage.entries.firstOrNull { it.code == code }?.let { it to name }
    }.toMap(),
    scientificName = scientificName,
    iconFile = null,
)
