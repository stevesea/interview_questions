package org.stevesea

import java.util.*

/**
 * problem:
 *
 *  given a integer list w/ nested integer lists, provide sum of all integers
 */

@Suppress("UNCHECKED_CAST")
fun nested_int_sum_recursive(l: List<Any>) : Long {

    var sum = 0L
    l.forEach { item ->
        if (item is List<*>) {
            sum += nested_int_sum_recursive(item as List<Any>)
        } else if (item is Int) {
            sum += item
        } else {
            throw IllegalArgumentException("Invalid nested item input (must be int or list: $item")
        }
    }

    return sum
}

@Suppress("UNCHECKED_CAST")
fun nested_int_sum_iterative(l: List<Any>) : Long {
    var sum = 0L

    val s = Stack<List<Any>>()

    s.push(l)

    while (s.isNotEmpty()) {
        val poppedList = s.pop()
        poppedList.forEach { item ->
            if (item is List<*>) {
                s.push(item as List<Any>)
            } else if (item is Int) {
                sum += item
            } else {
                throw IllegalArgumentException("Invalid nested item input (must be int or list: $item")
            }
        }
    }
    return sum
}
