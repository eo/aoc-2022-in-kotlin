fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readLines("Test01")
    val input = readLines("Input01")
    check(part1(testInput) == 1)
    check(part2(testInput) == 1)

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
