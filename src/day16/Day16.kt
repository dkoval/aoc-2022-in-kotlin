package day16

import readInput

private const val DAY_ID = "16"

private data class Valve(
    val id: String,
    val rate: Int,
    val adj: List<String>
)

private data class State(
    val valve: String,
    val time: Int,
    val opened: Set<String>
)

fun main() {

    fun parseInput(input: List<String>): List<Valve> {
        val valve = """Valve ([A-Z]{2}) has flow rate=(\d+)""".toRegex()
        val tunnels = """tunnels? leads? to valves? (.*)""".toRegex()
        return input.map { line ->
            val (s1, s2) = line.split("; ")
            val (id, rate) = valve.let { it.find(s1)!!.destructured }
            val adj = tunnels.let { it.find(s2)!!.groupValues[1] }.split(", ")
            Valve(id, rate.toInt(), adj)
        }
    }

    fun part1(input: List<String>): Int {
        val valves = parseInput(input)

        // DP top-down
        val lookup = valves.associateBy { it.id }
        val memo = mutableMapOf<State, Int>()
        fun solve(valve: String, time: Int, opened: Set<String>): Int {
            if (time == 0) {
                return 0
            }

            // already solved?
            val key = State(valve, time, opened)
            if (key in memo) {
                return memo[key]!!
            }

            val curr = lookup[valve]!!
            var best = 0

            // option #1: open the current valve to release more pressure remaining (time - 1) minutes
            if (curr.id !in opened && curr.rate > 0) {
                best = maxOf(best, solve(curr.id, time - 1, opened + curr.id) + (time - 1) * curr.rate)
            }

            // option #2: skip the current valve
            for (neighbor in curr.adj) {
                best = maxOf(best, solve(neighbor, time - 1, opened))
            }

            // cache and return the answer
            memo[key] = best
            return best
        }

        return solve("AA", 30, setOf())
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 1651)
    //check(part2(testInput) == 42)

    val input = readInput("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input))
    // println(part2(input))
}
