package com.competra.rating.presentation.form

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.RatingNavigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.Gender
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.models.rating.RatingGroup
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.rating.RatingRepository
import com.competra.rating.data.form.RatingFormAction
import com.competra.rating.data.form.RatingFormState
import com.competra.rating.data.form.RatingGroupDraft
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class RatingFormViewModel(
    private val ratingRepository: RatingRepository,
    private val navigation: Navigation,
    private val networkErrorRepository: NetworkErrorRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<RatingFormState>(RatingFormState()) {

    private var nextLocalId = 0

    fun initialize(clubId: String) {
        if (stateValue.clubId == clubId) return
        updateState { copy(clubId = clubId) }
    }

    override fun onAction(action: BaseAction) {
        when (action) {
            is RatingFormAction.NameChanged -> updateState { copy(name = action.name, nameError = false) }
            is RatingFormAction.AddGroupClick -> {
                updateState { copy(groups = groups + RatingGroupDraft(localId = nextLocalId++), groupsError = false) }
            }
            is RatingFormAction.RemoveGroupClick -> {
                updateState { copy(groups = groups.filterNot { it.localId == action.localId }) }
            }
            is RatingFormAction.GroupTitleChanged -> updateGroup(action.localId) { copy(title = action.title) }
            is RatingFormAction.GroupGenderChanged -> updateGroup(action.localId) { copy(gender = action.gender) }
            is RatingFormAction.GroupMinAgeChanged -> updateGroup(action.localId) { copy(minAge = action.minAge) }
            is RatingFormAction.GroupMaxAgeChanged -> updateGroup(action.localId) { copy(maxAge = action.maxAge) }
            is RatingFormAction.Save -> save()
        }
    }

    private fun updateGroup(localId: Int, transform: RatingGroupDraft.() -> RatingGroupDraft) {
        updateState { copy(groups = groups.map { if (it.localId == localId) it.transform() else it }) }
    }

    private fun save() {
        val name = stateValue.name.trim()
        if (name.isBlank()) {
            updateState { copy(nameError = true) }
            return
        }
        if (stateValue.groups.isEmpty()) {
            updateState { copy(groupsError = true) }
            return
        }

        val groups = stateValue.groups.mapIndexed { index, draft ->
            RatingGroup(
                id = 0L,
                ratingId = "",
                title = draft.title.trim().ifBlank { "Группа ${index + 1}" },
                gender = draft.gender,
                minAge = draft.minAge.toIntOrNull(),
                maxAge = draft.maxAge.toIntOrNull(),
                orderIndex = index
            )
        }

        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            ratingRepository.create(stateValue.clubId, name, groups)
                .onSuccess { rating ->
                    analytics.trackEvent(AnalyticsEvent.RatingCreated(rating.id, stateValue.clubId))
                    updateState { copy(isSaving = false) }
                    navigation.navigate(RatingNavigation.RatingDetailRoute(rating.id))
                }
                .onFailure { throwable ->
                    updateState { copy(isSaving = false) }
                    val code = (throwable as? NetworkException)?.code
                    networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
                }
        }
    }
}
