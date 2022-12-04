// https://adventofcode.com/2022/day/4
fun main() {
    infix fun IntRange.isEitherFullyContainedInTheOther(other: IntRange): Boolean =
        (first in other && last in other) || (other.first in this && other.last in this)

    infix fun IntRange.overlaps(other: IntRange): Boolean =
        !(first > other.last || last < other.first)

    fun sectionAssignmentPairs(input: List<String>): List<Pair<IntRange, IntRange>> {
        return input.map { line ->
            val (firstSection, secondSection) = line.split(",").map { section ->
                val (sectionStart, sectionEnd) = section.split("-").map(String::toInt)
                sectionStart..sectionEnd
            }
            firstSection to secondSection
        }
    }

    fun part1(input: List<String>): Int {
        return sectionAssignmentPairs(input)
            .count { (first, second) -> first isEitherFullyContainedInTheOther second }
    }

    fun part2(input: List<String>): Int {
        return sectionAssignmentPairs(input)
            .count { (first, second) -> first overlaps second }
    }

    val input = readLines("Input04")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
