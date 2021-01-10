package com.omelan.cofi.utils

import junit.framework.TestCase

class NumberUtilsKtTest : TestCase() {

    fun testToMillis() {
        val testInt = 4
        val expectedMillis = 4000
        assertEquals(expectedMillis, testInt.toMillis())
    }

    fun testToStringDuration() {
        val testMillis = 303000
        val expectedString = "5:03"
        assertEquals(expectedString, testMillis.toStringDuration())
    }

    fun testToStringDurationFullOptions() {
        val testMillis = 303100
        val expectedString = "05:03:10"
        assertEquals(
            expectedString,
            testMillis.toStringDuration(
                padMillis = true,
                padMinutes = true,
                padSeconds = true,
                showMillis = true,
            )
        )
    }
}