package org.stevesea

import org.junit.Assert
import org.junit.Test


class NestedIntSum {
    @Test
    fun sum_recursive() {
        Assert.assertEquals(36, nested_int_sum_recursive(listOf<Any>(1,2,3,listOf<Any>(4,5,listOf<Any>(6,7,8)))))
    }
    @Test
    fun sum_iterative() {
        Assert.assertEquals(36, nested_int_sum_iterative(listOf<Any>(1,2,3,listOf<Any>(4,5,listOf<Any>(6,7,8)))))
    }
}
