fun main() {
    fun solve(input: String, windowSize: Int): Int {
        // sliding window [start : start + k - 1]
        var start = 0
        while (start + windowSize < input.length) {
            // starting from `start` index, check next k characters
            val seen = hashMapOf<Char, Int>()
            for (i in start until start + windowSize) {
                if (input[i] in seen) {
                    start = seen[input[i]]!! + 1
                    break
                }
                seen[input[i]] = i
            }

            // are checked k characters unique?
            if (seen.size == windowSize) {
                return start + windowSize
            }
        }
        return -1
    }

    fun part1(input: String): Int = solve(input, 4)

    fun part2(input: String): Int = solve(input, 14)

    // test if implementation meets criteria from the description, like:
    check(part1(readInputAsString("Day06_test_part1_1")) == 7)
    check(part1(readInputAsString("Day06_test_part1_2")) == 5)
    check(part1(readInputAsString("Day06_test_part1_3")) == 6)
    check(part1(readInputAsString("Day06_test_part1_4")) == 10)
    check(part1(readInputAsString("Day06_test_part1_5")) == 11)

    check(part2(readInputAsString("Day06_test_part2_1")) == 19)
    check(part2(readInputAsString("Day06_test_part2_2")) == 23)
    check(part2(readInputAsString("Day06_test_part2_3")) == 23)
    check(part2(readInputAsString("Day06_test_part2_4")) == 29)
    check(part2(readInputAsString("Day06_test_part2_5")) == 26)

    val input = readInputAsString("Day06")
    println(part1(input)) // answer = 1766
    println(part2(input)) // answer = 2383
}
