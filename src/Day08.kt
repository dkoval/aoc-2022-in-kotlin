fun main() {
    fun parseInput(input: List<String>): List<List<Int>> =
        input.map { line -> line.map { it - '0' } }

    fun findVisibleTreesInInterior(grid: List<List<Int>>): Set<Pair<Int, Int>> {
        val m = grid.size
        val n = grid[0].size
        val visible = hashSetOf<Pair<Int, Int>>()

        // process interior rows
        for (row in 1 until m - 1) {
            // scan i-th row in both directions
            var lastLeft = grid[row][0]
            var lastRight = grid[row][n - 1]
            for (leftCol in 1 until n - 1) {
                if (grid[row][leftCol] > lastLeft) {
                    visible += row to leftCol
                    lastLeft = grid[row][leftCol]
                }

                val rightCol = n - leftCol - 1
                if (grid[row][rightCol] > lastRight) {
                    visible += row to rightCol
                    lastRight = grid[row][rightCol]
                }
            }
        }

        // process interior columns
        for (col in 1 until n - 1) {
            // scan i-th column in both directions
            var lastTop = grid[0][col]
            var lastBottom = grid[m - 1][col]
            for (topRow in 1 until m - 1) {
                if (grid[topRow][col] > lastTop) {
                    visible += topRow to col
                    lastTop = grid[topRow][col]
                }

                val bottomRow = m - topRow - 1
                if (grid[bottomRow][col] > lastBottom) {
                    visible += bottomRow to col
                    lastBottom = grid[bottomRow][col]
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

        val visible = findVisibleTreesInInterior(grid)
        var bestScore = 0
        for ((row, col) in visible) {
            // TODO: make more generic

            // look up
            var i = row - 1
            var distUp = 1
            while (i > 0 && grid[i][col] < grid[row][col]) {
                i--
                distUp++
            }

            // look down
            i = row + 1
            var distDown = 1
            while (i < m - 1 && grid[i][col] < grid[row][col]) {
                i++
                distDown++
            }

            // look left
            var j = col - 1
            var distLeft = 1
            while (j > 0 && grid[row][j] < grid[row][col]) {
                j--
                distLeft++
            }

            // look right
            j = col + 1
            var distRight = 1
            while (j < n - 1 && grid[row][j] < grid[row][col]) {
                j++
                distRight++
            }

            bestScore = maxOf(bestScore, distUp * distDown * distLeft * distRight)
        }
        return bestScore
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input)) // answer = 1560
    println(part2(input)) // answer = 252000
}
