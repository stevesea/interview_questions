package org.stevesea

//
// the usual
//
//   0,1,1,2,3,5,8,13,21,34
//
//  f(x) = f(n-1) + f(n-2)
//
//


// if want real memoization, look at funktionale library

fun fib_recursive(n: Long) : Long =
    when (n) {
        0L -> 0
        1L -> 1
        else -> fib_recursive(n-2) + fib_recursive(n-1)
    }


fun fib_iterative(n: Long): Long {
    if (n == 0L) return 0
    if (n == 1L) return 1

    var prevPrev = 0L
    var prev = 1L
    var result = 0L

    for (i in 2..n) {
        result = prev + prevPrev
        prevPrev = prev
        prev = result
    }
    return result
}
