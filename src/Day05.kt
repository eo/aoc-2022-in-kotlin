// https://adventofcode.com/2022/day/5
fun main() {
    fun parseInput(input: String): Pair<CrateStacks, List<Move>> {
        val (drawingStr, movesStr) = input.split("\n\n")
        val crateStacks = CrateStacks.fromDrawing(drawingStr)
        val moves = movesStr.lines().map(Move::fromString)

        return crateStacks to moves
    }

    fun part1(input: String): String {
        val (crateStacks, moves) = parseInput(input)
        moves.forEach { crateStacks.performSingleCrateMove(it) }
        return crateStacks.topCrates()
    }

    fun part2(input: String): String {
        val (crateStacks, moves) = parseInput(input)
        moves.forEach { crateStacks.performMultiCratesMove(it) }
        return crateStacks.topCrates()
    }

    val input = readText("Input05")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

private class Move(val quantity: Int, val from: Int, val to: Int) {
    companion object {
        private val REGEX = "move (\\d+) from (\\d+) to (\\d+)".toRegex()

        fun fromString(str: String): Move = REGEX.matchEntire(str)?.let {
            val (quantity, from, to) = it.destructured
            Move(quantity.toInt(), from.toInt(), to.toInt())
        } ?: error("Invalid move string: $str")
    }
}

private class CrateStacks
private constructor(private val stackIds: List<Int>, private val stacks: Map<Int, ArrayDeque<Char>>) {
    fun performSingleCrateMove(move: Move) {
        repeat(move.quantity) {
            stacks[move.from]?.removeLast()?.let { crate ->
                stacks[move.to]?.add(crate)
            }
        }
    }

    fun performMultiCratesMove(move: Move) {
        stacks[move.to]?.addAll(
                Array(move.quantity) {
                    stacks[move.from]?.removeLast()
                }.filterNotNull().reversed()
        )
    }

    fun topCrates() = buildString {
        stackIds.forEach { stackId ->
            stacks[stackId]?.let { stack ->
                if (stack.isEmpty()) append(" ") else append(stack.last())
            }
        }
    }

    companion object {
        fun fromDrawing(drawingStr: String): CrateStacks {
            val stackLinesReversed = drawingStr.lines().reversed()
            val stackIds = "\\d+".toRegex().findAll(stackLinesReversed.first())
                    .map { it.value.toInt() }
                    .toList()

            val stacks = stackIds.associateWith { ArrayDeque<Char>() }
            stackLinesReversed.drop(1)
                    .forEach { line ->
                        line.chunked(4).forEachIndexed { stackIndex, crate ->
                            if (crate.startsWith('[')) {
                                stacks[stackIds[stackIndex]]?.add(crate[1])
                            }
                        }
                    }

            return CrateStacks(stackIds, stacks)
        }
    }
}
