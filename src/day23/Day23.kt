package day23

import readInput

private const val DAY_ID = "23"

private enum class Direction(val dx: Int, val dy: Int) {
    N(-1, 0),
    S(1, 0),
    W(0, -1),
    E(0, 1),
    NW(-1, -1),
    NE(-1, 1),
    SW(1, -1),
    SE(1, 1)
}

private data class Position(val row: Int, val col: Int) {
    fun next(d: Direction): Position = Position(row + d.dx, col + d.dy)
}

private sealed class MoveProposal {
    object Bad : MoveProposal()
    data class Good(val source: Position) : MoveProposal()
}

private data class SimulationResult(val grid: Set<Position>, val rounds: Int)

fun main() {
    fun parseInput(input: List<String>): MutableSet<Position> =
        input.foldIndexed(sortedSetOf(compareBy({ it.row }, { it.col }))) { row, acc, line ->
            line.forEachIndexed { col, c ->
                if (c == '#') {
                    acc += Position(row, col)
                }
            }
            acc
        }

    fun runSimulation(input: List<String>, maxRounds: Int = Int.MAX_VALUE): SimulationResult {
        val grid = parseInput(input)

        fun hasNoElvesAround(curr: Position, vararg ds: Direction): Boolean {
            check(ds.isNotEmpty())
            return ds.none { d ->
                val next = curr.next(d)
                next in grid
            }
        }

        fun canMove(curr: Position, d: Direction): Boolean = when (d) {
            Direction.N -> hasNoElvesAround(curr, Direction.N, Direction.NE, Direction.NW)
            Direction.S -> hasNoElvesAround(curr, Direction.S, Direction.SE, Direction.SW)
            Direction.W -> hasNoElvesAround(curr, Direction.W, Direction.NW, Direction.SW)
            Direction.E -> hasNoElvesAround(curr, Direction.E, Direction.NE, Direction.SE)
            else -> false
        }

        // 4 directions for an elf to propose moving to
        val directions = listOf(Direction.N, Direction.S, Direction.W, Direction.E)
        var offset = 0

        var rounds = 1
        while(rounds <= maxRounds) {
            // next -> curr
            val proposals = mutableMapOf<Position, MoveProposal>()

            // 1st half of each round: each Elf considers the eight positions adjacent to himself
            for (curr in grid) {
                // 8 adjacent positions to (row, col)
                val ok = hasNoElvesAround(curr, *Direction.values())
                if (ok) {
                    continue
                }

                // propose moving one step in the first valid direction
                for (i in 0 until 4) {
                    // choose direction
                    val d = directions[(i + offset) % 4]
                    val canMove = canMove(curr, d)
                    if (canMove) {
                        val next = curr.next(d)
                        proposals[next] = if (next !in proposals) MoveProposal.Good(curr) else MoveProposal.Bad
                        break
                    }
                }
            }

            // the first round where no Elf moves; no need to continue the simulation
            if (proposals.isEmpty()) {
                break
            }

            // 2nd half of the round
            proposals.asSequence()
                .filter { (_, proposal) -> proposal is MoveProposal.Good }
                .forEach { (next, proposal) ->
                    val curr = (proposal as MoveProposal.Good).source
                    grid -= curr
                    grid += next
                }

            // end of the round
            offset++
            offset %= 4
            rounds++
        }
        return SimulationResult(grid, rounds)
    }

    fun part1(input: List<String>): Int {
        val rounds = 10
        val (grid, _) = runSimulation(input, rounds)

        // the edges of the smallest rectangle that contains every Elf
        var minRow = Int.MAX_VALUE
        var maxRow = Int.MIN_VALUE
        var minCol = Int.MAX_VALUE
        var maxCol = Int.MIN_VALUE

        grid.forEach {
            minRow = minOf(minRow, it.row)
            maxRow = maxOf(maxRow, it.row)
            minCol = minOf(minCol, it.col)
            maxCol = maxOf(maxCol, it.col)
        }

        // count the number of empty ground tiles
        return (maxRow - minRow + 1) * (maxCol - minCol + 1) - grid.size
    }

    fun part2(input: List<String>): Int {
        val (_, rounds) = runSimulation(input)
        return rounds
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    val input = readInput("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input)) // answer = 3874
    println(part2(input)) // answer = 948
}
