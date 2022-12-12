import java.util.*

private const val DAY_ID = "12"

private data class Cell(
    val row: Int,
    val col: Int
) {
    fun withinBoundaries(numRows: Int, numCols: Int): Boolean =
        row in 0 until numRows && col in 0 until numCols
}

private data class Input(
    val grid: List<List<Int>>,
    val source: Cell,
    val target: Cell,
    val a: List<Cell>
)

fun main() {
    fun parseInput(input: List<String>): Input {
        var source: Cell? = null
        var target: Cell? = null
        val a = mutableListOf<Cell>()
        val grid = input.mapIndexed{ row, line ->
            line.mapIndexed{ col, c ->
                var x = c
                when (c) {
                    'S' -> {
                        source = Cell(row, col)
                        x = 'a'
                    }
                    'E' -> {
                        target = Cell(row, col)
                        x = 'z'
                    }
                    'a' -> {
                        a += Cell(row, col)
                    }
                }
                x - 'a'
            }
        }
        return Input(
            grid,
            checkNotNull(source) { "S was not found in the grid" },
            checkNotNull(target) { "E was not found in the grid" },
            a
        )
    }

    fun bfs(grid: List<List<Int>>, source: Cell, target: Cell): Int {
        if (source == target) {
            return 0
        }

        val m = grid.size
        val n = grid[0].size
        val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        val q: Queue<Cell> = ArrayDeque()
        val visited: MutableSet<Cell> = hashSetOf()
        fun enqueue(cell: Cell) {
            q.offer(cell)
            visited += cell
        }

        var steps = 0
        enqueue(source)
        while (!q.isEmpty()) {
            val size = q.size
            repeat(size) {
                val curr = q.poll()
                for ((dx, dy) in directions) {
                    val next = Cell(curr.row + dx, curr.col + dy)
                    // out of boundaries or visited?
                    if (!next.withinBoundaries(m, n) || next in visited ) {
                        continue
                    }
                    // can go?
                    val h1 = grid[curr.row][curr.col]
                    val h2 = grid[next.row][next.col]
                    if (h2 < h1 || h2 - h1 <= 1) {
                        if (next == target) {
                            return steps + 1
                        }
                        enqueue(next)
                    }
                }
            }
            steps++
        }
        // there is no path starting at `source` and ending at `target`
        return -1
    }

    fun part1(input: List<String>): Int {
        val (grid, source, target) = parseInput(input)
        return bfs(grid, source, target)
    }

    fun part2(input: List<String>): Int {
        val (grid, source, target, a) = parseInput(input)
        var best = Int.MAX_VALUE
        for (x in a + source) {
            val steps = bfs(grid, x, target)
            if (steps != -1) {
                best = minOf(best, steps)
            }
        }
        return best
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${DAY_ID}_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day${DAY_ID}")
    println(part1(input)) // answer = 380
    println(part2(input)) // answer = 375
}
