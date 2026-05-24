package com.competra.center.presentation.orientiring_competition_create

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.competra.analytics.AnalyticsEvent
import com.competra.analytics.AnalyticsTracker
import com.competra.center.data.creator.OrienteeringCreatorAction
import com.competra.center.data.creator.OrienteeringCreatorState
import com.competra.center.data.interactors.OrienteeringCompetitionInteractor
import com.competra.data.navigation.CenterNavigation
import com.competra.data.navigation.Navigation
import com.competra.domain.exception.NetworkException
import com.competra.domain.models.Coordinates
import com.competra.domain.models.NetworkErrorEvent
import com.competra.domain.models.orienteering.RegistrationEndMode
import com.competra.domain.models.user.User
import com.competra.domain.repository.LoadingRepository
import com.competra.domain.repository.NetworkErrorRepository
import com.competra.domain.repository.UploadRepository
import com.competra.domain.repository.user.UserRepository
import com.competra.resources.ResourceProvider
import com.competra.ui.BaseAction
import com.competra.ui.viewmodel.BaseViewModel
import com.competra.utils.DateTimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * ViewModel для управления процессом пошагового создания соревнования.
 * 
 * Обеспечивает сохранение данных на каждом этапе и навигацию между экранами мастера.
 */
class OrienteeringCreatorViewModel(
    val navigation: Navigation,
    private val resourceProvider: ResourceProvider,
    private val orienteeringCompetitionInteractor: OrienteeringCompetitionInteractor,
    private val userRepository: UserRepository,
    private val networkErrorRepository: NetworkErrorRepository,
    private val uploadRepository: UploadRepository,
    private val context: Context,
    private val loadingRepository: LoadingRepository,
    private val analytics: AnalyticsTracker,
) : BaseViewModel<OrienteeringCreatorState>(OrienteeringCreatorState()) {

    var user: User? = null

    init {
        viewModelScope.launch {
            user = userRepository.retrieveUser().getOrNull()
        }
    }


    override fun onAction(action: BaseAction) {
        when (action) {
            OrienteeringCreatorAction.ShowDistanceCreateDialog -> updateState {
                copy(
                    isShowDistanceCreateDialog = true,
                    editDistanceIndex = -1
                )
            }

            OrienteeringCreatorAction.HideDistanceCreateDialog -> updateState {
                copy(
                    isShowDistanceCreateDialog = false
                )
            }

            is OrienteeringCreatorAction.CreateDistance -> {
                val updatedDistances = stateValue.distances.toMutableList()
                if (action.index == -1) {
                    updatedDistances.add(action.distance)
                    viewModelScope.launch {
                        orienteeringCompetitionInteractor.saveDistance(action.distance, silent = true)
                            .onFailure { error ->
                                Log.e("OrienteeringCreatorVM", "saveDistance failed", error)
                                handleFailure(error)
                            }
                    }
                } else {
                    updatedDistances[action.index] = action.distance
                    viewModelScope.launch {
                        orienteeringCompetitionInteractor.updateDistance(action.distance, silent = true)
                            .onFailure { error ->
                                Log.e("OrienteeringCreatorVM", "updateDistance failed", error)
                                handleFailure(error)
                            }
                    }
                }
                updateState {
                    copy(
                        distances = updatedDistances,
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
                    isShowGroupCreateDialog = true,
                    editGroupIndex = -1
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
                if (action.index == -1) {
                    val updatedGroups = stateValue.participantGroups.toMutableList().apply { add(action.participantGroup) }
                    updateState { copy(participantGroups = updatedGroups, isShowGroupCreateDialog = false) }
                    viewModelScope.launch {
                        orienteeringCompetitionInteractor.localSaveParticipantGroups(
                            participantGroups = listOf(action.participantGroup),
                            silent = true
                        )
                        // Перезагружаем группы из БД, чтобы получить реальный groupId
                        // вместо 0L, который приходит из диалога для новых групп.
                        // Без этого updateParticipantsGroups в finishCreation вставит группу повторно.
                        stateValue.competitionId?.let { compId ->
                            orienteeringCompetitionInteractor.getCompetitionWithDetails(compId)
                                .getOrNull()
                                ?.groupsWithParticipants
                                ?.map { it.group }
                                ?.let { freshGroups -> updateState { copy(participantGroups = freshGroups) } }
                        }
                    }
                } else {
                    val updatedGroups = stateValue.participantGroups.toMutableList().apply { set(action.index, action.participantGroup) }
                    updateState { copy(participantGroups = updatedGroups, isShowGroupCreateDialog = false) }
                    viewModelScope.launch {
                        orienteeringCompetitionInteractor.updateParticipantGroup(action.participantGroup, silent = true)
                    }
                }
            }

            is OrienteeringCreatorAction.UpdateCompetitionDate -> {
                updateStartDate(action.competitionDate)
            }

            is OrienteeringCreatorAction.UpdateCompetitionTime -> {
                val combined = DateTimeFormat.updateTimeInTimestamp(stateValue.startDate, action.competitionTime, competitionZone())
                updateState { copy(startTimeStr = action.competitionTime, startDate = combined ?: stateValue.startDate) }
            }

            is OrienteeringCreatorAction.UpdateTimeZone -> handleTimeZoneChange(action.zoneId)

            is OrienteeringCreatorAction.UpdateRegistrationStartDate -> {
                val combined = DateTimeFormat.updateTimeInTimestamp(action.date, stateValue.registrationStartTimeStr, competitionZone()) ?: action.date
                updateState { copy(registrationStart = combined, errors = errors.copy(isEmptyRegistrationStart = false)) }
            }

            is OrienteeringCreatorAction.UpdateRegistrationStartTime -> {
                val combined = DateTimeFormat.updateTimeInTimestamp(stateValue.registrationStart, action.time, competitionZone())
                updateState { copy(registrationStartTimeStr = action.time, registrationStart = combined ?: stateValue.registrationStart) }
            }

            is OrienteeringCreatorAction.UpdateRegistrationStartOnCreate -> updateState {
                copy(
                    registrationStartOnCreate = action.enabled,
                    errors = errors.copy(isEmptyRegistrationStart = false)
                )
            }

            is OrienteeringCreatorAction.UpdateRegistrationEndDate -> {
                val combined = DateTimeFormat.updateTimeInTimestamp(action.date, stateValue.registrationEndTimeStr, competitionZone()) ?: action.date
                updateState { copy(registrationEnd = combined, errors = errors.copy(isEmptyRegistrationEnd = false)) }
            }

            is OrienteeringCreatorAction.UpdateRegistrationEndTime -> {
                val combined = DateTimeFormat.updateTimeInTimestamp(stateValue.registrationEnd, action.time, competitionZone())
                updateState { copy(registrationEndTimeStr = action.time, registrationEnd = combined ?: stateValue.registrationEnd) }
            }

            is OrienteeringCreatorAction.UpdateRegistrationEndMode -> updateState {
                copy(
                    registrationEndMode = action.mode,
                    errors = errors.copy(isEmptyRegistrationEnd = false)
                )
            }

            is OrienteeringCreatorAction.UpdateCompetitionDirection -> updateState {
                copy(competitionDirection = action.direction)
            }

            is OrienteeringCreatorAction.UpdateStartTimeMode -> updateState {
                copy(startTimeMode = action.startTimeMode)
            }

            is OrienteeringCreatorAction.UpdatePunchingSystem -> updateState {
                copy(punchingSystem = action.punchingSystem)
            }

            is OrienteeringCreatorAction.UpdateStartInterval -> updateState {
                copy(startIntervalSeconds = action.seconds)
            }

            is OrienteeringCreatorAction.EditDistanceDialog -> updateState {
                copy(isShowDistanceCreateDialog = true, editDistanceIndex = action.index)
            }

            is OrienteeringCreatorAction.EditGroupDialog -> updateState {
                copy(isShowGroupCreateDialog = true, editGroupIndex = action.index)
            }

            is OrienteeringCreatorAction.UpdateCoordinates -> updateState {
                copy(coordinates = Coordinates(action.latitude, action.longitude))
            }

            is OrienteeringCreatorAction.UploadCompetitionImage -> uploadFile(
                uri = action.uri,
                type = "competition_image",
                onSuccess = { url -> updateState { copy(imageUrl = url) } }
            )

            is OrienteeringCreatorAction.UploadCompetitionMap -> uploadFile(
                uri = action.uri,
                type = "competition_map",
                onSuccess = { url -> updateState { copy(mapUrl = url) } }
            )
        }
    }

    /**
     * Инициализирует состояние данными существующего соревнования для редактирования.
     * Загружает детали соревнования, список дистанций и список групп.
     * 
     * @param competitionId Идентификатор соревнования.
     */
    fun initialize(competitionId: Long?) {
        if (competitionId == null) return

        viewModelScope.launch {
            if (user == null) {
                user = userRepository.retrieveUser().getOrNull()
            }

            // Загрузка основных деталей соревнования
            val comp =
                orienteeringCompetitionInteractor.getCompetition(competitionId) ?: return@launch

            val loadedZone = runCatching { ZoneId.of(comp.competition.timeZoneId) }
                .getOrDefault(ZoneId.systemDefault())
            updateState {
                copy(
                    competitionId = competitionId,
                    remoteCompetitionId = comp.competition.remoteId,
                    title = comp.competition.title,
                    imageUrl = comp.competition.imageUrl,
                    timeZoneId = comp.competition.timeZoneId,
                    startDate = comp.competition.startDate,
                    startTimeStr = DateTimeFormat.transformLongToTime(comp.competition.startDate, loadedZone),
                    endDate = comp.competition.endDate,
                    kindOfSport = comp.competition.kindOfSport,
                    description = comp.competition.description ?: "",
                    address = comp.competition.address ?: "",
                    coordinates = if (isCoordinatesSetByUser) coordinates
                                 else comp.competition.coordinates ?: coordinates,
                    registrationStart = comp.competition.registrationStart,
                    registrationStartTimeStr = DateTimeFormat.transformLongToTime(comp.competition.registrationStart, loadedZone).ifEmpty { "10:00" },
                    registrationStartOnCreate = comp.competition.registrationStart == null,
                    registrationEnd = comp.competition.registrationEnd,
                    registrationEndTimeStr = DateTimeFormat.transformLongToTime(comp.competition.registrationEnd, loadedZone).ifEmpty { "23:59" },
                    registrationEndMode = if (comp.competition.registrationEnd != null &&
                        comp.competition.registrationEnd == comp.competition.startDate - 24L * 60 * 60 * 1000
                    ) {
                        RegistrationEndMode.DAY_BEFORE_START
                    } else {
                        RegistrationEndMode.AT_COMPETITION_START
                    },
                    maxParticipants = comp.competition.maxParticipants,
                    isFeeEnabled = comp.competition.feeAmount != null,
                    feeAmount = comp.competition.feeAmount,
                    feeCurrency = comp.competition.feeCurrency ?: "RUB",
                    regulationUrl = comp.competition.regulationUrl ?: "",
                    mapUrl = comp.competition.mapUrl ?: "",
                    contactPhone = comp.competition.contactPhone?.ifEmpty { user?.phoneNumber ?: "" } ?: user?.phoneNumber ?: "",
                    contactEmail = comp.competition.contactEmail?.ifEmpty { user?.email ?: "" } ?: user?.email ?: "",
                    website = comp.competition.website ?: "",
                    competitionDirection = comp.direction,
                    punchingSystem = comp.punchingSystem,
                    startTimeMode = comp.startTimeMode,
                    countdownTimer = comp.countdownTimer,
                    startIntervalSeconds = comp.startIntervalSeconds,
                )
            }
            
            // Синхронизируем дистанции и группы с сервером (если соревнование опубликовано)
            val remoteId = comp.competition.remoteId
            if (remoteId != null) {
                orienteeringCompetitionInteractor.fetchAndSyncFromServer(remoteId, competitionId)
            }

            // Загрузка дистанций из локальной БД (уже актуальных после синхронизации)
            orienteeringCompetitionInteractor.getDistances(competitionId).onSuccess { list ->
                updateState { copy(distances = list) }
            }

            // Загрузка групп из локальной БД (уже актуальных после синхронизации)
            orienteeringCompetitionInteractor.getCompetitionWithDetails(competitionId).onSuccess { details ->
                updateState {
                    copy(participantGroups = details.groupsWithParticipants.map { it.group })
                }
            }
        }
    }

    /**
     * Сохраняет данные первого шага (Общая информация) и переходит ко второму.
     */
    fun saveStepOne() {
        val isNew = stateValue.competitionId == null
        if (isNew) analytics.trackEvent(AnalyticsEvent.CreateCompetitionStarted(KIND_ORIENTEERING))
        viewModelScope.launch(Dispatchers.IO) {
            loadingRepository.emit(true)
            val competition = stateValue.toOrienteeringCompetition(user?.id)
            val result = if (isNew) {
                // Создание нового
                orienteeringCompetitionInteractor.saveCompetitionNew(competition, silent = true)
            } else {
                // Обновление существующего
                orienteeringCompetitionInteractor.updateCompetitionNew(competition, silent = true)
            }
            result.onSuccess {
                val id = it.localCompetitionId
                val remoteId = it.competition.remoteId
                updateState {
                    copy(
                        competitionId = id,
                        remoteCompetitionId = remoteId ?: remoteCompetitionId
                    )
                }
                analytics.trackEvent(
                    AnalyticsEvent.CreateCompetitionStepCompleted(AnalyticsEvent.CreateCompetitionStep.COMMON)
                )
                viewModelScope.launch(Dispatchers.Main) {
                    navigation.navigate(
                        CenterNavigation.RegistrationCompetitionFieldRoute(
                            competitionId = id
                        )
                    )
                }
            }
                .onFailure {
                    handleFailure(it)
                }
            loadingRepository.emit(false)
        }
    }

    /**
     * Сохраняет данные второго шага (Регистрация) и переходит к третьему.
     * Перед сохранением валидирует поля регистрации и вычисляет итоговые значения
     * в зависимости от состояния свитчей.
     */
    fun saveStepTwo() {
        val startEmpty = !stateValue.registrationStartOnCreate && stateValue.registrationStart == null
        if (startEmpty) {
            updateState {
                copy(errors = errors.copy(isEmptyRegistrationStart = startEmpty))
            }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val actualRegistrationStart = if (stateValue.registrationStartOnCreate) null else stateValue.registrationStart
            val actualRegistrationEnd = when (stateValue.registrationEndMode) {
                RegistrationEndMode.DAY_BEFORE_START -> stateValue.startDate - 24L * 60 * 60 * 1000
                RegistrationEndMode.AT_COMPETITION_START -> stateValue.startDate
            }

            val competition = stateValue.copy(
                registrationStart = actualRegistrationStart,
                registrationEnd = actualRegistrationEnd
            ).toOrienteeringCompetition(user?.id)

            orienteeringCompetitionInteractor.localUpdate(
                competition,
                stateValue.participantGroups,
                silent = true
            )

            analytics.trackEvent(
                AnalyticsEvent.CreateCompetitionStepCompleted(AnalyticsEvent.CreateCompetitionStep.REGISTRATION)
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
     * Валидирует обязательное поле контактного телефона.
     */
    fun saveStepThree() {
        if (stateValue.contactPhone.isBlank()) {
            updateState { copy(errors = errors.copy(isEmptyContactPhone = true)) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val competition = stateValue.toOrienteeringCompetition(user?.id)
            orienteeringCompetitionInteractor.localUpdate(
                competition,
                stateValue.participantGroups,
                silent = true
            )

            analytics.trackEvent(
                AnalyticsEvent.CreateCompetitionStepCompleted(AnalyticsEvent.CreateCompetitionStep.ORGANIZATOR)
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
        analytics.trackEvent(
            AnalyticsEvent.CreateCompetitionStepCompleted(AnalyticsEvent.CreateCompetitionStep.DISTANCE)
        )
        viewModelScope.launch(Dispatchers.Main) {
            navigation.navigate(
                CenterNavigation.CreateParticipantGroupRoute(
                    competitionId = stateValue.competitionId ?: 1L
                )
            )
        }
    }

    /**
     * Финальное сохранение, публикация на сервере и выход из мастера.
     * Обновляет локальные данные, затем отправляет соревнование на сервер.
     * Навигация выполняется вне зависимости от результата серверного запроса —
     * данные уже сохранены локально.
     * Выполняет переход на главный экран раздела "Центр" с очисткой навигационного стека.
     */
    fun finishCreation() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            loadingRepository.emit(true)
            val competition = stateValue.toOrienteeringCompetition(user?.id)
            val groups = stateValue.participantGroups

            orienteeringCompetitionInteractor.localUpdate(
                competition,
                groups
            )

            // Перезагружаем группы из БД, чтобы получить актуальные локальные ID
            // (новые группы с groupId=0 получают реальный ID при вставке)
            val freshGroups = stateValue.competitionId?.let { id ->
                orienteeringCompetitionInteractor.getCompetitionWithDetails(id)
                    .getOrNull()?.groupsWithParticipants?.map { it.group }
            } ?: groups

            analytics.trackEvent(
                AnalyticsEvent.CreateCompetitionStepCompleted(AnalyticsEvent.CreateCompetitionStep.GROUPS)
            )
            orienteeringCompetitionInteractor.publishCompetitionToServer(competition)
                .onSuccess { serverCompetition ->
                    analytics.trackEvent(
                        AnalyticsEvent.CreateCompetitionFinished(
                            competitionId = serverCompetition.competition.remoteId ?: competition.localCompetitionId,
                            kindOfSport = KIND_ORIENTEERING,
                        )
                    )
                    serverCompetition.competition.remoteId?.let { remoteId ->
                        // Загружаем дистанции из локальной БД и помечаем их unsynced —
                        // worker сам выгрузит их и проставит remoteId.
                        val distances = orienteeringCompetitionInteractor
                            .getDistances(competition.localCompetitionId)
                            .getOrDefault(emptyList())
                        if (distances.isNotEmpty()) {
                            orienteeringCompetitionInteractor.publishDistancesToServer(
                                remoteCompetitionId = remoteId,
                                localCompetitionId = competition.localCompetitionId,
                                distances = distances
                            )
                        }

                        // Группы передаём с ЛОКАЛЬНЫМ distanceId — маппинг local→remote
                        // делает SyncOrchestrator.syncGroups перед отправкой. Если подменять
                        // distanceId здесь, worker не найдёт дистанцию по локальному PK и
                        // отфильтрует группу как «не готовую к синку» (см. SyncOrchestrator
                        // строки 275-278) — запрос на сервер просто не уйдёт.
                        orienteeringCompetitionInteractor.publishGroupsToServer(remoteId, freshGroups)
                    }
                }
                .onFailure { error ->
                    updateState { copy(error = error.message) }
                    handleFailure(error)
                }

            updateState { copy(isLoading = false) }
            loadingRepository.emit(false)

            viewModelScope.launch(Dispatchers.Main) {
                val destination = CenterNavigation.CenterRoute
                destination.navOptionsBuilder = {
                    popUpTo(CenterNavigation.CenterRoute) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
                navigation.navigate(destination)
            }
        }
    }

    fun back() {
        viewModelScope.launch(Dispatchers.Main) {
            navigation.back()
        }
    }

    private fun handleFailure(throwable: Throwable) {
        viewModelScope.launch {
            val code = (throwable as? NetworkException)?.code
            networkErrorRepository.emit(NetworkErrorEvent(code = code, message = throwable.message))
        }
    }

    fun updateTitle(title: String) = updateState { copy(title = title) }
    fun updateAddress(address: String) = updateState { copy(address = address) }
    fun updateDescription(description: String) = updateState { copy(description = description) }
    fun updateCoordinates(lat: Double, lon: Double) = updateState {
        copy(coordinates = Coordinates(lat, lon), isCoordinatesSetByUser = true)
    }

    private fun uploadFile(
        uri: android.net.Uri,
        type: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingRepository.emit(true)
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: run {
                loadingRepository.emit(false)
                return@launch
            }
            val fileName = uri.lastPathSegment ?: "file"
            uploadRepository.uploadFile(bytes, fileName, type)
                .onSuccess { url -> onSuccess(url) }
                .onFailure { handleFailure(it) }
            loadingRepository.emit(false)
        }
    }

    /** Открывает экран выбора координат на карте. */
    fun openMapPicker() {
        viewModelScope.launch(Dispatchers.Main) {
            navigation.navigate(
                CenterNavigation.MapPickerRoute(
                    initLatE6 = (stateValue.coordinates.latitude * 1_000_000).toLong(),
                    initLonE6 = (stateValue.coordinates.longitude * 1_000_000).toLong()
                )
            )
        }
    }
    
    fun updateStartDate(date: Long) {
        val updatedTimestamp = DateTimeFormat.updateTimeInTimestamp(date, stateValue.startTimeStr, competitionZone()) ?: date
        updateState { copy(startDate = updatedTimestamp) }
    }

    private fun competitionZone(): ZoneId =
        runCatching { ZoneId.of(stateValue.timeZoneId) }.getOrDefault(ZoneId.systemDefault())

    /**
     * Меняет часовой пояс соревнования и пересчитывает все timestamp'ы так,
     * чтобы локально-отображаемое время (HH:mm, выбранная дата) осталось тем же.
     * Поведение: пользователь видит «10:00 1 июня» — после смены на NSK тот же «10:00 1 июня»
     * по новосибирскому времени.
     */
    private fun handleTimeZoneChange(newZoneIdRaw: String) {
        val newZone = runCatching { ZoneId.of(newZoneIdRaw) }.getOrNull() ?: return
        val oldZone = competitionZone()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        fun recompute(ts: Long?, timeStr: String): Long? {
            if (ts == null || ts == 0L) return ts
            val localDate = java.time.Instant.ofEpochMilli(ts).atZone(oldZone).toLocalDate()
            val localTime = runCatching { LocalTime.parse(timeStr, timeFormatter) }
                .getOrNull() ?: java.time.Instant.ofEpochMilli(ts).atZone(oldZone).toLocalTime()
            return localDate.atTime(localTime).atZone(newZone).toInstant().toEpochMilli()
        }

        updateState {
            copy(
                timeZoneId = newZoneIdRaw,
                startDate = recompute(startDate, startTimeStr) ?: startDate,
                registrationStart = recompute(registrationStart, registrationStartTimeStr),
                registrationEnd = recompute(registrationEnd, registrationEndTimeStr)
            )
        }
    }

    fun updateEndDate(date: Long?) = updateState { copy(endDate = date) }

    fun updateRegistrationStart(date: Long?) = updateState { copy(registrationStart = date) }
    fun updateRegistrationEnd(date: Long?) = updateState { copy(registrationEnd = date) }
    fun updateMaxParticipants(max: String) =
        updateState { copy(maxParticipants = max.toIntOrNull()) }

    /**
     * Переключает возможность ввода взноса.
     * @param enabled true, если взнос включен.
     */
    fun updateFeeEnabled(enabled: Boolean) = updateState { copy(isFeeEnabled = enabled) }

    fun updateFeeAmount(amount: String) = updateState { copy(feeAmount = amount.toDoubleOrNull()) }
    fun updateRegulationUrl(url: String) = updateState { copy(regulationUrl = url) }

    fun updateMapUrl(url: String) = updateState { copy(mapUrl = url) }
    fun updateContactPhone(phone: String) = updateState { copy(contactPhone = phone, errors = errors.copy(isEmptyContactPhone = false)) }
    fun updateContactEmail(email: String) = updateState { copy(contactEmail = email) }
    fun updateWebsite(site: String) = updateState { copy(website = site) }

    private companion object {
        const val KIND_ORIENTEERING = "orienteering"
    }
}
