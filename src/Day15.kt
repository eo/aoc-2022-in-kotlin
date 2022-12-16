import kotlin.math.abs

// https://adventofcode.com/2022/day/15
fun main() {
    fun parseInput(input: List<String>): List<SensorBeaconPair> {
        val regex = "x=(-?\\d+), y=(-?\\d+)".toRegex()
        return input
            .flatMap { regex.findAll(it) }
            .map { Coordinate(it.groupValues[1].toInt(), it.groupValues[2].toInt()) }
            .chunked(2)
            .map { (first, second) -> SensorBeaconPair(first, second) }
    }

    fun List<IntRange>.merged(): List<IntRange> {
        if (isEmpty()) return this

        val sortedRanges = sortedBy(IntRange::first)
        val mergedRanges = mutableListOf(sortedRanges.first())
        (1..sortedRanges.lastIndex).forEach { index ->
            if (mergedRanges.last().last + 1 < sortedRanges[index].first) {
                mergedRanges += sortedRanges[index]
            } else if (mergedRanges.last().last < sortedRanges[index].last) {
                mergedRanges[mergedRanges.lastIndex]  =
                    mergedRanges.last().first..sortedRanges[index].last
            }
        }
        return mergedRanges
    }

    fun List<IntRange>.countValuesContained(values: Collection<Int>): Int {
        val sortedValues = ArrayDeque(values.sorted())

        var count = 0
        for (range in this) {
            while (
                sortedValues.isNotEmpty() &&
                sortedValues.first() < range.first
            ) {
                sortedValues.removeFirst()
            }

            while (
                sortedValues.isNotEmpty() &&
                sortedValues.first() <= range.last
            ) {
                sortedValues.removeFirst()
                count++
            }

            if (sortedValues.isEmpty()) {
                break
            }
        }
        return count
    }

    fun IntRange.findMissing(ranges: List<IntRange>): Int? {
        val firstRange = ranges.first()

        return if (firstRange.first <= first && firstRange.last >= last) {
            return null
        } else if (firstRange.first == first + 1) {
            first
        } else if (firstRange.last == last - 1) {
            last
        } else if (ranges.size == 2 && firstRange.last + 1 == ranges[1].first - 1) {
            firstRange.last + 1
        } else {
            error("More than 1 missing value means multiple solutions!")
        }
    }

    fun part1(input: List<String>): Int {
        val rowOfInterest = 10
        val sensorBeaconPairs = parseInput(input)

        val noBeaconRanges = sensorBeaconPairs
            .mapNotNull { it.noBeaconRangeAtY(rowOfInterest) }
            .merged()
        val xCoordinatesOfBeaconsAtY = sensorBeaconPairs
                .map(SensorBeaconPair::beaconCoordinate)
                .filter { it.y == rowOfInterest }
                .map(Coordinate::x)
                .distinct()

        val totalSizeOfNoBeaconRanges =  noBeaconRanges.sumOf { it.last - it.first + 1 }
        val numberOfBeaconsInNoBeaconRanges =
            noBeaconRanges.countValuesContained(xCoordinatesOfBeaconsAtY)

        return totalSizeOfNoBeaconRanges - numberOfBeaconsInNoBeaconRanges
    }

    fun part2(input: List<String>): Long {
        val maxSize = 20
        val tuningFreqMultiplier = 4_000_000L
        val rangeOfInterest = 0..maxSize
        val sensorBeaconPairs = parseInput(input)

        for (y in rangeOfInterest) {
            val noBeaconRanges = sensorBeaconPairs
                .mapNotNull { it.noBeaconRangeAtY(y) }
                .filterNot { it.last < 0 || it.first > maxSize }
                .merged()
                .map { if (it.first < 0) 0..it.last else it }
                .map { if (it.last > maxSize) it.first..maxSize else it }

            rangeOfInterest.findMissing(noBeaconRanges)?.let {
                return it * tuningFreqMultiplier + y
            }
        }

        return 0
    }

    val input = readLines("Input15")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

private data class Coordinate(val x: Int, val y: Int) {
    fun distanceTo(other: Coordinate) = abs(x - other.x) + abs(y - other.y)
}

private class SensorBeaconPair(val sensorCoordinate: Coordinate, val beaconCoordinate: Coordinate) {
    val distanceInBetween = sensorCoordinate.distanceTo(beaconCoordinate)

    fun noBeaconRangeAtY(y: Int): IntRange? {
        val verticalDistance = abs(sensorCoordinate.y - y)
        val horizontalDistance = distanceInBetween - verticalDistance

        return if (horizontalDistance >= 0) {
            (sensorCoordinate.x - horizontalDistance)..(sensorCoordinate.x + horizontalDistance)
        } else {
            null
        }
    }
}