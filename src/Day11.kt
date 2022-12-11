// https://adventofcode.com/2022/day/11
fun main() {
    fun parseInput(input: String): List<Monkey> =
        input
            .split("\n\n")
            .map(Monkey::fromString)

    fun solve(monkeys: List<Monkey>, rounds: Int, reliefFunction: (Long) -> Long): Long {
        val monkeysMap = monkeys.associateBy(Monkey::id)

        repeat(rounds) {
            monkeys.forEach { monkey ->
                monkey.inspect(reliefFunction) { item, monkeyId ->
                    monkeysMap[monkeyId]?.addItem(item)
                }
            }
        }

        return monkeys
            .map(Monkey::totalInspections)
            .sorted()
            .takeLast(2)
            .reduce { acc, value -> acc * value }
    }

    fun part1(input: String): Long {
        val monkeys = parseInput(input)
        return solve(monkeys, 20) { it / 3 }
    }

    fun part2(input: String): Long {
        val monkeys = parseInput(input)
        val reliefFactor = monkeys.map { it.test.value }.reduce { acc, value -> acc * value }
        return solve(monkeys, 10000) { it % reliefFactor }
    }

    val input = readText("Input11")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}


class Monkey(val id: Int, items: List<Long>, val operation: Operation, val test: Test) {
    private val items = items.toMutableList()
    var totalInspections = 0L
        private set

    fun addItem(item: Long) = items.add(item)

    fun inspect(reliefFunction: (Long) -> Long, transferAction: (Long, Int) -> Unit) {
        totalInspections += items.size
        items.forEach { item ->
            val newItem = reliefFunction(operation.perform(item))
            transferAction(newItem, test.monkeyIdToThrow(newItem))
        }
        items.clear()
    }

    sealed class Operation {
        abstract fun perform(oldValue: Long): Long
    }

    object SquareOperation : Operation() {
        override fun perform(oldValue: Long) = oldValue * oldValue
    }

    class AdditionOperation(val addition: Long): Operation() {
        override fun perform(oldValue: Long) = oldValue + addition
    }

    class MultiplicationOperation(val times: Long): Operation() {
        override fun perform(oldValue: Long) = oldValue * times
    }

    class Test(
        val value: Long,
        val monkeyIdToThrowIfTrue: Int,
        val monkeyIdToThrowIfFalse: Int
    ) {
        fun monkeyIdToThrow(number: Long) = if (number % value == 0L) {
            monkeyIdToThrowIfTrue
        } else {
            monkeyIdToThrowIfFalse
        }
    }

    companion object {
        fun fromString(str: String): Monkey {
            val lines = str.lines()

            return Monkey(
                parseMonkeyId(lines[0]),
                parseStartingItems(lines[1]),
                parseOperation(lines[2]),
                parseTest(lines[3], lines[4], lines[5])
            )
        }

        private fun parseMonkeyId(monkeyIdLine: String): Int =
            monkeyIdLine.substringAfter("Monkey ").trimEnd(':').toInt()

        private fun parseStartingItems(startingItemsLine: String): List<Long> =
            startingItemsLine.substringAfter("Starting items: ").split(", ").map(String::toLong)

        private fun parseOperation(operationLine: String): Operation {
            val restOfOperation = operationLine.substringAfter("Operation: new = old ")
            val (symbol, value) = restOfOperation.split(" ")

            return when (symbol) {
                "*" -> if (value == "old") SquareOperation else MultiplicationOperation(value.toLong())
                "+" -> AdditionOperation(value.toLong())
                else -> error("Invalid operation! $symbol")
            }
        }

        private fun parseTest(
            testLine: String,
            trueActionLine: String,
            falseActionLine: String
        ) = Test(
            testLine.substringAfter("Test: divisible by ").toLong(),
            trueActionLine.substringAfter("If true: throw to monkey ").toInt(),
            falseActionLine.substringAfter("If false: throw to monkey ").toInt()
        )
    }
}