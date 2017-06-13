package org.stevesea

// impl algorithm similar to itoa
fun itoaDecimal(num: Int) : String {

    val chars = mutableListOf<Char>()
    var div = 10

    val isNeg = num < 0

    val alwaysPosNum = if (isNeg) -1*num else num

    do {
        val r = alwaysPosNum % div
        val d = r / (div / 10)

        div *= 10

        chars.add(0, '0' + d)

    } while ( r != alwaysPosNum)

    if (isNeg)
        chars.add(0, '-')

    return chars.joinToString("")
}
