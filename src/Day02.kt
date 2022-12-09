private const val DAY_ID = "02"

private enum class Shape(val score: Int) {
    // rock
    A(1) {
        override fun wins(): Shape = C
        override fun loses(): Shape = B
    },

    // paper
    B(2) {
        override fun wins(): Shape = A
        override fun loses(): Shape = C
    },

    // scissors
    C(3) {
        override fun wins(): Shape = B
        override fun loses(): Shape = A
    };

    abstract fun wins(): Shape
    abstract fun loses(): Shape

    fun wins(opponent: Shape): Boolean = wins() == opponent
}

private enum class RoundEnd(val score: Int) {
    // you need to lose
    X(0),

    // you need to end the round in a draw
    Y(3),

    // you need to win
    Z(6)
}

fun main() {
    fun part1(input: List<String>): Int {
        fun playRound(me: Shape, opponent: Shape): Int = when {
            me == opponent -> 3
            me.wins(opponent) -> 6
            else -> 0
        }

        val shape = mapOf("X" to "A", "Y" to "B", "Z" to "C")
        return input.map { it.split(" ") }
            .sumOf { (s1, s2) ->
                val opponent = enumValueOf<Shape>(s1)
                val me = enumValueOf<Shape>(shape[s2]!!)
                me.score + playRound(me, opponent)
            }
    }

    fun part2(input: List<String>): Int {
        fun myResponse(opponent: Shape, roundEnd: RoundEnd): Shape = when (roundEnd) {
            // need to lose
            RoundEnd.X -> opponent.wins()
            // need to win
            RoundEnd.Z -> opponent.loses()
            // need to end the round in a draw
            else -> opponent
        }

        return input.map { it.split(" ") }
            .sumOf { (s1, s2) ->
                val opponent = enumValueOf<Shape>(s1)
                val roundEnd = enumValueOf<RoundEnd>(s2)
                myResponse(opponent, roundEnd).score + roundEnd.score
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${DAY_ID}_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day${DAY_ID}")
    println(part1(input)) // answer = 14375
    println(part2(input)) // answer = 10274
}
