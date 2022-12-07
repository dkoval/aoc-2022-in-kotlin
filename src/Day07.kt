sealed class Filesystem(
    open val name: String,
    open val parent: Directory?
)

data class File(
    override val name: String,
    val size: Int,
    override val parent: Directory?
) : Filesystem(name, parent)

data class Directory(
    override val name: String,
    override val parent: Directory?,
    val children: MutableMap<String, Filesystem> = mutableMapOf()
) : Filesystem(name, parent)

fun main() {
    fun parseInput(current: Directory?, input: List<String>, idx: Int) {
        if (current == null || idx >= input.size) {
            return
        }

        val line = input[idx]
        if (line.startsWith("$")) {
            val command = line.removePrefix("$ ")
            if (command == "ls") {
                parseInput(current, input, idx + 1)
            } else {
                // cd <directory name>
                val dir = command.removePrefix("cd ")
                val newCurrent = if (dir == "..") current.parent else current.children[dir] as? Directory
                parseInput(newCurrent, input, idx + 1)
            }
        } else {
            if (line.startsWith("dir")) {
                // dir <directory name>
                val name = line.removePrefix("dir ")
                current.children[name] = Directory(name, current)
            } else {
                // <file size> <file name>
                val (size, name) = line.split(" ")
                current.children[name] = File(name, size.toInt(), current)
            }
            parseInput(current, input, idx + 1)
        }
    }

    fun buildFilesystem(input: List<String>): Directory =
        Directory("/", null)
            .also { parseInput(it, input, 1) }

    fun traverseFilesystem(root: Filesystem, hook: (dirSize: Int) -> Unit = {}): Int {
        fun dfs(current: Filesystem): Int = when (current) {
            is File -> current.size
            is Directory -> current.children.values.sumOf { dfs(it) }
                .also { size -> hook(size) }
        }
        return dfs(root)
    }

    class Hook1(private val thresholdSize: Int) : (Int) -> Unit {
        private var _sum = 0
        val sumOf: Int get() = _sum

        override fun invoke(dirSize: Int) {
            if (dirSize <= thresholdSize) {
                _sum += dirSize
            }
        }
    }

    class Hook2(private val needSize: Int) : (Int) -> Unit {
        private var _minSize: Int = Int.MAX_VALUE
        val minSize: Int get() = _minSize

        override fun invoke(dirSize: Int) {
            if (dirSize >= needSize) {
                _minSize = minOf(_minSize, dirSize)
            }
        }
    }

    fun part1(input: List<String>): Int {
        val thresholdSize = 100000

        val root = buildFilesystem(input)
        val hook = Hook1(thresholdSize)
        traverseFilesystem(root, hook)
        return hook.sumOf
    }

    fun part2(input: List<String>): Int {
        val diskSize = 70000000
        val updateSize = 30000000

        val root = buildFilesystem(input)
        val unusedSize = diskSize - traverseFilesystem(root)
        val needSize = updateSize - unusedSize

        val hook = Hook2(needSize)
        traverseFilesystem(root, hook)
        return hook.minSize
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input)) // answer = 1348005
    println(part2(input)) // answer = 12785886
}
