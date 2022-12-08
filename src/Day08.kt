fun main() {
    fun parseInput(input: List<String>): List<List<Int>> =
        input.map { line -> line.map { it - '0' } }

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
            for (leftCol in 1 until n - 1) {
                if (grid[row][leftCol] > last[0]) {
                    last[0] = grid[row][leftCol]
                    visible += row to leftCol
                }

                val rightCol = n - leftCol - 1
                if (grid[row][rightCol] > last[1]) {
                    last[1] = grid[row][rightCol]
                    visible += row to rightCol
                }
            }
        }

        // process interior columns
        for (col in 1 until n - 1) {
            // scan i-th column in both directions
            last[0] = grid[0][col]
            last[1] = grid[m - 1][col]
            for (topRow in 1 until m - 1) {
                if (grid[topRow][col] > last[0]) {
                    last[0] = grid[topRow][col]
                    visible += topRow to col
                }

                val bottomRow = m - topRow - 1
                if (grid[bottomRow][col] > last[1]) {
                    last[1] = grid[bottomRow][col]
                    visible += bottomRow to col
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
            var dist = 1
            var nextRow = row + dx
            var nextCol = col + dy
            while (nextRow in 1 until m - 1 && nextCol in 1 until n - 1 && grid[nextRow][nextCol] < grid[row][col]) {
                dist++
                nextRow += dx
                nextCol += dy
            }
            return dist
        }

        val visible = findVisibleTreesInInterior(grid)
        val deltas = arrayOf(-1 to 0, 0 to -1, 1 to 0, 0 to 1) // look up, left, down, right

        return visible.fold(0) { bestScore, (row, col) ->
            val score = deltas.fold(1) { acc, (dx, dy) -> acc * viewingDistance(row, col, dx, dy) }
            maxOf(bestScore, score)
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
