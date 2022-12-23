package day22

import readInputAsString

private const val DAY_ID = "22"

private enum class Turn {
    // turn 90 degrees clockwise
    R,

    // turn 90 degrees counterclockwise
    L
}

private enum class Facing(val score: Int, val dx: Int, val dy: Int) {
    RIGHT(0, 0, 1) {
        override fun change(turn: Turn): Facing = when (turn) {
            Turn.R -> DOWN
            Turn.L -> UP
        }
    },
    DOWN(1, 1, 0) {
        override fun change(turn: Turn): Facing = when (turn) {
            Turn.R -> LEFT
            Turn.L -> RIGHT
        }
    },
    LEFT(2, 0, -1) {
        override fun change(turn: Turn): Facing = when (turn) {
            Turn.R -> UP
            Turn.L -> DOWN
        }
    },
    UP(3, -1, 0) {
        override fun change(turn: Turn): Facing = when (turn) {
            Turn.R -> RIGHT
            Turn.L -> LEFT
        }
    };

    abstract fun change(turn: Turn): Facing
}

private data class Board(val rows: List<RowInfo>, val cols: List<ColInfo>) {

    // row = x + offsetX
    data class RowInfo(private val items: List<Boolean>, val offsetY: Int) {
        val size: Int = items.size
        operator fun get(col: Int): Boolean = items[col]
    }

    // col = y + offsetY
    data class ColInfo(val size: Int, val offsetX: Int)

    companion object {
        fun fromString(s: String): Board {
            // since key is an int, the natural order of columns will be preserved;
            // cols[y][0] - size of column y
            // cols[y][1] - X offset
            val cols = hashMapOf<Int, IntArray>()
            val rows = s.lines().mapIndexed { x, line ->
                var offsetY = 0
                val items = mutableListOf<Boolean>()
                line.forEachIndexed { y, c ->
                    if (!c.isWhitespace()) {
                        if (items.isEmpty()) {
                            offsetY = y
                        }
                        items += c == '.'
                        cols.getOrPut(y) { intArrayOf(0, x) }[0]++
                    }
                }
                RowInfo(items, offsetY)
            }
            return Board(rows, cols.values.map { (size, offsetX) -> ColInfo(size, offsetX) })
        }
    }
}

private sealed class Move {
    data class Number(val steps: Int) : Move()
    data class Letter(val turn: Turn) : Move()
}

private data class Path(val moves: List<Move>) {
    companion object {
        fun fromString(s: String): Path {
            var i = 0
            var x = 0
            val moves = mutableListOf<Move>()
            while (i < s.length) {
                if (s[i].isDigit()) {
                    x *= 10
                    x += s[i].digitToInt()
                } else {
                    // "10R..." vs possible? "R10..."
                    if (s.first().isDigit()) {
                        moves += Move.Number(x)
                    }
                    moves += Move.Letter(enumValueOf("${s[i]}"))
                    x = 0
                }
                i++
            }
            // "...L5" vs possible? "...5L"
            if (s.last().isDigit()) {
                moves += Move.Number(x)
            }
            return Path(moves)
        }
    }
}

private data class Note(val board: Board, val path: Path)

fun main() {
    fun parseInput(input: String): Note {
        val (board, path) = input.split("\n\n")
        return Note(Board.fromString(board), Path.fromString(path))
    }

    fun mod(x: Int, m: Int): Int = if (x < 0) (m - (-x) % m) % m else x % m

    fun part1(input: String): Int {
        val (board, path) = parseInput(input)

        var row = 0
        var y = -1
        var facing = Facing.RIGHT
        for (move in path.moves) {
            when (move) {
                is Move.Letter -> facing = facing.change(move.turn)
                is Move.Number -> {
                    for (step in 0 until move.steps) {
                        val oldRow = row
                        val oldY = y
                        // move to the next tile and wrap around if needed
                        when (facing) {
                            Facing.LEFT, Facing.RIGHT -> {
                                // x doesn't change
                                y += facing.dy
                                y = mod(y, board.rows[row].size)
                            }
                            Facing.UP, Facing.DOWN -> {
                                // y doesn't change
                                val col = y + board.rows[row].offsetY

                                row -= board.cols[col].offsetX
                                row += facing.dx
                                row = mod(row, board.cols[col].size)
                                row += board.cols[col].offsetX

                                // recompute y in a new row
                                y = col - board.rows[row].offsetY
                            }
                        }

                        // hit the wall?
                        if (!board.rows[row][y]) {
                            row = oldRow
                            y = oldY
                            break
                        }
                    }
                }
            }
        }

        val col = y + board.rows[row].offsetY
        return 1000 * (row + 1) + 4 * (col + 1) + facing.score
    }

    fun part2(input: String): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 6032)
    //check(part2(testInput) == 42)

    val input = readInputAsString("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input)) // answer = 76332
    //println(part2(input))
}
