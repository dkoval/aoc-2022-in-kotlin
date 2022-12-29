package day19

import readInput
import java.util.*

private const val DAY_ID = "19"

private data class Blueprint(
    val id: Int,
    val costs: List<List<Int>>
)

fun main() {
    fun parseInput(input: List<String>): List<Blueprint> {
        val regex = """
            |Blueprint (\d+):
            | Each ore robot costs (\d+) ore.
            | Each clay robot costs (\d+) ore.
            | Each obsidian robot costs (\d+) ore and (\d+) clay.
            | Each geode robot costs (\d+) ore and (\d+) obsidian.
        """.trimMargin().replace("\n", "").toRegex()

        return input.map { line ->
            val groups = regex.find(line)!!.groupValues
            Blueprint(
                groups[1].toInt(),
                listOf(
                    // 0 - ore robot
                    listOf(groups[2].toInt(), 0, 0, 0),
                    // 1 - clay robot
                    listOf(groups[3].toInt(), 0, 0, 0),
                    // 2 - obsidian robot
                    listOf(groups[4].toInt(), groups[5].toInt(), 0, 0),
                    // 3 - geode robot
                    listOf(groups[6].toInt(), 0, groups[7].toInt(), 0)
                )
            )
        }
    }

    fun solve(blueprint: Blueprint, time: Int): Int {
        data class State(
            val time: Int,
            val minerals: List<Int>,
            val robots: List<Int>
        )

        val maxRobotsNeeded = (0..3).map { i -> blueprint.costs.maxOf { costs -> costs[i] } }

        // ~ BFS to explore all possible states
        val q: Queue<State> = ArrayDeque()
        val seen = mutableSetOf<State>()
        fun enqueue(item: State) {
            if (item !in seen) {
                q.offer(item)
                seen += item
            }
        }

        fun divRoundUp(x: Int, y: Int): Int = (x - 1) / y + 1

        var best = 0
        enqueue(State(time, listOf(0, 0, 0, 0), listOf(1, 0, 0, 0)))
        while (!q.isEmpty()) {
            val curr = q.poll()

            // what is the number of geodes we can get from this state in curr.time remaining time?
            val numGeodesCanGet = curr.minerals[3] + curr.robots[3] * curr.time
            best = maxOf(best, numGeodesCanGet)

            if (curr.time == 0) {
                continue
            }

            for (x in 0..3) {
                // Do we already have enough x-collecting robots?
                if (x != 3 && curr.robots[x] == maxRobotsNeeded[x]) {
                    continue
                }

                // What are the minerals required to build an x-collecting robot?
                val costs = blueprint.costs[x].withIndex().filter { (_, cost) -> cost > 0 }
                val canBuild = costs.all { (i, _) -> curr.robots[i] > 0 }
                if (!canBuild) {
                    continue
                }

                // How much time do we need to collect all the required minerals?
                // It also takes an extra 1 unit of time to build a robot.
                val spentTime = 1 + costs.maxOf { (i, cost) ->
                    if (curr.minerals[i] >= cost) 0 else divRoundUp(cost - curr.minerals[i], curr.robots[i])
                }

                // Proceed to the next state
                val nextTime = curr.time - spentTime
                if (nextTime <= 0) {
                    continue
                }
                val nextMinerals = (0..3).map { i -> curr.minerals[i] - blueprint.costs[x][i] + curr.robots[i] * spentTime }
                val nextRobots = curr.robots.toMutableList().also { it[x]++ }
                enqueue(State(nextTime, nextMinerals, nextRobots))
            }
        }
        return best
    }

    fun part1(input: List<String>): Int {
        val time = 24
        val blueprints = parseInput(input)
        return blueprints.sumOf { blueprint -> blueprint.id * solve(blueprint, time) }
    }

    fun part2(input: List<String>): Long {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 33)

    val input = readInput("day${DAY_ID}/Day${DAY_ID}")
    println(part1(input)) // answer = 1725
    //println(part2(input))
}
