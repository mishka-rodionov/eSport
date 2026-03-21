package com.rodionov.center.presentation.orientiring_competition_create

import androidx.lifecycle.viewModelScope
import com.rodionov.center.data.creator.OrienteeringCreatorAction
import com.rodionov.center.data.creator.OrienteeringCreatorState
import com.rodionov.center.data.interactors.OrienteeringCompetitionInteractor
import com.rodionov.data.navigation.CenterNavigation
import com.rodionov.data.navigation.Navigation
import com.rodionov.domain.models.orienteering.OrienteeringCompetition
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.orienteering.Distance
import com.rodionov.domain.repository.user.UserRepository
import com.rodionov.resources.R
import com.rodionov.resources.ResourceProvider
import com.rodionov.ui.BaseAction
import com.rodionov.ui.viewmodel.BaseViewModel
import com.rodionov.utils.DateTimeFormat
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
        when(action) {
            OrienteeringCreatorAction.ShowDistanceCreateDialog -> updateState { copy(isShowDistanceCreateDialog = true) }
            OrienteeringCreatorAction.HideDistanceCreateDialog -> updateState { copy(isShowDistanceCreateDialog = false) }
            is OrienteeringCreatorAction.CreateDistance -> {
                updateState {copy(distances = distances + action.distance, isShowDistanceCreateDialog = false)}
            }
        }
        // Оставляем пустую реализацию для совместимости с базовым классом, 
        // если действия будут добавлены позже.
    }

    /**
     * Инициализирует состояние данными существующего соревнования для редактирования.
     * 
     * @param competitionId Идентификатор соревнования.
     */
    fun initialize(competitionId: Long?) {
        if (competitionId == null || competitionId == 0L) return
        
        viewModelScope.launch {
            orienteeringCompetitionInteractor.getCompetitionWithDetails(competitionId)
                .onSuccess { details ->
                    val comp = details.competition
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
                            participantGroups = details.groupsWithParticipants.map { it.group }
                        )
                    }
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
                    orienteeringCompetitionInteractor.saveCompetition(competition, emptyList())
                } else {
                    // Обновление существующего
                    orienteeringCompetitionInteractor.updateCompetition(competition, stateValue.participantGroups)
                }
                
                // TODO: Получить ID созданного соревнования, если оно новое
                // Пока предполагаем, что навигация идет дальше
                val id = stateValue.competitionId ?: 1L // Заглушка для ID
                
                viewModelScope.launch(Dispatchers.Main) {
                    navigation.navigate(CenterNavigation.RegistrationCompetitionFieldRoute(competitionId = id))
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
            orienteeringCompetitionInteractor.updateCompetition(competition, stateValue.participantGroups)
            
            viewModelScope.launch(Dispatchers.Main) {
                navigation.navigate(CenterNavigation.OrganizatorCompetitionFieldRoute(competitionId = stateValue.competitionId ?: 1L))
            }
        }
    }

    /**
     * Сохраняет данные третьего шага (Организатор) и переходит к четвертому.
     */
    fun saveStepThree() {
        viewModelScope.launch(Dispatchers.IO) {
            val competition = stateValue.toOrienteeringCompetition(null)
            orienteeringCompetitionInteractor.updateCompetition(competition, stateValue.participantGroups)
            
            viewModelScope.launch(Dispatchers.Main) {
                navigation.navigate(CenterNavigation.CreateDistanceRoute(competitionId = stateValue.competitionId ?: 1L))
            }
        }
    }

    /**
     * Сохраняет данные четвертого шага (Дистанции) и переходит к пятому.
     */
    fun saveStepFour() {
        // Логика сохранения дистанций
        viewModelScope.launch(Dispatchers.Main) {
            navigation.navigate(CenterNavigation.CreateParticipantGroupRoute(competitionId = stateValue.competitionId ?: 1L))
        }
    }

    /**
     * Финальное сохранение и выход из мастера.
     */
    fun finishCreation() {
        viewModelScope.launch(Dispatchers.IO) {
            val competition = stateValue.toOrienteeringCompetition(null)
            orienteeringCompetitionInteractor.updateCompetition(competition, stateValue.participantGroups)
            
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
    fun updateMaxParticipants(max: String) = updateState { copy(maxParticipants = max.toIntOrNull()) }
    fun updateFeeAmount(amount: String) = updateState { copy(feeAmount = amount.toDoubleOrNull()) }
    fun updateRegulationUrl(url: String) = updateState { copy(regulationUrl = url) }
    
    fun updateMapUrl(url: String) = updateState { copy(mapUrl = url) }
    fun updateContactPhone(phone: String) = updateState { copy(contactPhone = phone) }
    fun updateContactEmail(email: String) = updateState { copy(contactEmail = email) }
    fun updateWebsite(site: String) = updateState { copy(website = site) }
}
