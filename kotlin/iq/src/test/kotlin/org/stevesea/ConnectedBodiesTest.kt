package org.stevesea

import org.junit.Assert
import org.junit.Test


class ConnectedBodiesTest {
    @Test
    fun test1() {
        val elevmap = listOf(
                listOf(0,0,1,2,1),
                listOf(1,0,1,1,0),
                listOf(0,1,2,0,0),
                listOf(1,1,3,1,1),
                listOf(0,1,0,1,0)
        )
        val bf = BodyFinder(5,elevmap)
        Assert.assertEquals(listOf(1,1,1,3,4), bf.findBodies())
    }
    @Test
    fun test2() {
        val elevmap = listOf(
                listOf(5,5,1,2,1),
                listOf(1,5,1,1,5),
                listOf(5,1,2,5,5),
                listOf(1,1,3,1,1),
                listOf(5,1,5,1,5)
        )
        val bf = BodyFinder(5,elevmap)
        Assert.assertEquals(listOf<Int>(), bf.findBodies())
    }
    @Test
    fun test3() {
        val elevmap = listOf(
                listOf(0,0,1,2,1),
                listOf(1,0,1,1,0),
                listOf(0,1,2,0,0),
                listOf(0,1,3,1,0),
                listOf(0,0,0,0,0)
        )
        val bf = BodyFinder(5,elevmap)
        Assert.assertEquals(listOf(14), bf.findBodies())
    }

}

