// https://adventofcode.com/2022/day/2
fun main() {
    fun opponentHandShape(symbol: String) = when (symbol) {
        "A" -> HandShape.ROCK
        "B" -> HandShape.PAPER
        "C" -> HandShape.SCISSORS
        else -> error("$symbol is invalid!")
    }

    fun part1(input: List<String>): Int {
        fun myHandShape(symbol: String) = when (symbol) {
            "X" -> HandShape.ROCK
            "Y" -> HandShape.PAPER
            "Z" -> HandShape.SCISSORS
            else -> error("$symbol is invalid!")
        }

        fun roundScore(symbol1: String, symbol2: String): Int {
            val opponentHandShape = opponentHandShape(symbol1)
            val myHandShape = myHandShape(symbol2)
            val roundResult = when (opponentHandShape) {
                myHandShape.winsAgainst -> RoundResult.WIN
                myHandShape.losesAgainst -> RoundResult.LOSS
                else -> RoundResult.DRAW
            }

            return myHandShape.score + roundResult.score
        }

        return input
            .map { it.split(" ") }
            .sumOf { (symbol1, symbol2) -> roundScore(symbol1, symbol2) }
    }

    fun part2(input: List<String>): Int {
        fun roundResult(symbol: String) = when (symbol) {
            "X" -> RoundResult.LOSS
            "Y" -> RoundResult.DRAW
            "Z" -> RoundResult.WIN
            else -> error("$symbol is invalid!")
        }

        fun roundScore(symbol1: String, symbol2: String): Int {
            val opponentHandShape = opponentHandShape(symbol1)
            val roundResult = roundResult(symbol2)
            val myHandShape = when (roundResult) {
                RoundResult.LOSS -> opponentHandShape.winsAgainst
                RoundResult.DRAW -> opponentHandShape
                else -> opponentHandShape.losesAgainst
            }

            return myHandShape.score + roundResult.score
        }

        return input
            .map { it.split(" ") }
            .sumOf { (symbol1, symbol2) -> roundScore(symbol1, symbol2) }
    }

    val input = readLines("Input02")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

private enum class HandShape(val score: Int) {
    ROCK(1) {
        override val winsAgainst get() = SCISSORS
        override val losesAgainst get() = PAPER
    },
    PAPER(2) {
        override val winsAgainst get() = ROCK
        override val losesAgainst get() = SCISSORS
    },
    SCISSORS(3) {
        override val winsAgainst get() = PAPER
        override val losesAgainst get() = ROCK
    };

    abstract val winsAgainst: HandShape
    abstract val losesAgainst: HandShape
}

private enum class RoundResult(val score: Int) {
    LOSS(0),
    DRAW(3),
    WIN(6);
}
