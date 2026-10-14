package com.balajiprabhu.newsfeed.data.util

import android.os.SystemClock
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerTimeProvider(
    private val monotonicTimeSource: () -> Long
) : TimeProvider {

    @Inject
    constructor() : this({ SystemClock.elapsedRealtime() })

    // Clock skew: (serverUtcMillis - monotonicMillis)
    // Defaults to wall-clock approximation until first network response arrives
    private val clockSkewMillis = AtomicLong(
        System.currentTimeMillis() - monotonicTimeSource()
    )

    /**
     * Updates the clock skew based on the server's authoritative UTC timestamp
     * (e.g. from the HTTP Date response header).
     */
    fun updateServerTime(serverEpochMillis: Long) {
        val currentMonotonic = monotonicTimeSource()
        clockSkewMillis.set(serverEpochMillis - currentMonotonic)
    }

    override fun currentTimeMillis(): Long {
        return monotonicTimeSource() + clockSkewMillis.get()
    }
}
