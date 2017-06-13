package org.stevesea

/**
 * given two integers L & R, find number of x such that
 *
 *   L <= x <= R
 *
 *   x = pow(a,p) + pow(b,q)
 *
 *   a,b >= 0
 *   p,q > 1
 */
fun summed_powers_in_range(l: Int, r: Int): Int {
    val powers = mutableListOf<Int>()
    // add 0,1 (0 to-anything = 0, 1 to-anything is 1), rather than complicating our loops below
    powers.add(0)
    powers.add(1)

    // accumulate a bunch of powers into the list
    for (b in 2..r) {
        for (q in 2..r) {
            val p = Math.pow(b.toDouble(), q.toDouble()).toInt()
            if (p > r) {
                // don't bother adding any power that's greater than R, and exit inner loop
                break
            }
            powers.add(p)
        }
    }

    val exes = mutableSetOf<Int>()
    powers.forEach { i ->
        powers.forEach { j ->
            val sum = i + j
            if (sum in l..r) {
                //println("found: $sum")
                exes.add(sum)
            }
        }
    }
    return exes.size
}
