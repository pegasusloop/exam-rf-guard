package com.classroom.rfdetector.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ObservationPeriodsTest {

    @Test
    fun lateObservationBridgesTwoBlePeriods() {
        val gap = ScanSource.BLE.historyGapMs()

        val first = addToPeriods(emptyList(), 0L, gap)
        val separated = addToPeriods(first, 30_000L, gap)

        assertEquals(2, separated.size)

        val bridged = addToPeriods(separated, 15_000L, gap)

        assertEquals(
            listOf(ObservationPeriod(0L, 30_000L, 3)),
            bridged
        )
    }

    @Test
    fun arrivalOrderDoesNotChangePeriodHistory() {
        val timestamps = listOf(1_000L, 40_000L, 2_000L)
        val gap = ScanSource.BLE.historyGapMs()

        fun periodsFor(order: List<Long>) =
            order.fold(emptyList<ObservationPeriod>()) { periods, time ->
                addToPeriods(periods, time, gap)
            }

        assertEquals(
            periodsFor(timestamps.sorted()),
            periodsFor(timestamps)
        )
    }

    @Test
    fun gapExactlyAtThresholdMerges() {
        val gap = ScanSource.BLE.historyGapMs()

        val result = addToPeriods(
            addToPeriods(emptyList(), 0L, gap),
            gap,
            gap
        )

        assertEquals(
            listOf(ObservationPeriod(0L, gap, 2)),
            result
        )
    }

    @Test
    fun gapBeyondThresholdStaysSeparate() {
        val gap = ScanSource.BLE.historyGapMs()

        val result = addToPeriods(
            addToPeriods(emptyList(), 0L, gap),
            gap + 1,
            gap
        )

        assertEquals(2, result.size)
    }
}