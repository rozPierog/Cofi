package com.omelan.cofi.utils

import com.omelan.cofi.share.utils.safeToInt
import com.omelan.cofi.share.utils.toMillis
import com.omelan.cofi.share.utils.toStringDuration
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
        val expectedString = "05:03.10"
        assertEquals(
            expectedString,
            testMillis.toStringDuration(
                padMillis = true,
                padMinutes = true,
                padSeconds = true,
                showMillis = true,
            ),
        )
    }

    fun testSafeToInt() {
        mapOf(
            "0" to 0,
            "a" to 0,
            "-" to 0,
            "1" to 1,
            "23 " to 23,
            " 17" to 17,
            "2!" to 0,
        ).forEach { entry: Map.Entry<String, Int> ->
            assertEquals(entry.value, entry.key.safeToInt())
        }
    }
}
