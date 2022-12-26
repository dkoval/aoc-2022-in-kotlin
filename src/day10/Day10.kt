package day10

import readInput

private const val DAY_ID = "10"

fun main() {
    fun solve(input: List<String>, onCycle: (cycle: Int, x: Int) -> Boolean) {
        var x = 1
        var cycle = 1
        for (line in input) {
            // noop
            var delta = 0
            var cycleToComplete = 1

            // addx <delta>
            if (line.startsWith("addx")) {
                delta = line.removePrefix("addx ").toInt()
                cycleToComplete = 2
            }

            // execute instruction
            repeat(cycleToComplete) {
                if (onCycle(cycle++, x)) {
                    // early termination
                    return
                }
            }

            // finish execution by updating the state of the X register (only relevant for `addx`)
            x += delta
        }
    }

    fun part1(input: List<String>): Int {
        val cycles = setOf(20, 60, 100, 140, 180, 220)
        val signalStrengths = mutableListOf<Int>()

        solve(input) { cycle, x ->
            if (cycle in cycles) {
                signalStrengths += x * cycle
            }
            // condition for early termination
            signalStrengths.size == cycles.size
        }
        return signalStrengths.sum()
    }

    fun part2(input: List<String>): String {
        val width = 40
        val height = 6
        val screen = Array(height) { CharArray(width) { '.' } }

        solve(input) { cycle, x ->
            val row = (cycle - 1) / width
            val col = (cycle - 1) % width
            // if the sprite is positioned such that one of its three pixels is the pixel currently being drawn,
            // the screen produces a lit pixel
            if (col in (x - 1)..(x + 1)) {
                screen[row][col] = '@'
            }
            // no condition for early termination, process the input till the end
            false
        }
        return screen.joinToString(separator = "\n") { it.joinToString(separator = " ") }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 13140)

    val input = readInput("day${DAY_ID}/Day$DAY_ID")
    println(part1(input)) // answer = 14340
    println(part2(input)) // answer = PAPJCBHP
}
