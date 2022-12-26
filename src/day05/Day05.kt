package day05

import readInput
import java.util.*

private const val DAY_ID = "05"

private data class Instruction(val quantity: Int, val from: Int, val to: Int)

fun main() {
    fun solve(input: List<String>, strategy: (stacks: Array<Deque<Char>>, instruction: Instruction) -> Unit): String {
        val data = input.takeWhile { line -> line.isNotEmpty() }

        // fill in the stacks
        val n = """(\d+)$""".toRegex().find(data.last())!!.value.toInt()
        val stacks = Array<Deque<Char>>(n) { ArrayDeque() }
        data.dropLast(1).forEach { line ->
            val crates = line.withIndex().filter { (_, c) -> c.isLetter() }
            crates.forEach { (index, c) ->
                // compute the index of the stack to put the current crate into:
                // pattern "[X]_" takes 4 characters
                stacks[index / 4].offerFirst(c)
            }
        }

        // process instructions
        val instruction = """move (\d+) from (\d+) to (\d+)""".toRegex()
        input.drop(data.size + 1).forEach { line ->
            val (quantity, from, to) = instruction.find(line)!!.destructured
            strategy(stacks, Instruction(quantity.toInt(), from.toInt(), to.toInt()))
        }

        return buildString {
            stacks.forEach { stack -> append(stack.peekLast()) }
        }
    }

    fun part1(input: List<String>): String =
        solve(input) { stacks, (quantity, from, to) ->
            repeat(quantity) {
                val top = stacks[from - 1].pollLast()
                stacks[to - 1].offerLast(top)
            }
        }

    fun part2(input: List<String>): String =
        solve(input) { stacks, (quantity, from, to) ->
            val buffer = ArrayDeque<Char>()
            repeat(quantity) {
                val top = stacks[from - 1].pollLast()
                buffer.push(top)
            }

            while (!buffer.isEmpty()) {
                val top = buffer.pop()
                stacks[to - 1].offerLast(top)
            }
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("day${DAY_ID}/Day$DAY_ID")
    println(part1(input)) // answer = JCMHLVGMG
    println(part2(input)) // answer = LVMRWSSPZ
}
