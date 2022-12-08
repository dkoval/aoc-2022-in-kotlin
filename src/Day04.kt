private const val DAY_ID = "04"

fun main() {
    fun parseInput(input: List<String>): List<Pair<IntRange, IntRange>> =
        input.map { line ->
                val (first, second) = line.split(",").map { range ->
                    val (start, end) = range.split("-").map { it.toInt() }
                    start..end
                }
                first to second
            }

    fun part1(input: List<String>): Int {
        fun IntRange.fullyContains(b: IntRange): Boolean =
            b.first >= this.first && b.last <= this.last

        val data = parseInput(input)
        return data.count { (first, second) -> first.fullyContains(second) || second.fullyContains(first) }
    }

    fun part2(input: List<String>): Int {
        fun overlap(a: IntRange, b: IntRange): Boolean =
            b.last >= a.first && b.first <= a.last

        val data = parseInput(input)
        return data.count { (first, second) -> overlap(first, second) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${DAY_ID}_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day${DAY_ID}")
    println(part1(input)) // answer = 588
    println(part2(input)) // answer = 911
}
