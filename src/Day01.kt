import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        var sum = 0
        var best = 0
        for (s in input) {
            if (s.isNotEmpty()) {
                sum += s.toInt()
                best = maxOf(best, sum)
            } else {
                sum = 0
            }
        }
        return best
    }

    fun part2(input: List<String>): Int {
        // keep top k integers
        val k = 3
        val minHeap = PriorityQueue<Int>()

        fun enqueue(x: Int) {
            minHeap.offer(x)
            if (minHeap.size > k) {
                minHeap.poll()
            }
        }

        var sum = 0
        for (s in input) {
            if (s.isNotEmpty()) {
                sum += s.toInt()
            } else {
                enqueue(sum)
                sum = 0
            }
        }

        enqueue(sum)
        return minHeap.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
