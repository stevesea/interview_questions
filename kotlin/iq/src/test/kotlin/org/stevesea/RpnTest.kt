package org.stevesea;

import org.junit.Assert
import org.junit.Test

class RpnTest {

    @Test
    fun testAdd() {
        Assert.assertEquals(35.0, rpn("25 10 +"), 0.0001)
    }
    @Test
    fun testMinus() {
        Assert.assertEquals(15.0, rpn("25 10 -"), 0.0001)
    }
    @Test
    fun testMult() {
        Assert.assertEquals(250.0, rpn("25 10 *"), 0.0001)
    }
    @Test
    fun testDiv() {
        Assert.assertEquals(2.5, rpn("25 10 /"), 0.0001)
    }
    @Test
    fun testPow() {
        Assert.assertEquals(16.0, rpn("4 2 ^"), 0.0001)
    }

    @Test
    fun testComplex() {
        Assert.assertEquals(50.0, rpn("25 10 + 10 - 2 *"), 0.0001)
    }
}
