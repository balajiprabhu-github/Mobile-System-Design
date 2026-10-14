package com.balajiprabhu.newsfeed.data.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ServerTimeProviderTest {

    @Test
    fun currentTimeMillis_advancesWithMonotonicClock() {
        var mockMonotonic = 100_000L
        val provider = ServerTimeProvider(monotonicTimeSource = { mockMonotonic })

        val initialTime = provider.currentTimeMillis()

        // Advance monotonic clock by 5 seconds
        mockMonotonic += 5_000L
        val advancedTime = provider.currentTimeMillis()

        assertEquals(5_000L, advancedTime - initialTime)
    }

    @Test
    fun updateServerTime_correctsClockSkewToAuthoritativeServerUtc() {
        var mockMonotonic = 50_000L
        val provider = ServerTimeProvider(monotonicTimeSource = { mockMonotonic })

        // Simulate server response stating current UTC is 1_700_000_000_000L
        val authoritativeServerTime = 1_700_000_000_000L
        provider.updateServerTime(authoritativeServerTime)

        // At this exact monotonic instant, currentTimeMillis() should equal the server time
        assertEquals(authoritativeServerTime, provider.currentTimeMillis())

        // Monotonic advances 10 seconds (10,000 ms)
        mockMonotonic += 10_000L
        assertEquals(authoritativeServerTime + 10_000L, provider.currentTimeMillis())
    }
}
