private const val DAY_ID = "13"

private sealed class PacketItem {
    data class Num(val x: Int) : PacketItem() {
        fun toSeq(): Seq = Seq(listOf(this))
    }

    data class Seq(val items: List<PacketItem>) : PacketItem()

    companion object {
        fun fromString(s: String): PacketItem {
            var idx = 1
            fun traverse(items: MutableList<PacketItem>): List<PacketItem> {
                if (idx >= s.length) {
                    return items
                }

                val c = s[idx]
                idx++

                if (c == ']') {
                    return items
                }

                // parse Seq or Num
                when {
                    c == '[' -> items += Seq(traverse(mutableListOf()))
                    c.isDigit() -> {
                        var x = c.digitToInt()
                        while (s[idx].isDigit()) {
                            x *= 10
                            x += s[idx].digitToInt()
                            idx++
                        }
                        items += Num(x)
                    }
                }
                // proceed to the next index, also ignoring commas
                return traverse(items)
            }
            return Seq(traverse(mutableListOf()))
        }

        fun compare(left: PacketItem, right: PacketItem): Int {
            // base case #1
            if (left is Num && right is Num) {
                return compareValues(left.x, right.x)
            }

            // base case #2
            if (left is Seq && right is Seq) {
                var i = 0
                while (i <= minOf(left.items.size, right.items.size)) {
                    if (i == left.items.size) {
                        return if (left.items.size == right.items.size) 0 else -1
                    }

                    if (i == right.items.size) {
                        return 1
                    }

                    val res = compare(left.items[i], right.items[i])
                    if (res != 0) {
                        return res
                    }
                    i++
                }
                return 0
            }

            // if exactly one value is an integer, convert it to a list, then retry the comparison
            if (left is Num) {
                return compare(left.toSeq(), right)
            }

            if (right is Num) {
                return compare(left, right.toSeq())
            }
            return 0
        }
    }
}

fun main() {
    fun parseInput(input: String): List<Pair<PacketItem, PacketItem>> =
        input.split("\n\n").map { pair ->
            val pair = pair.split("\n").map { PacketItem.fromString(it) }
            pair[0] to pair[1]
        }

    fun part1(input: String): Int {
        val pairs = parseInput(input)

        val sum = pairs.foldIndexed(0) { index, acc, (left, right) ->
            // sum indices (1-indexed) of the pairs that are in the right order
            val ok = PacketItem.compare(left, right) < 0
            acc + if (ok) index + 1 else 0
        }
        return sum
    }

    fun part2(input: String): Int {
        val dividers = sequenceOf("[[2]]", "[[6]]").map { PacketItem.fromString(it) }.toList()

        val input = parseInput(input).flatMap { it.toList() }
        val items = dividers.toMutableList()
        items += input

        items.sortWith { left, right -> PacketItem.compare(left, right) }
        val key = dividers.fold(1) { acc, divider ->
            val found = items.withIndex()
                .find { (_, item) -> PacketItem.compare(item, divider) == 0 }
                ?: error("Not found: $divider")

            acc * (found.index + 1)
        }
        return key
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("Day${DAY_ID}_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInputAsString("Day${DAY_ID}")
    println(part1(input)) // answer = 5013
    println(part2(input)) // answer = 25038
}
