package day20

import readInput

private const val DAY_ID = "20"

fun main() {
    fun solve(input: List<String>, mixes: Int, transform: (x: Long) -> Long): Long {
        val nums = input.asSequence()
            .map { transform(it.toLong()) }
            .withIndex()
            .toMutableList()

        val n = nums.size
        repeat(mixes) { _ ->
            for (i in 0 until n) {
                val oldIdx = nums.indexOfFirst { it.index == i }
                val x = nums[oldIdx]

                val moves = (x.value % (n - 1)).toInt()
                if (moves == 0) continue

                var newIdx = oldIdx + moves
                if (newIdx <= 0) {
                    newIdx %= n - 1
                    newIdx += n - 1
                } else if (newIdx >= n) {
                    newIdx %= n - 1
                }

                nums.removeAt(oldIdx)
                nums.add(newIdx, x)
            }
        }

        val offsets = listOf(1000, 2000, 3000)
        val zeroIdx = nums.indexOfFirst { it.value == 0L }
        return offsets.fold(0L) { acc, offset ->
            val idx = (zeroIdx + offset) % n
            acc + nums[idx].value
        }
    }

    fun part1(input: List<String>): Long = solve(input, 1) { x -> x }

    fun part2(input: List<String>): Long = solve(input, 10) { x -> x * 811589153 }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input)) // answer = 4914
    println(part2(input)) // answer = 7973051839072
}
