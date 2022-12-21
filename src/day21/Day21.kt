package day21

import readInput

private const val DAY_ID = "21"

private enum class Operator : (Long, Long) -> Long {
    ADD {
        override fun invoke(a: Long, b: Long): Long = a + b
    },
    SUBS {
        override fun invoke(a: Long, b: Long): Long = a - b
    },
    MULT {
        override fun invoke(a: Long, b: Long): Long = a * b
    },
    DIV {
        override fun invoke(a: Long, b: Long): Long = a / b
    };

    companion object {
        fun fromString(s: String): Operator = when (s) {
            "+" -> ADD
            "-" -> SUBS
            "*" -> MULT
            "/" -> DIV
            else -> error("Unknown operation: $s")
        }
    }
}

private sealed class Monkey {
    abstract val name: String

    data class Primitive(
        override val name: String,
        val x: Long
    ) : Monkey()

    data class Math(
        override val name: String,
        val other1: String,
        val other2: String,
        val op: Operator
    ) : Monkey()
}

fun main() {
    fun parseInput(input: List<String>): List<Monkey> {
        val math = """([a-z]{4}) ([+\-*/]) ([a-z]{4})""".toRegex()
        return input.map { line ->
            val (name, expression) = line.split(": ")
            if (expression[0].isDigit()) {
                Monkey.Primitive(name, expression.toLong())
            } else {
                val (other1, op, other2) = math.find(expression)!!.destructured
                Monkey.Math(name, other1, other2, Operator.fromString(op))
            }
        }
    }

    fun part1(input: List<String>): Long {
        val lookup = parseInput(input).associateBy { it.name }
        fun evaluate(name: String): Long = with(lookup[name]!!) {
            when (this) {
                is Monkey.Primitive -> x
                is Monkey.Math -> op(evaluate(other1), evaluate(other2))
            }
        }

        return evaluate("root")
    }

    fun part2(input: List<String>): Long {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 152L)
    //check(part2(testInput) == 301L)

    val input = readInput("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input)) // answer = 70674280581468
    //println(part2(input))
}
