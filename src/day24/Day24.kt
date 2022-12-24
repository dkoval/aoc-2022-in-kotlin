package day24

import readInput
import java.util.*

private const val DAY_ID = "24"

private enum class Direction(
    val dx: Int,
    val dy: Int
) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1),
    NOOP(0, 0);

    companion object {
        fun fromString(s: String): Direction = when (s) {
            "^" -> UP
            "v" -> DOWN
            "<" -> LEFT
            ">" -> RIGHT
            else -> error("Unknown direction: $s")
        }
    }
}

private data class Position(
    val row: Int,
    val col: Int
) {
    fun isInsideGrid(numRows: Int, numCols: Int): Boolean = (row in 0 until numRows) && (col in 0 until numCols)

    fun next(direction: Direction): Position = Position(row + direction.dx, col + direction.dy)

    fun nextCircular(d: Direction, numRows: Int, numCols: Int): Position {
        // wrap around, if needed
        fun mod(x: Int, m: Int): Int = if (x < 0) (m - (-x) % m) % m else x % m
        return Position(mod(row + d.dx, numRows), mod(col + d.dy, numCols))
    }
}

private data class TimedPosition(
    val time: Int,
    val position: Position
) {
    fun next(direction: Direction): TimedPosition = TimedPosition(time + 1, position.next(direction))
}

private data class Blizzard(
    val position: Position,
    val direction: Direction
)

private data class Grid(
    val source: Position,
    val target: Position,
    val blizzards: Set<Blizzard>,
    val numRows: Int,
    val numCols: Int
)

fun main() {
    fun parseInput(input: List<String>): Grid {
        val n = input.size

        // ignore surrounding '#'
        val numRows = n - 2
        val numCols = input[0].length - 2

        val source = Position(-1, input[0].indexOf('.') - 1)
        val target = Position(numRows, input[n - 1].indexOf('.') - 1)

        val blizzards = mutableSetOf<Blizzard>()
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                val c = input[row + 1][col + 1]
                if (c != '.') {
                    blizzards += Blizzard(Position(row, col), Direction.fromString("$c"))
                }
            }
        }
        return Grid(source, target, blizzards, numRows, numCols)
    }

    fun part1(input: List<String>): Int {
        val grid = parseInput(input)

        fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
        fun lcm(a: Int, b: Int): Int = a * b / gcd(a, b)

        // generate all possible states blizzards can be in
        val period = lcm(grid.numRows, grid.numCols)
        val states = mutableListOf(grid.blizzards)
        for (i in 1 until period) {
            val prev = states[i - 1]
            val curr = mutableSetOf<Blizzard>()
            // move blizzards
            for ((position, direction) in prev) {
                val newPosition = position.nextCircular(direction, grid.numRows, grid.numCols)
                curr += Blizzard(newPosition, direction)
            }
            states += curr
        }

        fun occupiedByBlizzards(position: Position, time: Int): Boolean {
            val blizzards = states[time % period]
            return Direction.values().any { Blizzard(position, it) in blizzards }
        }

        fun solve(source: Position, target: Position, time: Int): Int {
            // Dijkstra's algorithm
            val q: Queue<TimedPosition> = PriorityQueue(compareBy { it.time })
            val visited = mutableSetOf<TimedPosition>()
            fun enqueue(position: TimedPosition) {
                q.offer(position)
                visited += position
            }

            enqueue(TimedPosition(time, source))
            while (!q.isEmpty()) {
                val curr = q.poll()

                // move in each possible direction or wait in place
                for (direction in Direction.values()) {
                    val next = curr.next(direction)
                    if (next.position == target) {
                        return next.time
                    }

                    if (!next.position.isInsideGrid(grid.numRows, grid.numCols) || next in visited) {
                        continue
                    }

                    if (!occupiedByBlizzards(next.position, next.time)) {
                        enqueue(next)
                    }
                }
            }
            error("Couldn't find the shortest path from $source to $target")
        }

        return solve(grid.source, grid.target, 0)
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 18)
    //check(part2(testInput) == 54)

    val input = readInput("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input)) // answer = 292
    //println(part2(input))
}
