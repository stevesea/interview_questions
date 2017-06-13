package org.stevesea

import org.junit.Assert
import org.junit.Test

class SummedPowersTest {
    @Test
    fun test1() {
        Assert.assertEquals(2, summed_powers_in_range(0,1))
    }
    @Test
    fun test2() {
        Assert.assertEquals(14, summed_powers_in_range(0,20))
    }
}