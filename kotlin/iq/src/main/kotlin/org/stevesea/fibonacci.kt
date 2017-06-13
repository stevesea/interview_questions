package org.stevesea

import java.util.*


fun fib_recursive(n: Int) : Int =
    when (n) {
        0 -> 0
        1 -> 1
        else -> fib_recursive(n-2) + fib_recursive(n-1)
    }


fun fib_iterative(n: Int): Int {
    if (n == 0) return 0
    if (n == 1) return 1

    var prevPrev = 0
    var prev = 1
    var result = 0
    

    for (i in 2..n) {
        result = prev + prevPrev
        prevPrev = prev
        prev = result
    }
    return result
}
