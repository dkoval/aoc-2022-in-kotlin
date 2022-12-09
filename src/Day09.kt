import kotlin.math.abs

private const val DAY_ID = "09"

private enum class Direction(val dx: Int, val dy: Int) {
    U(1, 0), D(-1, 0), L(0, -1), R(0, 1),
    UL(1, -1), UR(1, 1), DL(-1, -1), DR(-1, 1)
}

private data class Knot(
    var row: Int,
    var col: Int
) {
    fun move(d: Direction) {
        row += d.dx
        col += d.dy
    }

    fun isAdjacent(that: Knot): Boolean =
        abs(row - that.row) <= 1 && abs(col - that.col) <= 1
}

fun main() {
    fun solve(input: List<String>, n: Int): Int {
        val knots = Array(n) { Knot(0, 0) }
        val visited = mutableSetOf<Pair<Int, Int>>().also { it += 0 to 0 }

        fun move(d: Direction, steps: Int) {
            repeat(steps) {
                // move head
                knots[0].move(d)

                // move remaining knots, if needed
                for (i in 1 until n) {
                    val head = knots[i - 1]
                    val tail = knots[i]
                    if (tail.isAdjacent(head)) continue
                    when {
                        // head and tail are in the same row
                        tail.row == head.row -> tail.move(if (head.col > tail.col) Direction.R else Direction.L)
                        // head and tail are in the same column
                        tail.col == head.col -> tail.move(if (head.row > tail.row) Direction.U else Direction.D)
                        // head and tail aren't in the same row or column, therefore move tail one step diagonally to keep up
                        else -> {

                            if (head.row > tail.row) {
                                tail.move(if (tail.col < head.col) Direction.UR else Direction.UL)
                            } else {
                                tail.move(if (tail.col < head.col) Direction.DR else Direction.DL)
                            }
                        }
                    }
                }
                visited += knots[n - 1].row to knots[n - 1].col
            }
        }

        val data = input.map { line ->
            val (d, steps) = line.split(" ")
            enumValueOf<Direction>(d) to steps.toInt()
        }

        data.forEach { (d, steps) -> move(d, steps) }
        return visited.size
    }

    fun part1(input: List<String>): Int = solve(input, 2)

    fun part2(input: List<String>): Int = solve(input, 10)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${DAY_ID}_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)
    check(part2(readInput("Day${DAY_ID}_test_part2")) == 36)

    val input = readInput("Day${DAY_ID}")
    println(part1(input)) // answer = 6037
    println(part2(input)) // answer = 2485
}
