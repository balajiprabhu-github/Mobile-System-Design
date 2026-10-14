package com.balajiprabhu.newsfeed.data.util

interface TimeProvider {
    /**
     * Returns the current timestamp in UTC epoch milliseconds.
     * Implementations should ensure this is synchronized with server time
     * and immune to client wall-clock manipulation.
     */
    fun currentTimeMillis(): Long
}
