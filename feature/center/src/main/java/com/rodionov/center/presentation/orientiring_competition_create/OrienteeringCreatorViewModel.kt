package com.rodionov.center.presentation.orientiring_competition_create

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.center.data.creator.OrienteeringCreatorState
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.resources.ResourceProvider
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel для управления процессом пошагового создания соревнования.
 * 
 * Обеспечивает сохранение данных на каждом этапе и навигацию между экранами мастера.
 */
class OrienteeringCreatorViewModel(
    val navigation: Navigation,
    private val resourceProvider: ResourceProvider,
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    private val userRepository: UserRepository
) : BaseViewModel<OrienteeringCreatorState>(OrienteeringCreatorState()) {

    override fun onAction(action: BaseAction) {
        when (action) {
            OrienteeringCreatorAction.ShowDistanceCreateDialog -> updateState {
                copy(
                    isShowDistanceCreateDialog = true
                )
            }

            OrienteeringCreatorAction.HideDistanceCreateDialog -> updateState {
                copy(
                    isShowDistanceCreateDialog = false
                )
            }

            is OrienteeringCreatorAction.CreateDistance -> {
                updateState {
                    copy(
                        distances = distances + action.distance,
                        isShowDistanceCreateDialog = false
                    )
                }
            }

            /**
             * Обработка экшена показа диалога создания группы участников.
             * Устанавливает флаг isShowGroupCreateDialog в true.
             */
            OrienteeringCreatorAction.ShowGroupCreateDialog -> updateState {
                copy(
                    isShowGroupCreateDialog = true
                )
            }

            /**
             * Скрытие диалога создания группы.
             */
            OrienteeringCreatorAction.HideGroupCreateDialog -> updateState {
                copy(
                    isShowGroupCreateDialog = false
                )
            }

            /**
             * Обработка создания/добавления новой группы участников.
             * Добавляет группу в список и закрывает диалог.
             */
            is OrienteeringCreatorAction.CreateParticipantGroup -> {
                updateState {
                    copy(
                        participantGroups = participantGroups + action.participantGroup,
                        isShowGroupCreateDialog = false
                    )
                }
            }

            is OrienteeringCreatorAction.UpdateCompetitionDate -> {
                updateStartDate(action.competitionDate)
            }

            is OrienteeringCreatorAction.UpdateCompetitionTime -> updateState { copy(startTimeStr = action.competitionTime) }
        }
    }

    /**
     * Инициализирует состояние данными существующего соревнования для редактирования.
     * 
     * @param competitionId Идентификатор соревнования.
     */
    fun initialize(competitionId: Long?) {
        if (competitionId == null) return

        viewModelScope.launch {
            val comp = orienteeringCompetitionInteractor.getCompetition(competitionId) ?: return@launch

            updateState {
                copy(
                    competitionId = competitionId,
                    title = comp.competition.title,
                    startDate = comp.competition.startDate,
                    endDate = comp.competition.endDate,
                    kindOfSport = comp.competition.kindOfSport,
                    description = comp.competition.description ?: "",
                    address = comp.competition.address ?: "",
                    coordinates = comp.competition.coordinates ?: coordinates,
                    registrationStart = comp.competition.registrationStart,
                    registrationEnd = comp.competition.registrationEnd,
                    maxParticipants = comp.competition.maxParticipants,
                    feeAmount = comp.competition.feeAmount,
                    feeCurrency = comp.competition.feeCurrency ?: "RUB",
                    regulationUrl = comp.competition.regulationUrl ?: "",
                    mapUrl = comp.competition.mapUrl ?: "",
                    contactPhone = comp.competition.contactPhone ?: "",
                    contactEmail = comp.competition.contactEmail ?: "",
                    website = comp.competition.website ?: "",
                    competitionDirection = comp.direction,
                    punchingSystem = comp.punchingSystem,
                    startTimeMode = comp.startTimeMode,
                    countdownTimer = comp.countdownTimer,
                )
            }
        }
    }

    /**
     * Сохраняет данные первого шага (Общая информация) и переходит ко второму.
     */
    fun saveStepOne() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.retrieveUser().onSuccess { user ->
                val competition = stateValue.toOrienteeringCompetition(user.id.toLongOrNull())
                val result = if (stateValue.competitionId == null) {
                    // Создание нового
                    orienteeringCompetitionInteractor.saveCompetitionNew(competition)
                } else {
                    // Обновление существующего
                    orienteeringCompetitionInteractor.updateCompetitionNew(competition)
                }
                result.onSuccess {
                    val id = it.localCompetitionId
                    updateState { copy(competitionId = id) }
                    viewModelScope.launch(Dispatchers.Main) {
                        navigation.navigate(
                            CenterNavigation.RegistrationCompetitionFieldRoute(
                                competitionId = id
                            )
                        )
                    }
                }
                    .onFailure {
                        // TODO здесь будет обработка ошибки
                    }

            }
        }
    }

    /**
     * Сохраняет данные второго шага (Регистрация) и переходит к третьему.
     */
    fun saveStepTwo() {
        viewModelScope.launch(Dispatchers.IO) {
            val competition = stateValue.toOrienteeringCompetition(null)
            orienteeringCompetitionInteractor.updateCompetition(
                competition,
                stateValue.participantGroups
            )

            viewModelScope.launch(Dispatchers.Main) {
                navigation.navigate(
                    CenterNavigation.OrganizatorCompetitionFieldRoute(
                        competitionId = stateValue.competitionId ?: 1L
                    )
                )
            }
        }
    }

    /**
     * Сохраняет данные третьего шага (Организатор) и переходит к четвертому.
     */
    fun saveStepThree() {
        viewModelScope.launch(Dispatchers.IO) {
            val competition = stateValue.toOrienteeringCompetition(null)
            orienteeringCompetitionInteractor.updateCompetition(
                competition,
                stateValue.participantGroups
            )

            viewModelScope.launch(Dispatchers.Main) {
                navigation.navigate(
                    CenterNavigation.CreateDistanceRoute(
                        competitionId = stateValue.competitionId ?: 1L
                    )
                )
            }
        }
    }

    /**
     * Сохраняет данные четвертого шага (Дистанции) и переходит к пятому.
     */
    fun saveStepFour() {
        // Логика сохранения дистанций
        viewModelScope.launch(Dispatchers.Main) {
            navigation.navigate(
                CenterNavigation.CreateParticipantGroupRoute(
                    competitionId = stateValue.competitionId ?: 1L
                )
            )
        }
    }

    /**
     * Финальное сохранение и выход из мастера.
     */
    fun finishCreation() {
        viewModelScope.launch(Dispatchers.IO) {
            val competition = stateValue.toOrienteeringCompetition(null)
            orienteeringCompetitionInteractor.updateCompetition(
                competition,
                stateValue.participantGroups
            )

            viewModelScope.launch(Dispatchers.Main) {
                navigation.navigate(CenterNavigation.CenterRoute)
            }
        }
    }

    fun back() {
        viewModelScope.launch(Dispatchers.Main) {
            navigation.back()
        }
    }

    fun updateTitle(title: String) = updateState { copy(title = title) }
    fun updateAddress(address: String) = updateState { copy(address = address) }
    fun updateDescription(description: String) = updateState { copy(description = description) }
    fun updateStartDate(date: Long) = updateState { copy(startDate = date) }
    fun updateEndDate(date: Long?) = updateState { copy(endDate = date) }

    fun updateRegistrationStart(date: Long?) = updateState { copy(registrationStart = date) }
    fun updateRegistrationEnd(date: Long?) = updateState { copy(registrationEnd = date) }
    fun updateMaxParticipants(max: String) =
        updateState { copy(maxParticipants = max.toIntOrNull()) }

    fun updateFeeAmount(amount: String) = updateState { copy(feeAmount = amount.toDoubleOrNull()) }
    fun updateRegulationUrl(url: String) = updateState { copy(regulationUrl = url) }

    fun updateMapUrl(url: String) = updateState { copy(mapUrl = url) }
    fun updateContactPhone(phone: String) = updateState { copy(contactPhone = phone) }
    fun updateContactEmail(email: String) = updateState { copy(contactEmail = email) }
    fun updateWebsite(site: String) = updateState { copy(website = site) }
}
