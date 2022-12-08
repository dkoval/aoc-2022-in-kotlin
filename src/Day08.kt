fun main() {
    fun parseInput(input: List<String>): List<List<Int>> =
        input.map { line -> line.map { it.digitToInt() } }

    fun findVisibleTreesInInterior(grid: List<List<Int>>): Set<Pair<Int, Int>> {
        val m = grid.size
        val n = grid[0].size
        val visible = hashSetOf<Pair<Int, Int>>()

        // last[0] - the last highest tree when going from left-to-right (in a row) or top-to-bottom (in a column)
        // last[1] - the last highest tree when going from right-to-left (in a row) or bottom-to-top (in a column)
        val last = IntArray(2)

        // process interior rows
        for (row in 1 until m - 1) {
            // scan i-th row in both directions
            last[0] = grid[row][0]
            last[1] = grid[row][n - 1]
            for (left in 1 until n - 1) {
                if (grid[row][left] > last[0]) {
                    last[0] = grid[row][left]
                    visible += row to left
                }
                val right = n - left - 1
                if (grid[row][right] > last[1]) {
                    last[1] = grid[row][right]
                    visible += row to right
                }
            }
        }

        // process interior columns
        for (col in 1 until n - 1) {
            // scan i-th column in both directions
            last[0] = grid[0][col]
            last[1] = grid[m - 1][col]
            for (top in 1 until m - 1) {
                if (grid[top][col] > last[0]) {
                    last[0] = grid[top][col]
                    visible += top to col
                }
                val bottom = m - top - 1
                if (grid[bottom][col] > last[1]) {
                    last[1] = grid[bottom][col]
                    visible += bottom to col
                }
            }
        }
        return visible
    }

    fun part1(input: List<String>): Int {
        val grid = parseInput(input)
        val m = grid.size
        val n = grid[0].size

        val visible = findVisibleTreesInInterior(grid)
        return 2 * (m + n - 2) + /* visible on the edge */ + visible.size /* visible in the interior */
    }

    fun part2(input: List<String>): Int {
        val grid = parseInput(input)
        val m = grid.size
        val n = grid[0].size

        fun viewingDistance(row: Int, col: Int, dx: Int, dy: Int): Int {
            var dist = 0
            var nextRow = row + dx
            var nextCol = col + dy
            while (nextRow in 0 until m && nextCol in 0 until n) {
                dist++
                if (grid[nextRow][nextCol] >= grid[row][col]) {
                    break
                }
                nextRow += dx
                nextCol += dy
            }
            return dist
        }

        val visible = findVisibleTreesInInterior(grid)
        val deltas = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1) // look up, down, left, right
        return visible.maxOf { (row, col) ->
            deltas.fold(1) { acc, (dx, dy) -> acc * viewingDistance(row, col, dx, dy) }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input)) // answer = 1560
    println(part2(input)) // answer = 252000
}
