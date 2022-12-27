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
    RIGHT(0, 1);

    companion object {
        fun fromChar(c: Char): Direction = when (c) {
            '^' -> UP
            'v' -> DOWN
            '<' -> LEFT
            '>' -> RIGHT
            else -> error("Unknown direction: $c")
        }
    }
}

private data class Position(
    val row: Int,
    val col: Int
) {
    fun isInBounds(numRows: Int, numCols: Int): Boolean = (row in 0 until numRows) && (col in 0 until numCols)

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
    val start: Position,
    val goal: Position,
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
                    blizzards += Blizzard(Position(row, col), Direction.fromChar(c))
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

        fun solve(start: Position, goal: Position, time: Int): Int {
            // BFS
            val q: Queue<TimedPosition> = ArrayDeque()
            val visited = mutableSetOf<TimedPosition>()
            fun enqueue(item: TimedPosition) {
                q.offer(item)
                visited += item
            }

            enqueue(TimedPosition(time, start))
            while (!q.isEmpty()) {
                val curr = q.poll()

                // move in each possible direction
                for (direction in Direction.values()) {
                    val next = curr.next(direction)
                    if (next.position == goal) {
                        return next.time
                    }

                    if (!next.position.isInBounds(grid.numRows, grid.numCols) || next in visited) {
                        continue
                    }

                    if (!occupiedByBlizzards(next.position, next.time)) {
                        enqueue(next)
                    }
                }

                // wait in place
                if (!occupiedByBlizzards(curr.position, curr.time + 1)) {
                    enqueue(TimedPosition(curr.time + 1, curr.position))
                }
            }
            error("Couldn't find the shortest path from $start to $goal")
        }

        return solve(grid.start, grid.goal, 0)
    }

    fun part2(input: List<String>): Int {
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

        fun solve(start: Position, goal: Position): Int {
            // BFS
            val q: Queue<Pair<TimedPosition, Int>> = ArrayDeque()
            val visited = mutableSetOf<Pair<TimedPosition, Int>>()
            fun enqueue(item: Pair<TimedPosition, Int>) {
                q.offer(item)
                visited += item
            }

            // extra `pass` argument in the state:
            // 0 - 1st trip from start to goal
            // 1 - trip back from goal to start
            // 2 - trip back from start to goal again
            enqueue(TimedPosition(0, start) to 0)
            while (!q.isEmpty()) {
                val (curr, pass) = q.poll()

                // move in each possible direction
                for (direction in Direction.values()) {
                    val next = curr.next(direction)
                    when {
                        next.position == goal && (pass == 0 || pass == 2) -> {
                            if (pass == 0) {
                                // now trip back to the start
                                q.offer(next to 1)
                            } else {
                                return next.time
                            }
                        }

                        next.position == start && pass == 1 -> {
                            // trip back to the goal again
                            q.offer(next to 2)
                            break
                        }

                        else -> {
                            if (!next.position.isInBounds(grid.numRows, grid.numCols)
                                || occupiedByBlizzards(next.position, next.time)
                            ) {
                                continue
                            }

                            val nextState = next to pass
                            if (nextState !in visited) {
                                enqueue(nextState)
                            }
                        }
                    }
                }

                // wait in place
                if (!occupiedByBlizzards(curr.position, curr.time + 1)) {
                    enqueue(TimedPosition(curr.time + 1, curr.position) to pass)
                }
            }
            error("Couldn't find the shortest path from $start to $goal")
        }

        return solve(grid.start, grid.goal)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 54)

    val input = readInput("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input)) // answer = 292
    println(part2(input)) // answer = 816
}
