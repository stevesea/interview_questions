package org.stevesea

import org.junit.Assert
import org.junit.Test

class ItoATest {
    @Test
    fun testZero() {
        Assert.assertEquals("0", itoaDecimal(0))
    }

    @Test
    fun testPositive() {
        Assert.assertEquals("1234", itoaDecimal(1234))
    }

    @Test
    fun testNegative() {
        Assert.assertEquals("-1234", itoaDecimal(-1234))
    }
}
