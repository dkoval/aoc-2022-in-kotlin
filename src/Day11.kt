import java.util.*

private const val DAY_ID = "11"

private data class Item(val initial: Int) {
    // mod -> x % mod
    private val _remainder = mutableMapOf<Int, Int>()
    val remainder: Map<Int, Int> get() = _remainder

    fun update(x: Int, mods: Set<Int>) {
        for (mod in mods) {
            _remainder[mod] = x % mod
        }
    }

    fun add(x: Int, mods: Set<Int>): Item {
        for (mod in mods) {
            val new = _remainder[mod]!! + x % mod
            _remainder[mod] = new % mod
        }
        return this
    }

    fun addSelf(mods: Set<Int>): Item {
        for (mod in mods) {
            val old = _remainder[mod]!!
            _remainder[mod] = (old + old) % mod
        }
        return this
    }

    fun mult(x: Int, mods: Set<Int>): Item {
        for (mod in mods) {
            val new = _remainder[mod]!! * x % mod
            _remainder[mod] = new % mod
        }
        return this
    }

    fun multSelf(mods: Set<Int>): Item {
        for (mod in mods) {
            val old = _remainder[mod]!!
            _remainder[mod] = (old * old) % mod
        }
        return this
    }
}

private enum class Operation {
    ADD, MULT;

    companion object {
        fun fromString(s: String): Operation = when (s) {
            "+" -> ADD
            "*" -> MULT
            else -> error("Unknown operation: $s")
        }
    }
}

private sealed class Value {
    data class New(val x: Int) : Value()
    object Old : Value()

    companion object {
        fun fromString(s: String): Value = if (s == "old") Old else New(s.toInt())
    }
}

private data class Monkey<T>(
    val id: Int,
    val items: Deque<T>,
    val operation: Operation,
    val value: Value,
    val mod: Int,
    val throwToMonkeyIfTrue: Int,
    val throwToMonkeyOtherwise: Int,
    var inspectedItems: Int = 0
)

fun main() {
    fun <T> parseInput(input: String, transformItem: (item: Int) -> T, onMod: (mod: Int) -> Unit = {}): List<Monkey<T>> {
        val operationRegex = """old (\*|\+) (\d+|old)""".toRegex()
        return input.split("\n\n").mapIndexed { index, monkey ->
            val lines = monkey.split("\n")

            val items = lines[1].removePrefix("  Starting items: ").split(", ").map { transformItem(it.toInt()) }
            val (operation, value) = operationRegex.find(lines[2].removePrefix("  Operation: new = "))!!.destructured
            val mod = lines[3].removePrefix("  Test: divisible by ").toInt()
            val throwToMonkeyIfTrue = lines[4].removePrefix("    If true: throw to monkey ").toInt()
            val throwToMonkeyIfFalse = lines[5].removePrefix("    If false: throw to monkey ").toInt()

            onMod(mod)

            Monkey(
                index,
                ArrayDeque(items),
                Operation.fromString(operation),
                Value.fromString(value),
                mod,
                throwToMonkeyIfTrue,
                throwToMonkeyIfFalse
            )
        }
    }

    fun part1(input: String): Int {
        val rounds = 20
        val k = 3

        val data = parseInput(input, transformItem = { it })
        repeat(rounds) {
            for (monkey in data) {
                with(monkey) {
                    inspectedItems += items.size
                    while (!items.isEmpty()) {
                        val old = items.pollFirst()
                        val x = if (value is Value.New) value.x else old

                        var new = when (operation) {
                            Operation.ADD -> old + x
                            Operation.MULT -> old * x
                        }
                        new /= k

                        val throwTo = if (new % mod == 0) throwToMonkeyIfTrue else throwToMonkeyOtherwise
                        data[throwTo].items.offerLast(new)
                    }
                }
            }
        }

        return data.sortedByDescending { it.inspectedItems }
            .take(2)
            .fold(1) { acc, monkey -> acc * monkey.inspectedItems }
    }

    fun part2(input: String): Long {
        val rounds = 10000
        val mods = mutableSetOf<Int>()
        val data = parseInput(input, transformItem = { Item(it) }) { mod -> mods += mod }

        data.forEach { monkey ->
            monkey.items.forEach { item -> item.update(item.initial, mods) }
        }

        repeat(rounds) {
            for (monkey in data) {
                with(monkey) {
                    inspectedItems += items.size
                    while (!items.isEmpty()) {
                        val old = items.pollFirst()
                        val new = when (operation) {
                            Operation.ADD -> {
                                when (value) {
                                    is Value.Old -> old.addSelf(mods)
                                    is Value.New -> old.add(value.x, mods)
                                }
                            }

                            Operation.MULT -> {
                                when (value) {
                                    is Value.Old -> old.multSelf(mods)
                                    is Value.New -> old.mult(value.x, mods)
                                }
                            }
                        }

                        val test = new.remainder[mod]!!
                        val throwToMonkey = if (test == 0) throwToMonkeyIfTrue else throwToMonkeyOtherwise
                        data[throwToMonkey].items.offerLast(new)
                    }

                }
            }
        }

        return data.sortedByDescending { it.inspectedItems }
            .take(2)
            .fold(1L) { acc, monkey -> acc * monkey.inspectedItems }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("Day${DAY_ID}_test")
    check(part1(testInput) == 10605)
    check(part2(testInput) == 2713310158L)

    val input = readInputAsString("Day${DAY_ID}")
    println(part1(input)) // answer = 76728
    println(part2(input)) // answer = 21553910156
}
