package com.rodionov.domain.models.orienteering

import com.rodionov.domain.models.Participant

/**
 * Участник соревнований по спортивному ориентированию.
 *
 * Представляет конкретного участника в рамках конкретных соревнований.
 * Содержит как общие данные участника, так и специфичные для спортивного ориентирования.
 *
 * @property id уникальный идентификатор записи участника-соревнования
 * @property userId идентификатор пользователя в системе
 * @property firstName имя участника
 * @property lastName фамилия участника
 * @property groupId идентификатор возрастной/спортивной группы
 *     Определяет категорию участника (например, "М18", "Ж21")
 * @property competitionId идентификатор соревнования
 *     Связывает участника с конкретными соревнованиями
 * @property commandName название команды/клуба
 *     Может быть пустым для индивидуальных участников
 * @property startNumber стартовый номер участника
 *     Уникален в рамках одного соревнования
 * @property chipNumber номер электронного чипа для отметки на КП
 *     Используется для электронной отметки на контрольных пунктах
 * @property comment дополнительный комментарий
 *     Может содержать информацию о разряде, статусе или особые отметки
 *
 * @constructor Создает участника соревнований по спортивному ориентированию.
 *
 * @throws IllegalArgumentException если [startNumber] пустой или [groupId] отрицательный
 *
 * @see Participant базовый интерфейс участника
 * @see CompetitionMappers маппер моделей
 *
 * @author Mikhail Rodionov
 * @since 1.0
 */
data class OrienteeringParticipant(
    override val id: Long,
    override val userId: String,
    val firstName: String,
    val lastName: String,
    val groupId: Long,
    val competitionId: Long,
    val commandName: String,
    val startNumber: String,
    val startTime: String,
    val chipNumber: String,
    val comment: String
): Participant