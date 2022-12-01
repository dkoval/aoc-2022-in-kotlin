import java.util.*

fun main() {
    fun parseInput(input: String): List<List<Int>> =
        input.split("\n\n").map { group -> group.lines().map { it.toInt() } }

    fun part1(input: String): Int {
        val groups = parseInput(input)
        return groups.maxOf { it.sum() }
    }

    fun part2(input: String): Int {
        val groups = parseInput(input)

        // min heap to keep top k integers
        val k = 3
        val pq = PriorityQueue<Int>()

        for (group in groups) {
            pq.offer(group.sum())
            if (pq.size > k) {
                pq.poll()
            }
        }
        return pq.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInputAsString("Day01")
    println(part1(input)) // answer = 69310
    println(part2(input)) // answer = 206104
}
