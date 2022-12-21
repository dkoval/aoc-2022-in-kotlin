package day21

import readInput
import kotlin.math.abs

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

private sealed interface Eval {
    fun invoke(op: Operator, other: Eval): Eval

    // single number
    data class Numeric(val x: Long) : Eval {

        override fun invoke(op: Operator, other: Eval): Eval = when (other) {
            is Numeric -> Numeric(op(x, other.x))
            is Symbolic -> when (op) {
                // special case: handle x on the right side of expression:
                // a - x <-> x * (-1) + a
                Operator.SUBS -> other.invoke(Operator.MULT, Numeric(-1)).invoke(Operator.ADD, this)
                Operator.DIV -> error("Unexpected a / x expression")
                else -> other.invoke(op, this)
            }
        }
    }

    // p * x + q
    data class Symbolic(val p: Long, val q: Long, val d: Long) : Eval {

        companion object {
            val BASIC = Symbolic(1, 0, 1)
        }

        override fun invoke(op: Operator, other: Eval): Eval = when (other) {
            is Numeric -> when (op) {
                Operator.ADD -> {
                    Symbolic(p, q + other.x * d, d)
                }

                Operator.SUBS -> {
                    Symbolic(p, q - other.x * d, d)
                }

                Operator.MULT -> {
                    val gcd = gcd(other.x, d)
                    val m = other.x / gcd
                    Symbolic(p * m, q * m, d / gcd)
                }

                Operator.DIV -> {
                    val sign = compareValues(other.x, 0)
                    val gcd = gcd(gcd(p, q), other.x)
                    Symbolic(p / gcd * sign, q / gcd * sign, other.x / gcd * d)
                }
            }

            else -> error("Can't evaluate <symbolic> <operator> <symbolic> expression")
        }

        private fun gcd(a: Long, b: Long): Long {
            fun gcdRec(a: Long, b: Long): Long {
                return if (b == 0L) a else gcdRec(b, a % b)
            }
            return gcdRec(abs(a), abs(b))
        }
    }
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
                is Monkey.Math -> {
                    val lhs = evaluate(other1)
                    val rhs = evaluate(other2)
                    op(lhs, rhs)
                }
            }
        }
        return evaluate("root")
    }

    fun part2(input: List<String>): Long {
        val lookup = parseInput(input).associateBy { it.name }
        fun evaluate(name: String): Eval = with(lookup[name]!!) {
            when (this) {
                is Monkey.Primitive -> {
                    if (name == "humn") Eval.Symbolic.BASIC else Eval.Numeric(x)
                }

                is Monkey.Math -> {
                    val lhs = evaluate(other1)
                    val rhs = evaluate(other2)
                    lhs.invoke(op, rhs)
                }
            }
        }

        val root = lookup["root"] as Monkey.Math
        val lhs = evaluate(root.other1)
        val rhs = evaluate(root.other2)

        fun yell(lhs: Eval, rhs: Eval): Long {
            // (p * x + q) / d = c
            // p * x + q = c * d
            // x = (c * d - q) / p
            fun doYell(lhs: Eval.Symbolic, rhs: Eval.Numeric): Long {
                return (rhs.x * lhs.d - lhs.q) / lhs.p
            }

            return when {
                lhs is Eval.Symbolic && rhs is Eval.Numeric -> doYell(lhs, rhs)
                rhs is Eval.Symbolic && lhs is Eval.Numeric -> doYell(rhs, lhs)
                else -> error("Expected exactly one symbolic expression, but got: $lhs, $rhs")
            }
        }
        return yell(lhs, rhs)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = readInput("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input)) // answer = 70674280581468
    println(part2(input)) // answer = 3243420789721
}
