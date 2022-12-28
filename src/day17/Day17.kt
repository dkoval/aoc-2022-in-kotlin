package day17

import readInputAsString

private const val DAY_ID = "17"

private enum class Direction(val dx: Int, val dy: Int) {
    LEFT(-1, 0),
    RIGHT(1, 0),
    DOWN(0, -1);
}

private data class Point(val x: Int, val y: Int) {

    fun move(d: Direction): Point {
        return Point(x + d.dx, y + d.dy)
    }
}

private data class Rock(val pieces: Set<Point>) {
    private var _minX: Int = Int.MAX_VALUE
    private var _maxX: Int = Int.MIN_VALUE
    private var _minY: Int = Int.MAX_VALUE
    private var _maxY: Int = Int.MIN_VALUE

    init {
        for ((x, y) in pieces) {
            _minX = minOf(_minX, x)
            _maxX = maxOf(_maxX, x)
            _minY = minOf(_minY, y)
            _maxY = maxOf(_maxY, y)
        }
    }

    val minX: Int = _minX
    val maxX: Int = _maxX
    val minY: Int = _minY
    val maxY: Int = _maxY

    fun move(d: Direction): Rock {
        if (minX + d.dx < 0 || maxX + d.dx >= 7) {
            return this
        }
        return Rock(pieces.mapTo(mutableSetOf()) { it.move(d) })
    }

    companion object {
        // (x, y) stands for the left bottom corner
        fun create(x: Int, y: Int, shape: Int): Rock {
            val pieces = when (shape) {
                // ####
                0 -> setOf(Point(x, y), Point(x + 1, y), Point(x + 2, y), Point(x + 3, y))
                // .#.
                // ###
                // .#.
                1 -> setOf(Point(x + 1, y), Point(x, y + 1), Point(x + 1, y + 1), Point(x + 2, y + 1), Point(x + 1, y + 2))
                // ..#
                // ..#
                // ###
                2 -> setOf(Point(x, y), Point(x + 1, y), Point(x + 2, y), Point(x + 2, y + 1), Point(x + 2, y + 2))
                // #
                // #
                // #
                // #
                3 -> setOf(Point(x, y), Point(x, y + 1), Point(x, y + 2), Point(x, y + 3))
                // ##
                // ##
                4 -> setOf(Point(x, y), Point(x + 1, y), Point(x, y + 1), Point(x + 1, y + 1))
                else -> error("Unknown shape of rock: $shape")
            }
            return Rock(pieces)
        }
    }
}

fun main() {
    fun part1(input: String): Int {
        val rocks = 2022
        val n = input.length

        var i = 0
        var maxY = -1
        val fallen = mutableSetOf<Point>()
        repeat(rocks) { rock ->
            var curr = Rock.create(2, maxY + 4, rock % 5)
            while (true) {
                // jet of gas pushes the rock left or right
                val hor = when (val c = input[i++ % n]) {
                    '<' -> Direction.LEFT
                    '>' -> Direction.RIGHT
                    else -> error("Unknown direction: $c")
                }

                var next = curr.move(hor)
                if (next.pieces.none { it in fallen }) {
                    curr = next
                }

                // then the rock falls 1 unit
                next = curr.move(Direction.DOWN)
                if (next.minY < 0 || next.pieces.any { it in fallen }) {
                    fallen += curr.pieces
                    maxY = maxOf(maxY, curr.maxY)
                    break
                }
                curr = next
            }
        }
        return maxY + 1
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 3068)
    //check(part2(testInput) == 42)

    val input = readInputAsString("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input))
    //println(part2(input))
}
