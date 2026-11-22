
package com.classroom.rfdetector.core

import org.junit.Assert.*
import org.junit.Test

class WifiIdentityCollisionTest {

    @Test
    fun differentAccessPointWithSameSsidIsSuppressed() {
        val tracker = DeviceTracker()

        tracker.update(
            RfObservation(
                "10:00:00:00:00:01",
                ScanSource.WIFI,
                "SCHOOL",
                -55,
                null,
                false,
                0L
            )
        )

        val baseline = SignalSnapshot.capture(
            "Classroom",
            1_000L,
            false,
            tracker.snapshot(1_000L, includeInactive = true)
        )

        val counts = baselineGroupCounts(baseline)
        val baselineAddresses = baselineWifiAddresses(baseline)

        // A distinct, globally administered BSSID.
        tracker.update(
            RfObservation(
                "20:11:22:33:44:55",
                ScanSource.WIFI,
                "SCHOOL",
                -45,
                null,
                false,
                100_000L
            )
        )

        val result = watchView(
            tracker.snapshot(101_000L, includeInactive = true),
            counts,
            emptySet(),
            101_000L,
            baselineAddresses
        )

        assertEquals(0, result.wifi.size)
    }
}
