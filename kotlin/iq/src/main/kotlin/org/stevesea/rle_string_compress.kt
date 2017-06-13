package org.stevesea

/**
 * problem RLE-compress a string (and a decompress too)
 *
 * twist: don't return compressed version if compressed version is longer than input
 *
 * examples:
 *    - input: cat
 *      output: cat (c1a1t1 is too long)
 *
 *    - input:  ssssccoooooppp
 *      output: s4c2o5p3
 */

fun rle_compress(inStr: String) : String {
    val sb = StringBuilder()
    if (inStr.isBlank())
        return inStr

    var prevChar = inStr[0]
    var charCount = 1
    (1 until inStr.length).forEach { i ->
        val curChar = inStr[i]
        if (curChar == prevChar) {
            charCount++
        } else {
            sb.append(prevChar)
            sb.append(charCount)
            prevChar = curChar
            charCount = 1
        }
    }
    // tack on the final char
    sb.append(prevChar)
    sb.append(charCount)

    if (sb.length < inStr.length) {
        return sb.toString()
    } else {
        return inStr
    }
}

fun rle_decompress(inStr: String) : String {
    val sb = StringBuilder()
    if (inStr.isBlank())
        return inStr

    var i = 0
    while (i < inStr.length) {
        var j = i + 1
        while (j < inStr.length) {
            val c = inStr[j]
            if (!c.isDigit())
                break
            j++
        }
        val c = inStr[i]
        if (j == i+1) {
            // no number or at end of string, just tack on the char
            sb.append(c)
        } else {
            val numStr = inStr.substring(i+1, j)
            val num = numStr.toInt()
            (1..num).forEach {
                sb.append(c)
            }
        }

        i = j
    }

    return sb.toString()
}
