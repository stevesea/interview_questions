package org.stevesea

import java.util.*

/**
 * problem: create a reverse polish notation calculator
 *
 * operators: * + - /  ^
 * numbers
 *
 * input: string  "25 10 +"
 * output: result of calculation
 */

fun rpn(input: String) : Double {
    // split string

    val s = Stack<Double>()

    val toks = input.split(Regex("\\s+"))

    // iterate over items, calculating results
    toks.forEach { tok ->
        when (tok) {
            "+" -> {
                s.push(s.pop() + s.pop())
            }
            "-" -> {
                s.push((-1)*s.pop() + s.pop())
            }
            "*" -> {
                s.push(s.pop() * s.pop())
            }
            "/" -> {
                val top = s.pop()
                s.push(s.pop() / top)
            }
            "^" -> {
                val top = s.pop()
                s.push(Math.pow(s.pop(),top))
            }
            else -> {
                s.push(tok.toDouble())
            }
        }
    }

    return s.pop()
}

