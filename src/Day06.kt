// https://adventofcode.com/2022/day/6
fun main() {
    fun findMarker(input: String, markerSize: Int): Int? {
        val markerCharacters =
            input
                .take(markerSize - 1)
                .groupingBy { it }
                .eachCount()
                .toMutableMap()

        for ((index, newChar) in input.withIndex().drop(markerSize - 1)) {
            val oldChar = input[index - markerSize + 1]
            markerCharacters[newChar] = markerCharacters.getOrDefault(newChar, 0) + 1
            if (markerCharacters.size == markerSize) return index + 1

            markerCharacters[oldChar] = markerCharacters.getOrDefault(oldChar, 0) - 1
            markerCharacters.remove(oldChar, 0)
        }

        return null
    }
    fun part1(input: String): Int? {
        return findMarker(input, 4)
    }

    fun part2(input: String): Int? {
        return findMarker(input, 14)
    }

    val input = readText("Input06")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
