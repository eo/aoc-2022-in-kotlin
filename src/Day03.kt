// https://adventofcode.com/2022/day/3
fun main() {
    fun itemPriority(item: Char) = if (item.isLowerCase()) {
        item - 'a' + 1
    } else {
        item - 'A' + 27
    }

    fun part1(input: List<String>): Int {
        return input
            .map { it.chunked(it.length / 2).map(String::toSet) }
            .map { (items1, items2) -> (items1 intersect items2).single() }
            .sumOf(::itemPriority)
    }

    fun part2(input: List<String>): Int {
        return input
            .chunked(3)
            .map { rucksack ->
                rucksack
                    .map(String::toSet)
                    .reduce { acc, items ->  acc intersect items }
                    .single()
            }
            .sumOf(::itemPriority)
    }

    val input = readLines("Input03")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
