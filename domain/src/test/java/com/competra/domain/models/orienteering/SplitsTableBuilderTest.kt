package com.competra.domain.models.orienteering

import com.competra.domain.models.Gender
import com.competra.domain.models.ParticipantGroup
import com.competra.domain.models.ResultStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Проверка [buildSplitsTable] и [sortedForResults] — общего билдера таблицы сплитов,
 * переиспользуемого HTML/CSV/PDF-экспортом и экранами сплитов группы.
 */
class SplitsTableBuilderTest {

    private fun participant(id: String, groupId: Long = 1L, startTime: Long = 0L): OrienteeringParticipant =
        OrienteeringParticipant(
            id = id,
            userId = "u$id",
            firstName = "First$id",
            lastName = "Last$id",
            groupId = groupId,
            groupName = "G$groupId",
            competitionId = "c1",
            commandName = "team",
            startNumber = id,
            startTime = startTime,
            chipNumber = "chip$id",
            comment = "",
            isChipGiven = true,
        )

    private fun result(
        participantId: String,
        status: ResultStatus,
        startTime: Long? = 0L,
        totalTime: Long? = null,
        splits: List<SplitTime>? = null,
    ): OrienteeringResult = OrienteeringResult(
        competitionId = "c1",
        groupId = 1L,
        participantId = participantId,
        startTime = startTime,
        totalTime = totalTime,
        status = status,
        splits = splits,
    )

    private fun group(participants: List<ParticipantWithResult>): GroupWithParticipantsAndResults =
        GroupWithParticipantsAndResults(
            group = ParticipantGroup(
                groupId = 1L,
                competitionId = "c1",
                title = "M21",
                gender = Gender.MALE,
                distanceId = 1L,
            ),
            participants = participants,
        )

    @Test
    fun `positional split matching computes correct delta and cumulative seconds`() {
        val a = ParticipantWithResult(
            participant = participant("A"),
            result = result(
                "A", ResultStatus.FINISHED, totalTime = 200,
                splits = listOf(SplitTime(31, 100_000), SplitTime(32, 200_000)),
            ),
        )
        val b = ParticipantWithResult(
            participant = participant("B"),
            result = result(
                "B", ResultStatus.FINISHED, totalTime = 260,
                splits = listOf(SplitTime(31, 150_000), SplitTime(32, 260_000)),
            ),
        )

        val table = buildSplitsTable(group(listOf(a, b)))

        assertEquals(listOf(31, 32), table.columns.map { it.controlPoint })

        val rowA = table.rows[0]
        assertEquals(100L, rowA.cells[0].deltaSeconds)
        assertEquals(100L, rowA.cells[0].cumulativeSeconds)
        assertEquals(100L, rowA.cells[1].deltaSeconds)
        assertEquals(200L, rowA.cells[1].cumulativeSeconds)

        val rowB = table.rows[1]
        assertEquals(150L, rowB.cells[0].deltaSeconds)
        assertEquals(110L, rowB.cells[1].deltaSeconds)
        assertEquals(260L, rowB.cells[1].cumulativeSeconds)
    }

    @Test
    fun `fastest leg is ranked first and marked as best`() {
        val a = ParticipantWithResult(
            participant = participant("A"),
            result = result(
                "A", ResultStatus.FINISHED, totalTime = 200,
                splits = listOf(SplitTime(31, 100_000), SplitTime(32, 200_000)),
            ),
        )
        val b = ParticipantWithResult(
            participant = participant("B"),
            result = result(
                "B", ResultStatus.FINISHED, totalTime = 260,
                splits = listOf(SplitTime(31, 150_000), SplitTime(32, 260_000)),
            ),
        )

        val table = buildSplitsTable(group(listOf(a, b)))

        val rowA = table.rows[0]
        val rowB = table.rows[1]

        assertTrue(rowA.cells[0].isBestLeg)
        assertTrue(rowA.cells[1].isBestLeg)
        assertFalse(rowB.cells[0].isBestLeg)
        assertFalse(rowB.cells[1].isBestLeg)

        assertEquals(1, rowA.cells[0].deltaRank)
        assertEquals(2, rowB.cells[0].deltaRank)
        assertEquals(1, rowA.cells[1].cumulativeRank)
        assertEquals(2, rowB.cells[1].cumulativeRank)
    }

    @Test
    fun `participant with missing punch gets empty cell instead of crashing`() {
        val a = ParticipantWithResult(
            participant = participant("A"),
            result = result(
                "A", ResultStatus.FINISHED, totalTime = 200,
                splits = listOf(SplitTime(31, 100_000), SplitTime(32, 200_000)),
            ),
        )
        val c = ParticipantWithResult(
            participant = participant("C"),
            result = result(
                "C", ResultStatus.DNF,
                splits = listOf(SplitTime(31, 120_000)), // не дошёл до второго КП
            ),
        )

        val table = buildSplitsTable(group(listOf(a, c)))

        val rowC = table.rows[1]
        assertEquals(120L, rowC.cells[0].cumulativeSeconds)
        assertNull(rowC.cells[1].cumulativeSeconds)
        assertNull(rowC.cells[1].deltaSeconds)
        assertFalse(rowC.cells[1].isBestLeg)
    }

    @Test
    fun `falls back to participant scheduled start time when result start time is lost`() {
        // Сценарий SEN-27: реальный OrienteeringResult.startTime утерян (результаты потеряны и
        // восстановлены HTML-импортом), сплиты в этом случае реконструируются от планового
        // startTime участника (см. buildResultsDiff) — отображение должно использовать тот же анкер,
        // иначе кумулятив считается от эпохи Unix (1970 года) вместо реального времени старта.
        val scheduledStart = 1_700_000_000_000L // плановое время старта участника
        val a = ParticipantWithResult(
            participant = participant("A", startTime = scheduledStart),
            result = result(
                "A", ResultStatus.FINISHED, startTime = null, totalTime = 200,
                splits = listOf(
                    SplitTime(31, scheduledStart + 100_000),
                    SplitTime(32, scheduledStart + 200_000),
                ),
            ),
        )

        val table = buildSplitsTable(group(listOf(a)))
        val row = table.rows[0]

        assertEquals(100L, row.cells[0].cumulativeSeconds)
        assertEquals(100L, row.cells[0].deltaSeconds)
        assertEquals(200L, row.cells[1].cumulativeSeconds)
        assertEquals(100L, row.cells[1].deltaSeconds)
        assertEquals(1, row.cells[0].cumulativeRank)
    }

    @Test
    fun `participant without result produces columns with no splits`() {
        val onlyNoResult = ParticipantWithResult(participant = participant("A"), result = null)

        val table = buildSplitsTable(group(listOf(onlyNoResult)))

        assertTrue(table.columns.isEmpty())
        assertTrue(table.rows[0].cells.isEmpty())
    }

    @Test
    fun `sortedForResults orders by status then by total time`() {
        val finishedSlow = ParticipantWithResult(participant("slow"), result("slow", ResultStatus.FINISHED, totalTime = 500))
        val finishedFast = ParticipantWithResult(participant("fast"), result("fast", ResultStatus.FINISHED, totalTime = 300))
        val dsq = ParticipantWithResult(participant("dsq"), result("dsq", ResultStatus.DSQ))
        val dnf = ParticipantWithResult(participant("dnf"), result("dnf", ResultStatus.DNF))
        val dns = ParticipantWithResult(participant("dns"), result("dns", ResultStatus.DNS))
        val started = ParticipantWithResult(participant("started"), result("started", ResultStatus.STARTED))
        val registered = ParticipantWithResult(participant("registered"), result("registered", ResultStatus.REGISTERED))
        val noResult = ParticipantWithResult(participant("none"), result = null)

        val sorted = listOf(noResult, registered, started, dns, dnf, dsq, finishedSlow, finishedFast)
            .sortedForResults()

        assertEquals(
            listOf("fast", "slow", "dsq", "dnf", "dns", "started", "registered", "none"),
            sorted.map { it.participant.id },
        )
    }
}
