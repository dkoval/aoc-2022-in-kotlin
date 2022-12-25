package day25

import readInput

private const val DAY_ID = "25"

fun main() {
    val snafuToBase10Digits = mapOf('=' to -2, '-' to -1, '0' to 0, '1' to 1, '2' to 2)
    val base10ToSnafuDigits = mapOf(0 to '0', 1 to '1', 2 to '2', -2 to '=', -1 to '-')

    fun snafuToBase10(num: String): Long {
        var ans = 0L
        for (digit in num) {
            ans *= 5
            ans += snafuToBase10Digits[digit]!!
        }
        return ans
    }

    fun base10ToSnafu(num: Long): String {
        // x mod 5 is in {0, 1, 2, 3, 4}
        // now, remap the value to get a SNAFU digit
        // 0 -> 0, 1 -> 1, 2 -> 2, 3 -> -2, 4 -> -1
        fun base10SnafuDigit(x: Long): Int {
            return (if (x < 3) x else x - 5).toInt()
        }

        var x = num
        val sb = StringBuilder()
        while (x > 0) {
            val digit = base10SnafuDigit(x % 5)
            sb.append(base10ToSnafuDigits[digit])
            x /= 5
            x += if (digit < 0) 1 else 0
        }
        return sb.reverse().toString()
    }

    fun part1(input: List<String>): String {
        val x = input.sumOf { snafuToBase10(it) }
        return base10ToSnafu(x)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == "2=-1=0") // base10 = 4890, snafu = 2=-1=0

    val input = readInput("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input)) // answer = 2-20=01--0=0=0=2-120
}
