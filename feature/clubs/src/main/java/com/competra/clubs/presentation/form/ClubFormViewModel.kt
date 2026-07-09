package com.competra.clubs.presentation.form

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.clubs.data.form.ClubFormAction
import com.competra.clubs.data.form.ClubFormState
import com.competra.data.navigation.ClubsNavigation
import com.competra.data.navigation.Navigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.clubs.ClubRepository
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class ClubFormViewModel(
    private val clubRepository: ClubRepository,
    private val navigation: Navigation,
    private val networkErrorRepository: NetworkErrorRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<ClubFormState>(ClubFormState()) {

    override fun onAction(action: BaseAction) {
        when (action) {
            is ClubFormAction.NameChanged -> {
                updateState { copy(name = action.name, nameError = false) }
            }
            is ClubFormAction.DescriptionChanged -> {
                updateState { copy(description = action.description) }
            }
            is ClubFormAction.AllowJoinRequestsChanged -> {
                updateState { copy(allowJoinRequests = action.allow) }
            }
            is ClubFormAction.Save -> save()
        }
    }

    private fun save() {
        if (stateValue.name.isBlank()) {
            updateState { copy(nameError = true) }
            return
        }
        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            clubRepository.create(
                name = stateValue.name.trim(),
                description = stateValue.description.trim().ifBlank { null },
                allowJoinRequests = stateValue.allowJoinRequests
            )
                .onSuccess { club ->
                    analytics.trackEvent(AnalyticsEvent.ClubCreated(club.id))
                    updateState { copy(isSaving = false) }
                    navigation.navigate(ClubsNavigation.ClubDetailRoute(club.id))
                }
                .onFailure { throwable ->
                    updateState { copy(isSaving = false) }
                    val code = (throwable as? NetworkException)?.code
                    networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
                }
        }
    }
}
