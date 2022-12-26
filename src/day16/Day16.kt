package day16

import readInput
import kotlin.system.measureTimeMillis

private const val DAY_ID = "16"

private data class Valve(
    val id: String,
    val rate: Int,
    val adj: List<String>
)

fun main() {
    fun parseInput(input: List<String>): Map<String, Valve> {
        val valve = """Valve ([A-Z]{2}) has flow rate=(\d+)""".toRegex()
        val tunnels = """tunnels? leads? to valves? (.*)""".toRegex()
        return input.asSequence()
            .map { line ->
                val (s1, s2) = line.split("; ")
                val (id, rate) = valve.let { it.find(s1)!!.destructured }
                val adj = tunnels.let { it.find(s2)!!.groupValues[1] }.split(", ")
                Valve(id, rate.toInt(), adj)
            }.associateBy { it.id }
    }

    fun solve(lookup: Map<String, Valve>, start: String, maxTime: Int, withElephant: Boolean): Int {
        data class Key(
            val valve: String,
            val time: Int,
            val opened: Set<String>,
            val withElephant: Boolean
        )

        // DP top-down
        val dp = mutableMapOf<Key, Int>()
        fun traverse(valve: String, time: Int, opened: Set<String>, withElephant: Boolean): Int {
            // base case
            if (time == 0) {
                // work alone first, then get an elephant to help you and make him aware about the opened valves;
                // this will simulate the process of two of you working together
                if (withElephant) {
                    return traverse(start, maxTime, opened, false)
                }
                return 0
            }

            // already solved?
            val key = Key(valve, time, opened, withElephant)
            if (key in dp) {
                return dp[key]!!
            }

            val curr = lookup[valve]!!
            var best = 0

            // option #1: skip the current valve
            for (neighbor in curr.adj) {
                best = maxOf(best, traverse(neighbor, time - 1, opened, withElephant))
            }

            // option #2: open the current valve to release more pressure in the remaining (time - 1) minutes
            if (curr.id !in opened && curr.rate > 0) {
                best = maxOf(best, traverse(curr.id, time - 1, opened + curr.id, withElephant) + (time - 1) * curr.rate)
            }

            // cache and return the answer
            dp[key] = best
            return best
        }

        return traverse(start, maxTime, setOf(), withElephant)
    }

    fun part1(input: List<String>): Int {
        val lookup = parseInput(input)
        return solve(lookup, "AA", 30, false)
    }

    fun part2(input: List<String>): Int {
        val lookup = parseInput(input)
        return solve(lookup, "AA", 26, true)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("day${DAY_ID}/Day${DAY_ID}")

    // takes ~ 2 seconds on MacBook Pro 2015
    measureTimeMillis {
        println(part1(input)) // answer = 1845
    }.also { println("Part 1 took $it ms") }

    // takes ~ 9 minutes on MacBook Pro 2015
    //
    // Food for thoughts:
    // - use integers to label valves, i.e. AA -> 0, BB -> 1, CC -> 2, ...
    // - bitmask to compress the set of opened valves into an integer, i.e. ["AA", "CC", "DD] -> ...1101
    // - use a pure integer for a DP key
    // - DP can be represented as an array instead of a map, which is more performant
    measureTimeMillis {
        println(part2(input)) // answer = 2286
    }.also { println("Part 2 took $it ms") }
}
