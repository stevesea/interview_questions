package org.stevesea

import org.junit.Assert
import org.junit.Test


class StrReverseTest {
    @Test
    fun testSimple() {
        Assert.assertEquals("1234", strReverse("4321"))
        Assert.assertEquals("", strReverse(""))
        Assert.assertEquals("desrever", strReverse("reversed"))
    }
}
