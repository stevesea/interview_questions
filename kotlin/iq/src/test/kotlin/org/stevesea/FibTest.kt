package org.stevesea

import org.junit.Assert
import org.junit.Test

class FibTest {
    @Test
    fun fibRecursiveTest() {
        Assert.assertEquals(0, fib_recursive(0))
        Assert.assertEquals(1, fib_recursive(1))
        Assert.assertEquals(1, fib_recursive(2))
        Assert.assertEquals(2, fib_recursive(3))
        Assert.assertEquals(3, fib_recursive(4))
        Assert.assertEquals(5, fib_recursive(5))
        Assert.assertEquals(8, fib_recursive(6))
        Assert.assertEquals(13, fib_recursive(7))
        Assert.assertEquals(21, fib_recursive(8))
    }
    @Test
    fun fibIterativeTest() {
        Assert.assertEquals(0, fib_iterative(0))
        Assert.assertEquals(1, fib_iterative(1))
        Assert.assertEquals(1, fib_iterative(2))
        Assert.assertEquals(2, fib_iterative(3))
        Assert.assertEquals(3, fib_iterative(4))
        Assert.assertEquals(5, fib_iterative(5))
        Assert.assertEquals(8, fib_iterative(6))
        Assert.assertEquals(13, fib_iterative(7))
        Assert.assertEquals(21, fib_iterative(8))
    }
    @Test
    fun fibMemoized() {

        run {
            val start = System.nanoTime()
            Assert.assertEquals(102334155, fib_recursive(40))
            val end = System.nanoTime()
            println("recursive: ${end - start}ns")
        }

        run {
            val start = System.nanoTime()
            Assert.assertEquals(102334155, fib_recursive_memoized(40))
            val end = System.nanoTime()
            println("memoized : ${end - start}ns")
        }
        run {
            val start = System.nanoTime()
            Assert.assertEquals(102334155, fib_iterative(40))
            val end = System.nanoTime()
            println("iterative: ${end - start}ns")
        }
    }
}
