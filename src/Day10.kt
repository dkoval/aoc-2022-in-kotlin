private const val DAY_ID = "10"

fun main() {
    fun part1(input: List<String>): Int {
        val cycles = setOf(20, 60, 100, 140, 180, 220)

        var x = 1
        val signalStrengths = mutableListOf<Int>()
        fun checkCycle(cycle: Int) {
            if (cycle in cycles) {
                signalStrengths += x * cycle
            }
        }

        var i = 1
        for (line in input) {
            if (line.startsWith("addx")) {
                // addx <delta>
                val delta = line.removePrefix("addx ").toInt()
                // "start" and "during" phases of an "addx" instruction do not modify x
                repeat(2) {
                    checkCycle(i++)
                }
                x += delta
            } else {
                // noop
                checkCycle(i++)
            }
        }
        return signalStrengths.sum()
    }

    fun part2(input: List<String>): String {
        val width = 40
        val height = 6
        val screen = Array(height) { CharArray(width) { '.' } }

        var x = 1 // middle of the 3 pixels wide sprite
        fun printPixel(i: Int) {
            val row = i / width
            val col = i % width
            // if the sprite is positioned such that one of its three pixels is the pixel currently being drawn,
            // the screen produces a lit pixel
            if (col in (x - 1)..(x + 1)) {
                screen[row][col] = '@'
            }
        }

        var i = 0
        input.forEach { line ->
            if (line.startsWith("addx")) {
                // addx <delta>
                val delta = line.removePrefix("addx ").toInt()
                // "start" and "during" phases of an "addx" instruction do not modify x
                repeat(2) {
                    printPixel(i++)
                }
                x += delta
            } else {
                // noop
                printPixel(i++)
            }
        }
        return screen.joinToString(separator = "\n") { it.joinToString(separator = " ") }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${DAY_ID}_test")
    check(part1(testInput) == 13140)

    val input = readInput("Day${DAY_ID}")
    println(part1(input)) // answer = 14340
    println(part2(input)) // answer = PAPJCBHP
}
