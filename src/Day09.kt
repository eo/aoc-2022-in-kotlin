import kotlin.math.abs

// https://adventofcode.com/2022/day/9
fun main() {
    fun parseInput(input: List<String>): List<Motion> = input.map(Motion::fromString)

    fun part1(input: List<String>): Int {
        val motions = parseInput(input)
        var rope = Rope()

        val visitedPositions = mutableSetOf(rope.tailPosition)

        motions.forEach { motion ->
            repeat(motion.count) {
                rope = rope.moved(motion.direction)
                visitedPositions += rope.tailPosition
            }
        }

        return visitedPositions.size
    }

    fun part2(input: List<String>): Int {
        val motions = parseInput(input)
        var rope = MultiKnotRope(10)

        val visitedPositions = mutableSetOf(rope.tailPosition)

        motions.forEach { motion ->
            repeat(motion.count) {
                rope = rope.moved(motion.direction)
                visitedPositions += rope.tailPosition
            }
        }

        return visitedPositions.size
    }

    val input = readLines("Input09")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

private data class Position(val x: Int, val y: Int) {
    fun moved(direction: Direction) = when (direction) {
        Direction.LEFT -> Position(x - 1, y)
        Direction.RIGHT -> Position(x + 1, y)
        Direction.UP -> Position(x, y + 1)
        Direction.DOWN -> Position(x, y - 1)
    }

    fun isTouching(other: Position): Boolean =
        this == other || (abs(x - other.x) <= 1 && abs(y - other.y) <= 1)

    companion object {
        val ORIGIN = Position(0, 0)
    }
}

private enum class Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN,
}

private data class Motion(val direction: Direction, val count: Int) {
    companion object {
        fun fromString(str: String): Motion {
            val (directionStr, count) = str.split(" ")
            val direction = when (directionStr) {
                "L" -> Direction.LEFT
                "R" -> Direction.RIGHT
                "U" -> Direction.UP
                "D" -> Direction.DOWN
                else -> error("Invalid direction! $directionStr")
            }

            return Motion(direction, count.toInt())
        }
    }
}

private class Rope(
    val headPosition: Position = Position.ORIGIN,
    val tailPosition: Position = headPosition
) {
    fun moved(direction: Direction): Rope {
        val newHeadPosition = headPosition.moved(direction)
        val newTailPosition = tailFollowingNewHeadPosition(newHeadPosition)

        return Rope(newHeadPosition, newTailPosition)
    }

    private fun tailFollowingNewHeadPosition(newHeadPosition: Position): Position =
        if (tailPosition.isTouching(newHeadPosition)) {
            tailPosition
        } else if (tailPosition.y == newHeadPosition.y) {
            tailPosition.moved(if (tailPosition.x > newHeadPosition.x) {
                Direction.LEFT
            } else {
                Direction.RIGHT
            })
        } else if (tailPosition.x == newHeadPosition.x) {
            tailPosition.moved(if (tailPosition.y > newHeadPosition.y) {
                Direction.DOWN
            } else {
                Direction.UP
            })
        } else {
            Position(
                tailPosition.x + (headPosition.x - tailPosition.x).coerceIn(-1, 1),
                tailPosition.y + (headPosition.y - tailPosition.y).coerceIn(-1, 1)
            )
        }
}

private class MultiKnotRope
private constructor(
    private val knotPositions: List<Position>
) {
    val tailPosition: Position get() = knotPositions.last()

    constructor(numberOfKnots: Int) : this(List(numberOfKnots) { Position.ORIGIN })

    fun moved(direction: Direction): MultiKnotRope {
        val newKnotPositions = knotPositions.toMutableList()

        newKnotPositions[0] = newKnotPositions[0].moved(direction)
        for (knotIndex in 1..newKnotPositions.lastIndex) {
            newKnotPositions[knotIndex] = followKnot(
                followedKnotPosition = newKnotPositions[knotIndex - 1],
                followerKnotPosition = newKnotPositions[knotIndex]
            )
        }

        return MultiKnotRope(newKnotPositions)
    }

    private fun followKnot(
        followedKnotPosition: Position,
        followerKnotPosition: Position
    ): Position =
        if (followerKnotPosition.isTouching(followedKnotPosition)) {
            followerKnotPosition
        } else if (followerKnotPosition.y == followedKnotPosition.y) {
            followerKnotPosition.moved(if (followerKnotPosition.x > followedKnotPosition.x) {
                Direction.LEFT
            } else {
                Direction.RIGHT
            })
        } else if (followerKnotPosition.x == followedKnotPosition.x) {
            followerKnotPosition.moved(if (followerKnotPosition.y > followedKnotPosition.y) {
                Direction.DOWN
            } else {
                Direction.UP
            })
        } else {
            Position(
                followerKnotPosition.x + (followedKnotPosition.x - followerKnotPosition.x).coerceIn(-1, 1),
                followerKnotPosition.y + (followedKnotPosition.y - followerKnotPosition.y).coerceIn(-1, 1)
            )
        }
}
