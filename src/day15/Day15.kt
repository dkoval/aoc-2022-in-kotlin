package day15

import readInput
import kotlin.math.abs

private const val DAY_ID = "15"

private data class Point(
    val x: Int,
    val y: Int
) {
    companion object {
        fun fromString(s: String): Point {
            // example: x=-2, y=15
            val (x, y) = s.split(", ").map { it.substringAfter("=").toInt() }
            return Point(x, y)
        }
    }
}

private data class Data(
    val sensor: Point,
    val beacon: Point
)

private data class Interval(
    var start: Int,
    var end: Int
) {
    fun overlapsWith(that: Interval): Boolean = that.end >= start && that.start <= end
    operator fun contains(x: Int): Boolean = x in start..end
}

fun main() {
    fun parseInput(input: List<String>): List<Data> =
        input.map { line ->
            val (sensor, closestBeacon) = line.split(": ").asSequence()
                .map { it.substringAfter("at ") }
                .map { Point.fromString(it) }
                .toList()

            Data(sensor, closestBeacon)
        }

    fun part1(input: List<String>, y: Int): Int {
        val data = parseInput(input)

        val beacons = mutableSetOf<Point>()
        val intervals = mutableListOf<Interval>()
        for ((sensor, beacon) in data) {
            if (beacon.y == y) {
                beacons += beacon
            }

            val dx = abs(sensor.x - beacon.x)
            val dy = abs(sensor.y - beacon.y)
            val dist = dx + dy
            // (S, B) pair defines a square with S being in the middle, where other beacons can't possibly exist
            // for each such (S, B) pair, find an intersection with row Y:
            // |x - S[i].x| + |Y - S[i].y| = dist
            // |x - S[i].x| = dist - |Y - S[i].y|
            // let D = dist - |Y - S[i].y|, then
            // x - S[i].x = ±D
            //  <=>
            // x = S[i].x - D <- start of the interval
            // x = S[i].x + D <- end of the interval
            val d = dist - abs(y - sensor.y)
            if (d <= 0) {
                continue
            }
            intervals += Interval(sensor.x - d, sensor.x + d)
        }

        // merge potentially overlapping intervals
        intervals.sortBy { it.start }
        val union = mutableListOf<Interval>()
        for (interval in intervals) {
            if (union.isEmpty() || !union.last().overlapsWith(interval)) {
                union += interval
            } else {
                val last = union.last()
                last.end = maxOf(last.end, interval.end)
            }
        }

        return union.fold(0) { acc, interval ->
            var total = interval.end - interval.start + 1
            for (beacon in beacons) {
                if (beacon.x in interval) {
                    total--
                }
            }
            acc + total
        }
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day${DAY_ID}/Day${DAY_ID}_test")
    check(part1(testInput, 10).also { println(it) } == 26)
    //check(part2(testInput) == 42)

    val input = readInput("day${DAY_ID}/Day$DAY_ID")
    println(part1(input, 2000000))
    //println(part2(input))
}
