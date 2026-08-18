package compose.project.leshy.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    /** Whether the user has been through the first-run collection picker at least once. */
    fun observeCollectionPickerCompleted(): Flow<Boolean>
    suspend fun setCollectionPickerCompleted()
}
