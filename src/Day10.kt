// https://adventofcode.com/2022/day/10
fun main() {
    fun registerValuesAtEachCycle(input: List<String>): List<Int> {
        var lastRegisterValue = 1
        return buildList {
            add(lastRegisterValue)
            for (operation in input.map(Operation::fromString)) {
                repeat(operation.cycle) {
                    add(lastRegisterValue)
                }
                lastRegisterValue = when (operation) {
                    is NoOperation -> lastRegisterValue
                    is AddOperation -> lastRegisterValue + operation.value
                }
            }
            add(lastRegisterValue)
        }
    }

    fun part1(input: List<String>): Int {
        return registerValuesAtEachCycle(input)
            .withIndex()
            .toList()
            .slice(20..220 step 40)
            .sumOf { it.index * it.value }
    }

    fun part2(input: List<String>): String {
        val crtHeight = 6
        val crtWidth = 40
        val registerValuesAtEachCycle = registerValuesAtEachCycle(input)
        return List(crtHeight) { row ->
            List(crtWidth) { col ->
                val cycle = crtWidth * row + col + 1
                val registerValue = registerValuesAtEachCycle[cycle]
                if (col in registerValue - 1..registerValue + 1) "#" else "."
            }.joinToString("")
        }.joinToString("\n")
    }

    val input = readLines("Input10")
    println("Part 1: " + part1(input))
    println("Part 2: ")
    println(part2(input))
}

sealed class Operation(val cycle: Int) {
    companion object {
        fun fromString(str: String): Operation =
            when (val operationName = str.substringBefore(" ")) {
                "addx" -> AddOperation(str.substringAfter(" ").toInt())
                "noop" -> NoOperation
                else -> error("Invalid operation! $operationName")
            }
    }
}

class AddOperation(val value: Int) : Operation(2)

object NoOperation : Operation(1)