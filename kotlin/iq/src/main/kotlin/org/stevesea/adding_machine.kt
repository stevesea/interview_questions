package org.stevesea

/**
 * problem statement:
 *    write a method that validates and provides sum of adding machine tape
 *
 *    24
 *    +
 *    10
 *    -
 *    5
 *    +
 *    3
 *
 *
 *    input : list of strings
 *    output: result of operation
 *
 *    if bad input, exception will be thrown
 *
 *    operations: "+", "-"
 *
 *    invalid input:
 *          30 + + 10
 *          10 - + 10
 *    valid input:
 *          10 - -10
 */

fun adding_machine(tape: List<String>) : Long {

    // first element of tape should always be a #
    var result = tape[0].toLong()

    // iterate over rest of tape by 2s
    //   i-th element should always be an operator
    //   (i+1)-th element should always be a number
    (1 until tape.size step 2).forEach { i ->
        val item = tape[i]
        when(item) {
            "-" -> {
                result -= tape[i+1].toLong()
            }
            "+" -> {
                result += tape[i+1].toLong()
            }
        }
    }
    return result
}
