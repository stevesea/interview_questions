package org.stevesea

import org.junit.Assert
import org.junit.Test

class PivotIndexTest {
    @Test
    fun test1() {
        val fp = FindPivotIndex();
        Assert.assertEquals(3, fp.findPivot(intArrayOf(-1,2,-3,2,1,-5,1,1)))
    }
    @Test
    fun testDumb() {
        val fp = FindPivotIndex();
        Assert.assertEquals(1, fp.findPivot(intArrayOf(1,1,1)))
    }
    @Test
    fun testOneElement() {
        val fp = FindPivotIndex();
        Assert.assertEquals(0, fp.findPivot(intArrayOf(1)))
    }

    @Test
    fun testNoElements() {
        val fp = FindPivotIndex();
        Assert.assertEquals(-1, fp.findPivot(intArrayOf()))
    }
    @Test
    fun test2() {
        val fp = FindPivotIndex();
        Assert.assertEquals(1, fp.findPivot(intArrayOf(5,3,2,3)))
    }
}
