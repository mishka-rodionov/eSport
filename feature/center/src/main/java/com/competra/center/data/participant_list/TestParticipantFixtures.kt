package com.competra.center.data.participant_list

import com.competra.domain.models.Gender

/**
 * Генератор тестовых участников для отладки.
 *
 * Используется только в debug-сборке для тестовых соревнований: кнопка
 * «Сгенерировать тестовых участников» на экране списка участников подставляет
 * по [DEFAULT_COUNT] участников в каждую пустую группу. Имена — вымышленные, но
 * различные между собой; пол имени подбирается под пол группы.
 */
object TestParticipantFixtures {

    /** Сколько участников генерировать на группу по умолчанию. */
    const val DEFAULT_COUNT = 5

    data class TestName(val firstName: String, val lastName: String)

    /**
     * Возвращает [count] различных вымышленных имён для пола [gender].
     *
     * @param seed Смещение комбинаций, чтобы имена не повторялись между группами
     *   (передавай индекс группы).
     */
    fun names(gender: Gender?, count: Int = DEFAULT_COUNT, seed: Int = 0): List<TestName> {
        val isFemale = gender == Gender.FEMALE
        val firstNames = if (isFemale) FEMALE_FIRST_NAMES else MALE_FIRST_NAMES
        return (0 until count).map { i ->
            val idx = seed * count + i
            val first = firstNames[idx % firstNames.size]
            // Женские фамилии образуем из мужских добавлением «а» (Иванов → Иванова).
            val baseLast = MALE_LAST_NAMES[(idx / firstNames.size) % MALE_LAST_NAMES.size]
            val last = if (isFemale) "${baseLast}а" else baseLast
            TestName(first, last)
        }
    }

    private val MALE_FIRST_NAMES = listOf(
        "Иван", "Пётр", "Алексей", "Дмитрий", "Сергей", "Николай", "Андрей",
        "Михаил", "Владимир", "Егор", "Артём", "Кирилл", "Роман", "Максим", "Денис"
    )

    private val FEMALE_FIRST_NAMES = listOf(
        "Анна", "Мария", "Елена", "Ольга", "Наталья", "Ирина", "Татьяна",
        "Светлана", "Юлия", "Дарья", "Екатерина", "Полина", "Ксения", "Алина", "Вера"
    )

    private val MALE_LAST_NAMES = listOf(
        "Иванов", "Петров", "Смирнов", "Кузнецов", "Попов", "Соколов", "Лебедев",
        "Козлов", "Новиков", "Морозов", "Волков", "Зайцев", "Павлов", "Семёнов", "Голубев"
    )
}
