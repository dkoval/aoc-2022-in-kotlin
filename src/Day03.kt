fun main() {
    fun itemPriority(c: Char): Int = if (c.isLowerCase()) c - 'a' + 1 else c - 'A' + 27

    fun part1(input: List<String>): Int =
        input.sumOf { rucksack ->
            val h1 = rucksack.substring(0, rucksack.length / 2).toSet()
            val h2 = rucksack.substring(rucksack.length / 2).toSet()
            // find common item(s) in 2 compartments of a given rucksack
            h1.intersect(h2).sumOf { c -> itemPriority(c) }
        }

    fun part2(input: List<String>): Int =
        input.chunked(3) { group ->
            // find common item(s) in a group of 3 rucksacks
            val first = group.first().toSet()
            group.drop(1)
                .fold(first) { acc, rucksack -> acc.intersect(rucksack.toSet()) }
                .sumOf { c -> itemPriority(c) }
        }.sum()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input)) // answer = 7785
    println(part2(input)) // answer = 2633
}
