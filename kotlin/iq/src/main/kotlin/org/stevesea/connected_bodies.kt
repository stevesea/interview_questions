package org.stevesea

import java.util.*

/**
 * problem:
 *  given 2D matrix of integers, find connected bodies that have value 0
 *
 * input: 2d matrix of ints
 * output: sorted list of sizes of discovered bodies of water
 *
 * 'connected' cells can be any adjacent cell (diagonals too)
 *
 */

class BodyFinder(val n: Int, val elevmap : List<List<Int>>) {
    val visited  = mutableListOf<MutableList<Boolean>>()
    init {
        (0..n).forEach {
            val row = mutableListOf<Boolean>()
            (0..n).forEach {
                row.add(false)
            }
            visited.add(row)
        }
    }

    /**
     * @returns true: if valid matrix coords (not off edges) && not already visited && value in elevmap is zero
     */
    fun shouldVisit(r: Int, c: Int) : Boolean {
        return r in 0..(n - 1) &&
                c in 0..(n - 1) &&
                elevmap[r][c] == 0 &&
                !visited[r][c]
    }

    /**
     * given row and column of water cell, recursively search the adjacent cells to see if they are also water
     * @returns the size of the subgraph of water
     */
    fun searchWater(r: Int, c: Int) : Int {
        // count current cell as visited.
        // the 'size' of the local subgraph is at least 1 (for current cell)
        // NOTE: we only reach this method IF the current cell is a water cell.
        var bodySize = 1
        visited[r][c] = true

        // look at adjacent cells
        ((r-1)..(r+1)).forEach { i ->
            ((c-1)..(c+1) ).forEach { j ->
                // i,j==r,c will have been marked as visited above.
                if (shouldVisit(i,j)) {
                    // accumulate the size the sub-search found to our own
                    bodySize += searchWater(i,j)
                }
            }
        }
        return bodySize
    }

    fun findBodies() : List<Int> {
        val bodySizes = mutableListOf<Int>()

        (0..(n-1)).forEach { r ->
            (0..(n-1)).forEach { c ->
                if (shouldVisit(r,c)) {
                    bodySizes.add(searchWater(r,c))
                }
            }
        }
        Collections.sort(bodySizes)

        return bodySizes
    }

}

