import java.util.*

private const val DAY_ID = "11"

private data class Monkey(
    val id: Int,
    val items: List<Int>,
    val operator: Operator,
    val v: Value,
    val divisor: Int,
    val throwToIfTrue: Int,
    val throwToIfFalse: Int
)

private enum class Operator {
    ADD, MULT;

    companion object {
        fun fromString(s: String): Operator = when (s) {
            "+" -> ADD
            "*" -> MULT
            else -> error("Unknown operation: $s")
        }
    }
}

private sealed class Value {
    data class Num(val x: Int) : Value()
    object Old : Value()

    companion object {
        fun fromString(s: String): Value = if (s == "old") Old else Num(s.toInt())
    }
}

private sealed interface WorryLevel {
    operator fun plus(v: Value): WorryLevel
    operator fun times(v: Value): WorryLevel
    infix fun isDivisibleBy(divisor: Int): Boolean

    class Part1(initial: Int, private val k: Int) : WorryLevel {
        private var x = initial

        override fun plus(v: Value): WorryLevel {
            x += v.get()
            x /= k
            return this
        }

        override fun times(v: Value): WorryLevel {
            x *= v.get()
            x /= k
            return this
        }

        override fun isDivisibleBy(divisor: Int): Boolean = x % divisor == 0

        private fun Value.get() = when (this) {
            is Value.Num -> this.x
            is Value.Old -> x
        }
    }

    class Part2(initial: Int, private val divisors: Set<Int>) : WorryLevel {
        // (a + b) % c = (a % c + b % c) % c
        // (a * b) % c = (a % c * b % c) % c
        private val remainders = divisors.associateWithTo(mutableMapOf()) { divisor -> initial % divisor }

        override fun plus(v: Value): WorryLevel {
            for (divisor in divisors) {
                var new = remainders[divisor]!!
                new += v.get(divisor)
                new %= divisor
                remainders[divisor] = new
            }
            return this
        }

        override fun times(v: Value): WorryLevel {
            for (divisor in divisors) {
                var new = remainders[divisor]!!
                new *= v.get(divisor)
                new %= divisor
                remainders[divisor] = new
            }
            return this
        }

        override fun isDivisibleBy(divisor: Int): Boolean = remainders[divisor] == 0

        private fun Value.get(divisor: Int): Int = when (this) {
            is Value.Num -> this.x % divisor
            is Value.Old -> checkNotNull(remainders[divisor]) { "Unexpected divisor: $divisor" }
        }
    }
}

private fun WorryLevel.invoke(operator: Operator, v: Value): WorryLevel = when (operator) {
    Operator.ADD -> plus(v)
    Operator.MULT -> times(v)
}

fun main() {
    fun parseInput(input: String): List<Monkey> {
        val operation = """old (\*|\+) (\d+|old)""".toRegex()
        return input.split("\n\n").mapIndexed { index, s ->
            val lines = s.split("\n")

            val items = lines[1].removePrefix("  Starting items: ").split(", ").map { it.toInt() }
            val (operator, v) = operation.find(lines[2].removePrefix("  Operation: new = "))!!.destructured
            val divisor = lines[3].removePrefix("  Test: divisible by ").toInt()
            val throwToMonkeyIfTrue = lines[4].removePrefix("    If true: throw to monkey ").toInt()
            val throwToMonkeyIfFalse = lines[5].removePrefix("    If false: throw to monkey ").toInt()

            Monkey(
                index,
                items,
                Operator.fromString(operator),
                Value.fromString(v),
                divisor,
                throwToMonkeyIfTrue,
                throwToMonkeyIfFalse
            )
        }
    }

    fun solve(monkeys: List<Monkey>, rounds: Int, transform: (x: Int) -> WorryLevel): Long {
        val n = monkeys.size
        val state: List<Queue<WorryLevel>> = monkeys.map { it.items.mapTo(ArrayDeque()) { x -> transform(x) } }
        val inspectedItems = IntArray(n)

        repeat(rounds) {
            state.forEachIndexed { index, items ->
                with(monkeys[index]) {
                    inspectedItems[index] += items.size
                    while (!items.isEmpty()) {
                        val old = items.poll()
                        val new = old.invoke(operator, v)
                        val throwTo = if (new isDivisibleBy divisor) throwToIfTrue else throwToIfFalse
                        state[throwTo].offer(new)
                    }
                }
            }
        }

        return inspectedItems
            .also { it.sortDescending() }
            .take(2)
            .fold(1L) { acc, x -> acc * x }
    }

    fun part1(input: String): Long {
        val rounds = 20
        val k = 3
        val monkeys = parseInput(input)
        return solve(monkeys, rounds) { x -> WorryLevel.Part1(x, k) }
    }

    fun part2(input: String): Long {
        val rounds = 10000
        val monkeys = parseInput(input)
        val divisors = monkeys.asSequence().map { it.divisor }.toSet()
        return solve(monkeys, rounds) { x -> WorryLevel.Part2(x, divisors) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("Day${DAY_ID}_test")
    check(part1(testInput) == 10605L)
    check(part2(testInput) == 2713310158L)

    val input = readInputAsString("Day${DAY_ID}")
    println(part1(input)) // answer = 76728
    println(part2(input)) // answer = 21553910156
}
