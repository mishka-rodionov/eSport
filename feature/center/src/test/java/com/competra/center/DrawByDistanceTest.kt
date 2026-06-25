package com.competra.center

import com.competra.center.presentation.draw.drawParticipantsByDistance
import com.competra.domain.models.orienteering.OrienteeringParticipant
import com.competra.domain.models.orienteering.PunchingSystem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Проверка инвариантов жеребьёвки по дистанциям [drawParticipantsByDistance].
 */
class DrawByDistanceTest {

    private val startTime = 1_000_000L
    private val intervalMs = 60_000L

    private fun participant(id: Int, groupId: Long): OrienteeringParticipant =
        OrienteeringParticipant(
            id = id.toString(),
            userId = "u$id",
            firstName = "First$id",
            lastName = "Last$id",
            groupId = groupId,
            groupName = "G$groupId",
            competitionId = "c1",
            commandName = "team",
            startNumber = "",
            startTime = 0L,
            chipNumber = "chip$id",
            comment = "",
            isChipGiven = false,
        )

    /** Дистанция участника по [groupDistanceMap] (с тем же fallback, что и в алгоритме). */
    private fun distanceOf(p: OrienteeringParticipant, map: Map<Long, Long>): Long =
        map[p.groupId] ?: p.groupId

    private fun assertInvariants(
        result: List<OrienteeringParticipant>,
        groupDistanceMap: Map<Long, Long>,
        corridors: Int,
        gap: Int,
        expectedSize: Int,
    ) {
        // Все участники на месте, номера сквозные 1..N.
        assertEquals(expectedSize, result.size)
        assertEquals(
            (1..expectedSize).toList(),
            result.map { it.startNumber.toInt() }.sorted()
        )

        // В одной стартовой минуте — не больше corridors участников.
        result.groupBy { it.startTime }.forEach { (_, inSlot) ->
            assertTrue(
                "В слоте ${inSlot.size} участников при лимите $corridors",
                inSlot.size <= corridors
            )
            // И каждая дистанция в слоте встречается не более одного раза.
            val distances = inSlot.map { distanceOf(it, groupDistanceMap) }
            assertEquals(distances.size, distances.toSet().size)
        }

        // Участники одной дистанции разнесены минимум на gap интервалов.
        result.groupBy { distanceOf(it, groupDistanceMap) }.forEach { (_, sameDistance) ->
            val times = sameDistance.map { it.startTime }.sorted()
            times.zipWithNext { a, b ->
                assertTrue(
                    "Зазор ${(b - a) / intervalMs} < $gap интервалов",
                    b - a >= gap * intervalMs
                )
            }
        }
    }

    @Test
    fun unevenDistances_respectsAllConstraints() {
        // Группы 1,2 → дистанция 100; группа 3 → дистанция 200; группа 4 → дистанция 300.
        val groupDistanceMap = mapOf(1L to 100L, 2L to 100L, 3L to 200L, 4L to 300L)
        val participants = buildList {
            repeat(7) { add(participant(it, groupId = 1L)) }   // дистанция 100
            repeat(3) { add(participant(100 + it, groupId = 2L)) } // дистанция 100
            repeat(4) { add(participant(200 + it, groupId = 3L)) } // дистанция 200
            add(participant(300, groupId = 4L))                    // дистанция 300
        }

        val corridors = 2
        val gap = 2
        val result = drawParticipantsByDistance(
            participants = participants,
            groupDistanceMap = groupDistanceMap,
            punchingSystem = PunchingSystem.PENCIL,
            competitionStartTime = startTime,
            intervalMs = intervalMs,
            corridors = corridors,
            gap = gap,
        )

        assertInvariants(result, groupDistanceMap, corridors, gap, participants.size)
    }

    @Test
    fun singleDistance_spacedByGap() {
        val groupDistanceMap = mapOf(1L to 100L)
        val participants = (0 until 5).map { participant(it, groupId = 1L) }

        val result = drawParticipantsByDistance(
            participants = participants,
            groupDistanceMap = groupDistanceMap,
            punchingSystem = PunchingSystem.PENCIL,
            competitionStartTime = startTime,
            intervalMs = intervalMs,
            corridors = 3,
            gap = 3,
        )

        assertInvariants(result, groupDistanceMap, corridors = 3, gap = 3, expectedSize = 5)
        // При одной дистанции в каждой минуте — ровно один участник.
        assertEquals(result.size, result.map { it.startTime }.toSet().size)
    }

    @Test
    fun sportiduino_setsChipToStartNumber() {
        val groupDistanceMap = mapOf(1L to 100L, 2L to 200L)
        val participants =
            (0 until 3).map { participant(it, 1L) } + (0 until 3).map { participant(10 + it, 2L) }

        val result = drawParticipantsByDistance(
            participants = participants,
            groupDistanceMap = groupDistanceMap,
            punchingSystem = PunchingSystem.SPORTIDUINO,
            competitionStartTime = startTime,
            intervalMs = intervalMs,
            corridors = 2,
            gap = 1,
        )

        result.forEach { assertEquals(it.startNumber, it.chipNumber) }
    }

    @Test
    fun emptyInput_returnsEmpty() {
        val result = drawParticipantsByDistance(
            participants = emptyList(),
            groupDistanceMap = emptyMap(),
            punchingSystem = PunchingSystem.PENCIL,
            competitionStartTime = startTime,
            intervalMs = intervalMs,
            corridors = 2,
            gap = 2,
        )
        assertTrue(result.isEmpty())
    }
}
