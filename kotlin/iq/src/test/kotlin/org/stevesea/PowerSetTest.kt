package org.stevesea

import org.junit.Assert
import org.junit.Test


class PowerSetTest {
    @Test
    fun psetTest() {
        Assert.assertEquals(setOf(setOf(), setOf(1), setOf(2), setOf(1,2)), powerset(setOf(1,2)))
    }
}
