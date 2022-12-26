package day14

import readInput

private const val DAY_ID = "14"

private data class Point(
    val x: Int,
    val y: Int
)

private data class Cave(
    val rocks: Set<Point>,
    val minX: Int,
    val maxY: Int
)

fun main() {
    fun parseInput(input: List<String>): List<List<Point>> =
        input.map { line ->
            line.split(" -> ").map { point ->
                val (x, y) = point.split(",").map { it.toInt() }
                Point(x, y)
            }
        }

    fun scanCave(data: List<List<Point>>): Cave {
        val rocks = mutableSetOf<Point>()
        var minX = Int.MAX_VALUE
        var maxY = Int.MIN_VALUE
        for (path in data) {
            for (i in 0 until path.size - 1) {
                val dx = -1 * compareValues(path[i].x, path[i + 1].x)
                val dy = -1 * compareValues(path[i].y, path[i + 1].y)

                var curr = path[i]
                val stop = Point(path[i + 1].x + dx, path[i + 1].y + dy)
                while (curr != stop) {
                    rocks += curr
                    minX = minOf(minX, curr.x)
                    maxY = maxOf(maxY, curr.y)
                    curr = Point(curr.x + dx, curr.y + dy)
                }
            }
        }
        return Cave(rocks, minX, maxY)
    }

    fun part1(input: List<String>): Int {
        val source = Point(500, 0)
        val data = parseInput(input)

        val directions = arrayOf(0 to 1, -1 to 1, 1 to 1) // down, down-left, down-right
        val cave = scanCave(data)

        var done = false
        val sands = mutableSetOf<Point>()
        while (!done) {
            var curr = source
            var canMove = true
            while (canMove) {
                var moved = false
                for ((dx, dy) in directions) {
                    val next = Point(curr.x + dx, curr.y + dy)
                    if (next.x < cave.minX || next.y > cave.maxY) {
                        done = true
                        break
                    }
                    if (next !in sands && next !in cave.rocks) {
                        curr = next
                        moved = true
                        break
                    }
                }
                canMove = moved
            }

            if (!done) {
                sands += curr
            }
        }
        return sands.size
    }

    fun part2(input: List<String>): Int {
        val source = Point(500, 0)
        val data = parseInput(input)

        val directions = arrayOf(0 to 1, -1 to 1, 1 to 1) // down, down-left, down-right
        val cave = scanCave(data)

        var done = false
        val sands = mutableSetOf<Point>()
        while (!done) {
            var curr = source
            var canMove = true
            while (canMove) {
                var moved = false
                for ((dx, dy) in directions) {
                    val next = Point(curr.x + dx, curr.y + dy)
                    if (next !in sands && next !in cave.rocks && next.y < cave.maxY + 2) {
                        curr = next
                        moved = true
                        break
                    }
                }
                canMove = moved
            }

            sands += curr
            done = curr == source
        }
        return sands.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("day${DAY_ID}/Day$DAY_ID")
    println(part1(input)) // answer = 755
    println(part2(input)) // answer = 29805
}
