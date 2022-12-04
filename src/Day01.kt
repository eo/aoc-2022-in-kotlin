// https://adventofcode.com/2022/day/1
fun main() {
    fun totalCaloriesCarriedByEachElf(input: String): List<Int> = input.split("\n\n").map {
        it.lines().sumOf(String::toInt)
    }

    fun part1(input: String): Int {
        return totalCaloriesCarriedByEachElf(input)
            .maxOrNull() ?: 0
    }

    fun part2(input: String): Int {
        return totalCaloriesCarriedByEachElf(input)
            .sortedDescending()
            .take(3)
            .sum()
    }

    val input = readText("Input01")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
