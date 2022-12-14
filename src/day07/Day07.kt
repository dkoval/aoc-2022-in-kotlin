package day07

import readInput

private const val DAY_ID = "07"

private sealed class Filesystem(
    open val name: String,
    open val parent: Directory?
)

private data class File(
    override val name: String,
    val size: Int,
    override val parent: Directory?
) : Filesystem(name, parent)

private data class Directory(
    override val name: String,
    override val parent: Directory?,
    val children: MutableMap<String, Filesystem> = mutableMapOf()
) : Filesystem(name, parent)

fun main() {
    fun parseInput(input: List<String>, idx: Int, current: Directory?) {
        if (current == null || idx >= input.size) {
            return
        }

        val line = input[idx]
        if (line.startsWith("$")) {
            val command = line.removePrefix("$ ")
            if (command == "ls") {
                parseInput(input, idx + 1, current)
            } else {
                // format: cd <directory>
                val dir = command.removePrefix("cd ")
                parseInput(input, idx + 1, if (dir == "..") current.parent else current.children[dir] as? Directory)
            }
        } else {
            if (line.startsWith("dir")) {
                // format: dir <directory>
                val dir = line.removePrefix("dir ")
                current.children[dir] = Directory(dir, current)
            } else {
                // format: <size> <file>
                val (size, file) = line.split(" ")
                current.children[file] = File(file, size.toInt(), current)
            }
            parseInput(input, idx + 1, current)
        }
    }

    fun buildFilesystem(input: List<String>): Directory =
        Directory("/", null).also { parseInput(input, 1, it) }

    fun traverseFilesystem(root: Directory, onDirectory: (dirSize: Int) -> Unit): Int {
        fun dfs(current: Filesystem): Int = when (current) {
            is File -> current.size
            is Directory -> current.children.values.sumOf { dfs(it) }.also { size -> onDirectory(size) }
        }
        return dfs(root)
    }

    fun part1(input: List<String>): Int {
        val thresholdSize = 100000
        val root = buildFilesystem(input)

        var sum = 0
        traverseFilesystem(root) { dirSize ->
            if (dirSize <= thresholdSize) {
                sum += dirSize
            }
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        val diskSize = 70000000
        val updateSize = 30000000
        val root = buildFilesystem(input)

        val dirSizes = mutableListOf<Int>()
        val usedSize = traverseFilesystem(root) { dirSize ->
            dirSizes += dirSize
        }

        val unusedSize = diskSize - usedSize
        val needSize = updateSize - unusedSize
        return dirSizes.asSequence()
            .filter { dirSize -> dirSize >= needSize }
            .min()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("day${DAY_ID}/Day$DAY_ID")
    println(part1(input)) // answer = 1348005
    println(part2(input)) // answer = 12785886
}
