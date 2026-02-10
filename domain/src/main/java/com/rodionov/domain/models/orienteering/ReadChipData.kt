package com.rodionov.domain.models.orienteering

/**
 * Результат чтения данных с электронного чипа участника.
 *
 * Используется для разделения разных типов информации,
 * получаемых от станции чтения.
 */
sealed class ReadChipData {

    /**
     * "Сырые" данные результата участника.
     *
     * @property chipNumber номер чипа.
     * @property clearTime время очистки чипа на станции очистки.
     * @property splits список отметок на контрольных пунктах.
     */
    data class RawResult(
        val chipNumber: Int,
        val clearTime: Long,
        val splits: List<SplitTime>
    ): ReadChipData()

    /**
     * Данные мастер-чипа, используемого для настройки,
     * синхронизации или управления оборудованием.
     *
     * @property data строковое представление служебных данных.
     */
    data class MasterChipData(val data: String): ReadChipData()

}
