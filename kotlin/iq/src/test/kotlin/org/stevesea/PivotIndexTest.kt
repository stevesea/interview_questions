package org.stevesea

import org.junit.Assert
import org.junit.Test

class PivotIndexTest {
    @Test
    fun test1() {
        val fp = FindPivotIndex();
        Assert.assertEquals(3, fp.findPivot(intArrayOf(-1,3,-4,5,1,-6,2,1)))
    }
    @Test
    fun testDumb() {
        val fp = FindPivotIndex();
        Assert.assertEquals(1, fp.findPivot(intArrayOf(1,1,1)))
    }
}
