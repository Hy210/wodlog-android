package com.wodlog.app.presentation.wodedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.MovementCategory
import com.wodlog.app.domain.model.ImportedWodText
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodSection
import com.wodlog.app.domain.model.WodType
import com.wodlog.app.domain.repository.WodlogRepository
import com.wodlog.app.util.ValidationError
import com.wodlog.app.util.WodlogDateUtils
import com.wodlog.app.util.WodlogValidators
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WodEditViewModel(
    private val repository: WodlogRepository,
    private val editingWodId: Long? = null,
    importedWodText: ImportedWodText? = null,
    showImportedPrefillMissingMessage: Boolean = false,
    todayProvider: () -> LocalDate = { LocalDate.now() },
    private val nowProvider: () -> Instant = { Instant.now() },
    private val localIdProvider: () -> String = { UUID.randomUUID().toString() }
) : ViewModel() {
    private var originalWod: Wod? = null
    private val today = todayProvider()
    private val _uiState = MutableStateFlow(
        importedWodText?.toPrefilledState(today)
            ?: WodEditUiState(
                dateInput = WodlogDateUtils.formatDate(today),
                message = if (showImportedPrefillMissingMessage) {
                    "가져온 내용이 없어 일반 WOD 입력으로 시작합니다."
                } else {
                    null
                }
            )
    )
    val uiState: StateFlow<WodEditUiState> = _uiState.asStateFlow()

    init {
        editingWodId?.let(::loadExistingWod)
    }

    private fun loadExistingWod(wodId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    editingWodId = wodId,
                    isLoading = true,
                    message = null,
                    validationErrors = emptyList()
                )
            }

            val wod = repository.getWodById(wodId)
            if (wod == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = "수정할 WOD를 찾을 수 없습니다."
                    )
                }
                return@launch
            }

            originalWod = wod
            val sections = repository.getSectionsForWod(wodId).sortedBy { it.orderIndex }
            val sectionLocalIdById = sections.associate { section ->
                section.id to "section-${section.id}"
            }
            val movements = repository.getMovementsForWod(wodId).sortedBy { it.orderIndex }

            _uiState.update {
                it.copy(
                    editingWodId = wod.id,
                    dateInput = WodlogDateUtils.formatDate(wod.date),
                    titleInput = wod.title,
                    wodType = wod.type,
                    rawTextInput = wod.rawText.orEmpty(),
                    memoInput = wod.notes.orEmpty(),
                    sourceType = wod.sourceType,
                    sourceUrl = wod.sourceUrl,
                    importedAt = wod.importedAt,
                    sections = sections.map { section ->
                        WodSectionInputState(
                            localId = sectionLocalIdById.getValue(section.id),
                            originalId = section.id,
                            titleInput = section.name
                        )
                    },
                    movements = movements.map { movement ->
                        MovementInputState(
                            localId = "movement-${movement.id}",
                            originalId = movement.id,
                            sectionLocalId = movement.sectionId?.let(sectionLocalIdById::get),
                            nameInput = movement.name,
                            weightInput = movement.weightKg?.toString().orEmpty(),
                            repsInput = movement.reps?.toString().orEmpty(),
                            setsInput = movement.sets?.toString().orEmpty(),
                            roundsInput = movement.rounds?.toString().orEmpty(),
                            distanceInput = movement.distanceMeters?.toString().orEmpty(),
                            caloriesInput = movement.calories?.toString().orEmpty(),
                            timeSecondsInput = movement.durationSeconds?.toString().orEmpty(),
                            category = movement.category ?: MovementCategory.OTHER,
                            memoInput = movement.notes.orEmpty()
                        )
                    },
                    isLoading = false
                )
            }
        }
    }

    fun onDateChange(value: String) {
        updateInput { copy(dateInput = value) }
    }

    fun onTitleChange(value: String) {
        updateInput { copy(titleInput = value) }
    }

    fun onWodTypeChange(value: WodType) {
        updateInput { copy(wodType = value) }
    }

    fun onRawTextChange(value: String) {
        updateInput { copy(rawTextInput = value) }
    }

    fun onMemoChange(value: String) {
        updateInput { copy(memoInput = value) }
    }

    fun addSection() {
        updateInput {
            copy(sections = sections + WodSectionInputState(localId = localIdProvider()))
        }
    }

    fun removeSection(localId: String) {
        updateInput {
            copy(
                sections = sections.filterNot { it.localId == localId },
                movements = movements.map { movement ->
                    if (movement.sectionLocalId == localId) {
                        movement.copy(sectionLocalId = null)
                    } else {
                        movement
                    }
                }
            )
        }
    }

    fun updateSectionTitle(localId: String, value: String) {
        updateInput {
            copy(sections = sections.mapSection(localId) { copy(titleInput = value) })
        }
    }

    fun updateSectionMemo(localId: String, value: String) {
        updateInput {
            copy(sections = sections.mapSection(localId) { copy(memoInput = value) })
        }
    }

    fun addMovement(sectionLocalId: String? = null) {
        updateInput {
            copy(
                movements = movements + MovementInputState(
                    localId = localIdProvider(),
                    sectionLocalId = sectionLocalId
                )
            )
        }
    }

    fun removeMovement(localId: String) {
        updateInput {
            copy(movements = movements.filterNot { it.localId == localId })
        }
    }

    fun updateMovementName(localId: String, value: String) {
        updateMovement(localId) { copy(nameInput = value) }
    }

    fun updateMovementWeight(localId: String, value: String) {
        updateMovement(localId) { copy(weightInput = value) }
    }

    fun updateMovementReps(localId: String, value: String) {
        updateMovement(localId) { copy(repsInput = value) }
    }

    fun updateMovementSets(localId: String, value: String) {
        updateMovement(localId) { copy(setsInput = value) }
    }

    fun updateMovementRounds(localId: String, value: String) {
        updateMovement(localId) { copy(roundsInput = value) }
    }

    fun updateMovementDistance(localId: String, value: String) {
        updateMovement(localId) { copy(distanceInput = value) }
    }

    fun updateMovementCalories(localId: String, value: String) {
        updateMovement(localId) { copy(caloriesInput = value) }
    }

    fun updateMovementTimeSeconds(localId: String, value: String) {
        updateMovement(localId) { copy(timeSecondsInput = value) }
    }

    fun updateMovementCategory(localId: String, value: MovementCategory) {
        updateMovement(localId) { copy(category = value) }
    }

    fun updateMovementMemo(localId: String, value: String) {
        updateMovement(localId) { copy(memoInput = value) }
    }

    fun saveWod() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, message = null, savedWodId = null) }

            val currentState = _uiState.value
            val parsedInput = currentState.toParsedInput()
            val errors = parsedInput.validationErrors

            if (errors.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        validationErrors = errors.distinct(),
                        message = "Check WOD input values"
                    )
                }
                return@launch
            }

            val now = nowProvider()
            val original = originalWod
            val wodId = repository.saveWod(
                if (original != null) {
                    original.copy(
                        date = requireNotNull(parsedInput.date),
                        title = currentState.titleInput.trim(),
                        type = requireNotNull(currentState.wodType),
                        rawText = currentState.rawTextInput.trimToNull(),
                        notes = currentState.memoInput.trimToNull(),
                        updatedAt = now
                    )
                } else {
                    Wod(
                        date = requireNotNull(parsedInput.date),
                        title = currentState.titleInput.trim(),
                        type = requireNotNull(currentState.wodType),
                        rawText = currentState.rawTextInput.trimToNull(),
                        notes = currentState.memoInput.trimToNull(),
                        sourceType = currentState.sourceType,
                        sourceUrl = currentState.sourceUrl?.trimToNull(),
                        importedAt = currentState.importedAt,
                        createdAt = now,
                        updatedAt = now
                    )
                }
            )
            if (original != null) {
                replaceExistingSectionsAndMovements(wodId)
            }
            val sectionIdByLocalId = saveSections(wodId, currentState.sections)
            saveMovements(wodId, parsedInput.movements, sectionIdByLocalId)

            _uiState.update {
                it.copy(
                    isSaving = false,
                    validationErrors = emptyList(),
                    message = if (original != null) "수정되었습니다." else "WOD saved",
                    savedWodId = wodId
                )
            }
        }
    }

    fun onSavedNavigationHandled() {
        _uiState.update { it.copy(savedWodId = null) }
    }

    private suspend fun saveSections(
        wodId: Long,
        sections: List<WodSectionInputState>
    ): Map<String, Long> {
        return sections.mapIndexed { index, section ->
            val sectionId = repository.saveWodSection(
                WodSection(
                    wodId = wodId,
                    name = section.titleInput.trim(),
                    orderIndex = index
                )
            )
            section.localId to sectionId
        }.toMap()
    }

    private suspend fun replaceExistingSectionsAndMovements(wodId: Long) {
        repository.getMovementsForWod(wodId).forEach { movement ->
            repository.deleteMovement(movement.id)
        }
        repository.getSectionsForWod(wodId).forEach { section ->
            repository.deleteWodSection(section.id)
        }
    }

    private suspend fun saveMovements(
        wodId: Long,
        movements: List<ParsedMovementInput>,
        sectionIdByLocalId: Map<String, Long>
    ) {
        movements.forEachIndexed { index, movement ->
            repository.saveMovement(
                Movement(
                    wodId = wodId,
                    sectionId = movement.sectionLocalId?.let(sectionIdByLocalId::get),
                    name = movement.name,
                    category = movement.category,
                    weightKg = movement.weightKg,
                    reps = movement.reps,
                    sets = movement.sets,
                    rounds = movement.rounds,
                    distanceMeters = movement.distanceMeters,
                    calories = movement.calories,
                    durationSeconds = movement.durationSeconds,
                    orderIndex = index,
                    notes = movement.notes
                )
            )
        }
    }

    private fun WodEditUiState.toParsedInput(): ParsedWodInput {
        val date = parseDateInput(dateInput)
        val parsedMovements = movements.map { it.toParsedMovementInput() }
        val parseErrors = buildList {
            if (dateInput.isBlank()) {
                add(ValidationError.WOD_DATE_REQUIRED)
            } else if (date == null) {
                add(ValidationError.WOD_DATE_INVALID)
            }
            parsedMovements.forEach { addAll(it.parseErrors) }
        }
        val wodErrors = WodlogValidators.validateWodInput(
            date = date,
            title = titleInput,
            type = wodType
        ).errors
        val movementErrors = parsedMovements.flatMap { movement ->
            WodlogValidators.validateMovementInput(
                name = movement.name,
                category = movement.category,
                weightKg = movement.weightKg,
                reps = movement.reps,
                sets = movement.sets,
                rounds = movement.rounds,
                distanceMeters = movement.distanceMeters,
                calories = movement.calories,
                durationSeconds = movement.durationSeconds
            ).errors
        }

        return ParsedWodInput(
            date = date,
            movements = parsedMovements,
            validationErrors = parseErrors + wodErrors + movementErrors
        )
    }

    private fun MovementInputState.toParsedMovementInput(): ParsedMovementInput {
        val weight = parseOptionalDouble(weightInput)
        val reps = parseOptionalInt(repsInput)
        val sets = parseOptionalInt(setsInput)
        val rounds = parseOptionalInt(roundsInput)
        val distance = parseOptionalDouble(distanceInput)
        val calories = parseOptionalDouble(caloriesInput)
        val durationSeconds = parseOptionalInt(timeSecondsInput)

        val parseErrors = buildList {
            if (weightInput.isNotBlank() && weight == null) add(ValidationError.MOVEMENT_WEIGHT_INVALID)
            if (repsInput.isNotBlank() && reps == null) add(ValidationError.MOVEMENT_REPS_INVALID)
            if (setsInput.isNotBlank() && sets == null) add(ValidationError.MOVEMENT_SETS_INVALID)
            if (roundsInput.isNotBlank() && rounds == null) add(ValidationError.MOVEMENT_ROUNDS_INVALID)
            if (distanceInput.isNotBlank() && distance == null) add(ValidationError.MOVEMENT_DISTANCE_INVALID)
            if (caloriesInput.isNotBlank() && calories == null) add(ValidationError.MOVEMENT_CALORIES_INVALID)
            if (timeSecondsInput.isNotBlank() && durationSeconds == null) add(ValidationError.MOVEMENT_DURATION_INVALID)
        }

        return ParsedMovementInput(
            sectionLocalId = sectionLocalId,
            name = nameInput.trim(),
            weightKg = weight,
            reps = reps,
            sets = sets,
            rounds = rounds,
            distanceMeters = distance,
            calories = calories,
            durationSeconds = durationSeconds,
            category = category,
            notes = memoInput.trimToNull(),
            parseErrors = parseErrors
        )
    }

    private fun updateMovement(localId: String, transform: MovementInputState.() -> MovementInputState) {
        updateInput {
            copy(movements = movements.map { if (it.localId == localId) it.transform() else it })
        }
    }

    private fun updateInput(transform: WodEditUiState.() -> WodEditUiState) {
        _uiState.update {
            it.transform().copy(
                validationErrors = emptyList(),
                message = null
            )
        }
    }

    private fun List<WodSectionInputState>.mapSection(
        localId: String,
        transform: WodSectionInputState.() -> WodSectionInputState
    ): List<WodSectionInputState> {
        return map { if (it.localId == localId) it.transform() else it }
    }

    private fun parseDateInput(value: String): LocalDate? {
        return value.trim().takeIf { it.isNotEmpty() }?.let(WodlogDateUtils::parseDateOrNull)
    }

    private fun parseOptionalDouble(value: String): Double? {
        return value.trim().takeIf { it.isNotEmpty() }?.toDoubleOrNull()
    }

    private fun parseOptionalInt(value: String): Int? {
        return value.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
    }

    private fun String.trimToNull(): String? {
        return trim().takeIf { it.isNotEmpty() }
    }

    private fun ImportedWodText.toPrefilledState(today: LocalDate): WodEditUiState {
        return WodEditUiState(
            dateInput = WodlogDateUtils.formatDate(today),
            titleInput = title.trim(),
            wodType = WodType.OTHER,
            rawTextInput = importedText,
            sourceType = sourceType,
            sourceUrl = sourceUrl,
            importedAt = importedAt
        )
    }
}

private data class ParsedWodInput(
    val date: LocalDate?,
    val movements: List<ParsedMovementInput>,
    val validationErrors: List<ValidationError>
)

private data class ParsedMovementInput(
    val sectionLocalId: String?,
    val name: String,
    val weightKg: Double?,
    val reps: Int?,
    val sets: Int?,
    val rounds: Int?,
    val distanceMeters: Double?,
    val calories: Double?,
    val durationSeconds: Int?,
    val category: MovementCategory,
    val notes: String?,
    val parseErrors: List<ValidationError>
)
