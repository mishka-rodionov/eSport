package com.competra.profile.presentation.auth_code

import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.data.navigation.Navigation
import com.competra.data.navigation.PendingRegistrationRepository
import com.competra.data.navigation.ProfileNavigation
import com.competra.data.navigation.TabRoutes
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.profile.data.auth.AuthAction
import com.competra.profile.data.auth.OtpState
import com.competra.profile.data.interactors.AuthInteractor
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthCodeViewModel(
    private val authInteractor: AuthInteractor,
    private val navigation: Navigation,
    private val pendingRegistrationRepository: PendingRegistrationRepository,
    private val networkErrorRepository: NetworkErrorRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<OtpState>(OtpState()) {

    private var email = ""

    override fun onAction(action: BaseAction) {
        when (action) {
            is AuthAction.AuthCodeEntered -> sendAuthCode(action.code)
            else -> {}
        }
    }

    fun initialize(userEmail: String) {
        email = userEmail
    }

    fun sendAuthCode(code: String) {
        if (stateValue.isLoading) return

        analytics.trackEvent(AnalyticsEvent.AuthCodeSubmitted)
        updateState { copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            authInteractor.authorize(email = email, code = code).onSuccess {
                updateState { copy(isLoading = false) }
                analytics.trackEvent(AnalyticsEvent.AuthLoginSuccess)
                if (pendingRegistrationRepository.pending.value != null) {
                    navigation.switchTab(TabRoutes.EVENTS)
                } else {
                    navigation.navigate(ProfileNavigation.MainProfileRoute)
                }
            }.onFailure {
                updateState { copy(isLoading = false) }
                analytics.trackEvent(AnalyticsEvent.AuthLoginFailed(it.toAuthFailureReason()))
                handleFailure(it)
            }
        }
    }

    private fun Throwable.toAuthFailureReason(): AnalyticsEvent.AuthFailureReason {
        val networkCode = (this as? NetworkException)?.code ?: return AnalyticsEvent.AuthFailureReason.UNKNOWN
        return when {
            networkCode in 400..499 -> AnalyticsEvent.AuthFailureReason.INVALID_CODE
            networkCode in 500..599 -> AnalyticsEvent.AuthFailureReason.SERVER
            else -> AnalyticsEvent.AuthFailureReason.NETWORK
        }
    }

    private fun handleFailure(throwable: Throwable) {
        viewModelScope.launch {
            val code = (throwable as? NetworkException)?.code
            networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
        }
    }

}
