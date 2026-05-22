package com.competra.center.data.creator

import android.net.Uri
import com.competra.domain.models.orienteering.OrienteeringDirection
import com.competra.domain.models.ParticipantGroup
import com.competra.domain.models.orienteering.PunchingSystem
import com.competra.domain.models.orienteering.RegistrationEndMode
import com.competra.domain.models.orienteering.StartTimeMode
import com.competra.domain.models.orienteering.Distance
import com.competra.ui.BaseAction
import java.time.LocalDate

/**
 * Действия на экране создания соревнования.
 */
sealed class OrienteeringCreatorAction : BaseAction {
    data class CreateParticipantGroup(
        val participantGroup: ParticipantGroup,
        val index: Int
    ): OrienteeringCreatorAction()

    /**
     * Сохранение созданного соревнования
     * */
    data object Apply: OrienteeringCreatorAction()
    data class UpdateCompetitionDate(val competitionDate: Long): OrienteeringCreatorAction()
    data class UpdateCompetitionTime(val competitionTime: String): OrienteeringCreatorAction()
    /**
     * Смена часового пояса соревнования. Локальное «отображаемое» время (HH:mm в полях формы)
     * остаётся неизменным — пересчитываются epoch-таймстампы под новый пояс.
     */
    data class UpdateTimeZone(val zoneId: String): OrienteeringCreatorAction()
    data object ShowGroupCreateDialog: OrienteeringCreatorAction()
    data object HideGroupCreateDialog: OrienteeringCreatorAction()
    data class EditGroupDialog(val index: Int): OrienteeringCreatorAction()
    data class DeleteGroup(val index: Int): OrienteeringCreatorAction()
    data class UpdateCompetitionDirection(val direction: OrienteeringDirection): OrienteeringCreatorAction()
    data object SuccessfulCompetitionCreate: OrienteeringCreatorAction()
    data class FailedCompetitionCreate(val message: String): OrienteeringCreatorAction()
    data class UpdateStartTimeMode(val startTimeMode: StartTimeMode): OrienteeringCreatorAction()
    data class UpdatePunchingSystem(val punchingSystem: PunchingSystem): OrienteeringCreatorAction()
    data class UpdateStartInterval(val seconds: Int?): OrienteeringCreatorAction()
    
    // Действия для регистрации
    data class UpdateRegistrationStartDate(val date: Long) : OrienteeringCreatorAction()
    data class UpdateRegistrationStartTime(val time: String) : OrienteeringCreatorAction()
    data class UpdateRegistrationStartOnCreate(val enabled: Boolean) : OrienteeringCreatorAction()
    data class UpdateRegistrationEndDate(val date: Long) : OrienteeringCreatorAction()
    data class UpdateRegistrationEndTime(val time: String) : OrienteeringCreatorAction()
    data class UpdateRegistrationEndMode(val mode: RegistrationEndMode) : OrienteeringCreatorAction()

    // Новые действия для пошагового создания
    data object AddStage : OrienteeringCreatorAction()
    data class RemoveStage(val index: Int) : OrienteeringCreatorAction()
    data class UpdateStageDate(val index: Int, val date: Long) : OrienteeringCreatorAction()
    data class UpdateStageTime(val index: Int, val time: String) : OrienteeringCreatorAction()

    // Обновление координат места старта
    data class UpdateCoordinates(val latitude: Double, val longitude: Double) : OrienteeringCreatorAction()

    // Загрузка изображений
    data class UploadCompetitionImage(val uri: Uri) : OrienteeringCreatorAction()
    data class UploadCompetitionMap(val uri: Uri) : OrienteeringCreatorAction()

    // Действия для работы с дистанциями
    data object ShowDistanceCreateDialog : OrienteeringCreatorAction()
    data object HideDistanceCreateDialog : OrienteeringCreatorAction()
    data class CreateDistance(val distance: Distance, val index: Int) : OrienteeringCreatorAction()
    data class EditDistanceDialog(val index: Int) : OrienteeringCreatorAction()
    data class DeleteDistance(val index: Int) : OrienteeringCreatorAction()
}
