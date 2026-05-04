package com.wodlog.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.repository.WodlogRepository
import com.wodlog.app.util.ValidationError
import com.wodlog.app.util.WodlogDateUtils
import com.wodlog.app.util.WodlogValidators
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: WodlogRepository,
    private val todayProvider: () -> LocalDate = { LocalDate.now() },
    private val nowProvider: () -> Instant = { Instant.now() }
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var loadedProfile: UserProfile? = null

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }

            val profile = repository.getUserProfile()
            loadedProfile = profile

            _uiState.value = if (profile == null) {
                ProfileUiState(
                    crossfitStartDateInput = WodlogDateUtils.formatDate(todayProvider()),
                    trainingDays = 0L
                )
            } else {
                ProfileUiState(
                    heightCmInput = profile.heightCm?.toInputString().orEmpty(),
                    weightKgInput = profile.weightKg?.toInputString().orEmpty(),
                    crossfitStartDateInput = profile.crossfitStartDate?.let(WodlogDateUtils::formatDate).orEmpty(),
                    trainingDays = profile.crossfitStartDate.trainingDaysOrZero(),
                    hasProfile = true
                )
            }
        }
    }

    fun onHeightChange(value: String) {
        _uiState.update {
            it.copy(
                heightCmInput = value,
                validationErrors = emptyList(),
                message = null
            )
        }
    }

    fun onWeightChange(value: String) {
        _uiState.update {
            it.copy(
                weightKgInput = value,
                validationErrors = emptyList(),
                message = null
            )
        }
    }

    fun onCrossfitStartDateChange(value: String) {
        val startDate = parseOptionalDate(value)
        _uiState.update {
            it.copy(
                crossfitStartDateInput = value,
                trainingDays = startDate.trainingDaysOrZero(),
                validationErrors = emptyList(),
                message = null
            )
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, message = null) }

            val currentState = _uiState.value
            val heightCm = parseOptionalDouble(currentState.heightCmInput)
            val weightKg = parseOptionalDouble(currentState.weightKgInput)
            val crossfitStartDate = parseOptionalDate(currentState.crossfitStartDateInput)
            val parseErrors = buildParseErrors(currentState, heightCm, weightKg, crossfitStartDate)
            val validation = WodlogValidators.validateProfileInput(
                heightCm = heightCm,
                weightKg = weightKg,
                crossfitStartDate = crossfitStartDate,
                today = todayProvider()
            )
            val errors = parseErrors + validation.errors

            if (errors.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        validationErrors = errors.distinct(),
                        message = "입력값을 확인해 주세요"
                    )
                }
                return@launch
            }

            val now = nowProvider()
            val profile = UserProfile(
                id = loadedProfile?.id ?: 1L,
                heightCm = heightCm,
                weightKg = weightKg,
                crossfitStartDate = crossfitStartDate,
                createdAt = loadedProfile?.createdAt ?: now,
                updatedAt = now
            )
            repository.saveUserProfile(profile)
            loadedProfile = profile

            _uiState.update {
                it.copy(
                    isSaving = false,
                    trainingDays = crossfitStartDate.trainingDaysOrZero(),
                    validationErrors = emptyList(),
                    message = "프로필 저장 완료",
                    hasProfile = true
                )
            }
        }
    }

    private fun buildParseErrors(
        state: ProfileUiState,
        heightCm: Double?,
        weightKg: Double?,
        crossfitStartDate: LocalDate?
    ): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (state.heightCmInput.isNotBlank() && heightCm == null) {
            errors += ValidationError.HEIGHT_OUT_OF_RANGE
        }
        if (state.weightKgInput.isNotBlank() && weightKg == null) {
            errors += ValidationError.WEIGHT_OUT_OF_RANGE
        }
        if (state.crossfitStartDateInput.isNotBlank() && crossfitStartDate == null) {
            errors += ValidationError.CROSSFIT_START_DATE_IN_FUTURE
        }

        return errors
    }

    private fun parseOptionalDouble(value: String): Double? {
        return value.trim().takeIf { it.isNotEmpty() }?.toDoubleOrNull()
    }

    private fun parseOptionalDate(value: String): LocalDate? {
        return value.trim().takeIf { it.isNotEmpty() }?.let(WodlogDateUtils::parseDateOrNull)
    }

    private fun LocalDate?.trainingDaysOrZero(): Long {
        return this?.let { WodlogDateUtils.calculateTrainingDays(it, todayProvider()) } ?: 0L
    }

    private fun Double.toInputString(): String {
        return if (this % 1.0 == 0.0) {
            toLong().toString()
        } else {
            toString()
        }
    }
}
