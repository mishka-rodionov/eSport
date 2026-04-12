package com.rodionov.events.presentation.event_results

import androidx.lifecycle.viewModelScope
import com.rodionov.domain.models.Gender
import com.rodionov.domain.models.ParticipantGroup
import com.rodionov.domain.models.ResultStatus
import com.rodionov.domain.models.orienteering.GroupWithParticipantsAndResults
import com.rodionov.domain.models.orienteering.OrienteeringParticipant
import com.rodionov.domain.models.orienteering.OrienteeringResult
import com.rodionov.domain.models.orienteering.ParticipantWithResult
import com.rodionov.ui.BaseAction
import com.rodionov.ui.BaseState
import com.rodionov.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Состояние экрана результатов события.
 *
 * @property isLoading Флаг загрузки данных.
 * @property groupsWithResults Список групп с результатами участников.
 */
data class EventResultsState(
    val isLoading: Boolean = false,
    val groupsWithResults: List<GroupWithParticipantsAndResults> = emptyList()
) : BaseState

/**
 * ViewModel для экрана результатов события.
 * Отвечает за загрузку данных результатов по идентификатору события.
 */
class EventResultsViewModel : BaseViewModel<EventResultsState>(EventResultsState()) {

    override fun onAction(action: BaseAction) {}

    /**
     * Загружает результаты события.
     * Используются моковые данные для имитации сетевого запроса.
     *
     * @param eventId Идентификатор события.
     */
    fun loadResults(eventId: Long) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            // Имитация задержки сети
            delay(1000)

            val mockData = listOf(
                createMockGroup(1, "М21"),
                createMockGroup(2, "Ж21"),
                createMockGroup(3, "Open")
            )
            
            updateState { 
                copy(
                    isLoading = false,
                    groupsWithResults = mockData
                )
            }
        }
    }

    /**
     * Создает моковые данные для группы.
     */
    private fun createMockGroup(id: Long, title: String): GroupWithParticipantsAndResults {
        return GroupWithParticipantsAndResults(
            group = ParticipantGroup(
                groupId = id,
                competitionId = 0L,
                title = title,
                gender = if (title.startsWith("М")) Gender.MALE else if (title.startsWith("Ж")) Gender.FEMALE else Gender.MIXED,
                minAge = if (title.contains("21")) 21 else 0,
                maxAge = null,
                distanceId = id, // Ссылка на дистанцию
                maxParticipants = 100,
                isSynced = true,
                lastModified = System.currentTimeMillis()
            ),
            participants = listOf(
                ParticipantWithResult(
                    participant = OrienteeringParticipant(
                        id = 1,
                        userId = "user_1",
                        firstName = "Иван",
                        lastName = "Иванов",
                        groupId = id,
                        groupName = title,
                        competitionId = 0L,
                        commandName = "Команда А",
                        startNumber = "101",
                        startTime = 0L,
                        chipNumber = "12345",
                        comment = "",
                        isChipGiven = true
                    ),
                    result = OrienteeringResult(
                        id = 1,
                        competitionId = 0L,
                        groupId = id,
                        participantId = 1,
                        totalTime = 1800,
                        rank = 1,
                        status = ResultStatus.FINISHED
                    )
                ),
                ParticipantWithResult(
                    participant = OrienteeringParticipant(
                        id = 2,
                        userId = "user_2",
                        firstName = "Петр",
                        lastName = "Петров",
                        groupId = id,
                        groupName = title,
                        competitionId = 0L,
                        commandName = "Команда Б",
                        startNumber = "102",
                        startTime = 120L,
                        chipNumber = "54321",
                        comment = "",
                        isChipGiven = true
                    ),
                    result = OrienteeringResult(
                        id = 2,
                        competitionId = 0L,
                        groupId = id,
                        participantId = 2,
                        totalTime = 1950,
                        rank = 2,
                        status = ResultStatus.FINISHED
                    )
                )
            )
        )
    }
}
