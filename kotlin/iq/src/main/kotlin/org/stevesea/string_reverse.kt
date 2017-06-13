package org.stevesea

/**
 * reverse a string... der
 */
fun strReverse(inStr: String) : String {
    val sb = StringBuilder()
    
    for (i in inStr.length-1 downTo 0) {
        sb.append(inStr[i])
    }
    return sb.toString()
}