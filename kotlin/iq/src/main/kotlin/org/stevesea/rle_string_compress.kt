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

    var curChar: Char? = null
    val accumDigits = mutableListOf<Char>()

    inStr.forEach { c ->
        if (c.isDigit()) {
            accumDigits.add(c)
        } else {
            if (curChar != null) {
                if (accumDigits.isNotEmpty()) {
                    // we've finished accumulating digits and have landed on a new letter
                    val n = accumDigits.joinToString("").toInt()
                    (1..n).forEach {
                        sb.append(curChar ?: "")
                    }
                    accumDigits.clear()
                } else {
                    sb.append(curChar ?: "")
                }
            }
            // whether this is first letter or not, need to swap to new char
            curChar = c
        }
    }
    if (curChar != null) {
        if (accumDigits.isNotEmpty()) {
            val n = accumDigits.joinToString("").toInt()
            (1..n).forEach {
                sb.append(curChar ?: "")
            }
        } else {
            sb.append(curChar ?: "")
        }
    }

    return sb.toString()
}
