package org.stevesea

import org.junit.Assert
import org.junit.Test


class AddingMachineTest {
    @Test
    fun add() {
        Assert.assertEquals(10, adding_machine(listOf("5", "+", "5")))
    }

    @Test
    fun minus() {
        Assert.assertEquals(0, adding_machine(listOf("5", "-", "5")))
    }

    @Test
    fun addNegativeNum() {
        Assert.assertEquals(0, adding_machine(listOf("5", "+", "-5")))
    }

    @Test(expected = NumberFormatException::class)
    fun badInput1() {
        Assert.assertEquals(0, adding_machine(listOf("5", "+", "+", "-5")))
    }
    @Test(expected = NumberFormatException::class)
    fun badInput2() {
        Assert.assertEquals(0, adding_machine(listOf("5", "+", "-", "-5")))
    }
    @Test(expected = NumberFormatException::class)
    fun badInput3() {
        Assert.assertEquals(0, adding_machine(listOf("+", "-5")))
    }
}
