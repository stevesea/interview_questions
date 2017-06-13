package org.stevesea

//
// the usual
//
//   0,1,1,2,3,5,8,13,21,34
//
//  f(x) = f(n-1) + f(n-2)
//
//

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
