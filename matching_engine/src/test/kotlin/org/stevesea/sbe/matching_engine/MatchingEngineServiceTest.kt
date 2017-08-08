package org.stevesea.sbe.matching_engine

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MatchingEngineServiceTest {

    var svc: MatchingEngineService? = null

    @Before
    fun setup() {
        svc = MatchingEngineService()
        listOf(Order(10, 15.0), Order(10, 13.0)).forEach { it ->
            svc?.sell(it)
        }
        listOf(Order(10, 7.0), Order(10, 9.5)).forEach { it ->
            svc?.buy(it)
        }
    }

    @Test
    fun testGet() {
        val b = svc?.book()

        Assert.assertEquals(listOf(Order(10, 9.5), Order(10, 7.0)), b?.buys?.toList())
        Assert.assertEquals(listOf(Order(10, 13.0), Order(10, 15.0)), b?.sells?.toList())
    }

    @Test
    fun testSell1() {
        svc?.sell(Order(5, 9.5))

        val b = svc?.book()

        Assert.assertEquals(listOf(Order(5, 9.5), Order(10, 7.0)), b?.buys?.toList())
        Assert.assertEquals(listOf(Order(10, 13.0), Order(10, 15.0)), b?.sells?.toList())
    }

    @Test
    fun testBuy1() {
        svc?.sell(Order(5, 9.5))
        svc?.buy(Order(6, 13.0))

        val b = svc?.book()

        Assert.assertEquals(listOf(Order(5, 9.5), Order(10, 7.0)), b?.buys?.toList())
        Assert.assertEquals(listOf(Order(4, 13.0), Order(10, 15.0)), b?.sells?.toList())
    }
    @Test
    fun testSell2() {
        svc?.sell(Order(5, 9.5))
        svc?.buy(Order(6, 13.0))
        svc?.sell(Order(7, 7.0))

        val b = svc?.book()

        Assert.assertEquals(listOf(Order(8, 7.0)), b?.buys?.toList())
        Assert.assertEquals(listOf(Order(4, 13.0), Order(10, 15.0)), b?.sells?.toList())
    }
    @Test
    fun testSell3() {
        svc?.sell(Order(5, 9.5))
        svc?.buy(Order(6, 13.0))
        svc?.sell(Order(7, 7.0))
        svc?.sell(Order(12, 6.0))

        val b = svc?.book()

        Assert.assertEquals(listOf<Order>(), b?.buys?.toList())
        Assert.assertEquals(listOf(Order(4, 6.0), Order(4, 13.0), Order(10, 15.0)), b?.sells?.toList())
    }
}