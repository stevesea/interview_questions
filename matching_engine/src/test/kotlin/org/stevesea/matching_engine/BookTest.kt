package org.stevesea.matching_engine

import org.junit.Assert
import org.junit.Test

class BookTest {
    @Test
    fun testSorting() {
        val book = Book()

        book.sells.addAll(listOf(Order(10, 15.0), Order(10, 13.0)))
        book.buys.addAll(listOf(Order(10, 7.0), Order(10, 9.5)))

        Assert.assertEquals(listOf(Order(10, 9.5), Order(10, 7.0)), book.buys.toList())
        Assert.assertEquals(listOf(Order(10, 13.0), Order(10, 15.0)), book.sells.toList())
    }
}