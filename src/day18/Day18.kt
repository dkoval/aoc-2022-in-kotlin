package day18

import readInput
import java.util.*
import kotlin.math.abs

private const val DAY_ID = "18"

private data class Rock(
    val x: Int,
    val y: Int,
    val z: Int
)

private data class Delta(
    val dx: Int,
    val dy: Int,
    val dz: Int
)

private data class Range(
    var min: Int = Int.MAX_VALUE,
    var max: Int = Int.MIN_VALUE
) {
    operator fun contains(value: Int): Boolean = value in min..max

    fun onNext(value: Int) {
        min = minOf(min, value)
        max = maxOf(max, value)
    }
}

fun main() {
    fun parseInput(input: List<String>): List<Rock> =
        input.map { line ->
            val (x, y, z) = line.split(",").map { it.toInt() }
            Rock(x, y, z)
        }

    fun part1(input: List<String>): Int {
        val rocks = parseInput(input)
        val n = rocks.size

        var common = 0
        for (i in 0 until n - 1) {
            for (j in i + 1 until n) {
                val (x1, y1, z1) = rocks[i]
                val (x2, y2, z2) = rocks[j]
                if ((abs(x1 - x2) == 1 && y1 == y2 && z1 == z2)
                    || (abs(y1 - y2) == 1 && x1 == x2 && z1 == z2)
                    || (abs(z1 - z2) == 1 && x1 == x2 && y1 == y2)) {
                    common++
                }
            }
        }
        return 6 * n - 2 * common
    }

    fun part2(input: List<String>): Int {
        val rocks = parseInput(input).toSet()

        val rangeX = Range()
        val rangeY = Range()
        val rangeZ = Range()

        for (rock in rocks) {
            rangeX.onNext(rock.x)
            rangeY.onNext(rock.y)
            rangeZ.onNext(rock.z)
        }

        val deltas = listOf(
            Delta(-1, 0, 0), Delta(1, 0, 0), // Ox
            Delta(0, -1, 0), Delta(0, 1, 0), // Oy
            Delta(0, 0, -1), Delta(0, 0, 1)) // Oz

        fun isTrapped(source: Rock): Boolean {
            // ~ bfs
            val q: Queue<Rock> = ArrayDeque()
            val seen: MutableSet<Rock> = mutableSetOf()

            q.offer(source)
            seen += source
            while (!q.isEmpty()) {
                val curr = q.poll()

                // must be an "empty" position
                if (curr in rocks) {
                    continue
                }

                // reached beyond rocks?
                if (curr.x !in rangeX || curr.y !in rangeY || curr.z !in rangeZ) {
                    return false
                }

                for ((dx, dy, dz) in deltas) {
                    val next = Rock(curr.x + dx, curr.y + dy, curr.z + dz)
                    if (next !in seen) {
                        q.offer(next)
                        seen += next
                    }
                }
            }
            return true
        }

        var ans = 0
        for ((x, y, z) in rocks) {
            for ((dx, dy, dz) in deltas) {
                val next = Rock(x + dx, y + dy, z + dz)
                if (!isTrapped(next)) {
                    ans++
                }
            }
        }
        return ans
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input)) // answer = 3454
    println(part2(input)) // answer = 2014
}
