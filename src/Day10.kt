private const val DAY_ID = "10"

fun main() {
    fun part1(input: List<String>): Int {
        val cycles = mutableSetOf(20, 60, 100, 140, 180, 220)

        var x = 1
        var i = 1
        val strengths = mutableListOf<Int>()
        for (line in input) {
            if (line.startsWith("addx")) {
                // addx <delta>
                val delta = line.removePrefix("addx ").toInt()
                // evaluate "start", "during" and "finish" phases of an "addx" instruction
                for ((step, inc) in arrayOf(0 to 0, 1 to 0, 2 to delta)) {
                    val cycle = i + step
                    if (cycle in cycles) {
                        strengths += (x + inc) * cycle
                        cycles -= cycle
                    }
                }
                x += delta
                i += 2
                if (cycles.isEmpty()) {
                    break
                }
            } else {
                // noop
                i++
            }
        }
        return strengths.sum()
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
                // "start" and "during" phases of an "addx" instruction do not modify the state of the register X
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
