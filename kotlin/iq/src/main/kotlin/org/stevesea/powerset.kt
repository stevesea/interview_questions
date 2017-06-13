package org.stevesea

/**
 * given a set, return the power set  (the set of all subsets of S).
 *
 * see https://en.wikipedia.org/wiki/Power_set
 *
 * input: a set
 * output: all subsets of input (including empty set, and the set itself)
 *
 */

fun powerset(s: Set<Any>) : Set<Set<Any>> {
    var powersets = mutableSetOf<Set<Any>>()

    powersets.add(setOf()) // add empty list

    s.forEach { item ->
        val newps = mutableSetOf<Set<Any>>()

        // copy all of current powerset's subsets
        // plus, the subsets w/ current item
        powersets.forEach { pset ->
            newps.add(pset)

            val newset = mutableSetOf(item)
            newset.addAll(pset)
            newps.add(newset)
        }

        powersets = newps
    }

    return powersets
}
