// https://adventofcode.com/2022/day/14
fun main() {
    fun part1(input: List<String>): Int {
        val sandSourcePoint = Point(500, 0)
        val blockedPoints = input.map(Path::fromString)
            .flatMap(Path::points)
            .toMutableSet()
        val maxOfPointY = blockedPoints.maxOf(Point::y)

        var sandsRested = 0
        while (true) {
            var currentSandPoint = sandSourcePoint

            while (currentSandPoint !in blockedPoints) {
                val nextSandPoint = listOf(
                    currentSandPoint.belowPoint,
                    currentSandPoint.belowLeftPoint,
                    currentSandPoint.belowRightPoint
                ).firstOrNull { it !in blockedPoints }

                if (nextSandPoint == null) {
                    blockedPoints += currentSandPoint
                    sandsRested++
                } else if (nextSandPoint.y < maxOfPointY) {
                    currentSandPoint = nextSandPoint
                } else {
                    return sandsRested
                }
            }
        }
    }

    fun part2(input: List<String>): Int {
        val sandSourcePoint = Point(500, 0)
        val blockedPoints = input.map(Path::fromString)
            .flatMap(Path::points)
            .toMutableSet()
        val floorY = blockedPoints.maxOf(Point::y) + 2

        var sandsRested = 0
        while (sandSourcePoint !in blockedPoints) {
            var currentSandPoint = sandSourcePoint

            while (currentSandPoint !in blockedPoints) {
                val nextSandPoint = listOf(
                    currentSandPoint.belowPoint,
                    currentSandPoint.belowLeftPoint,
                    currentSandPoint.belowRightPoint
                ).firstOrNull { it !in blockedPoints }

                if (nextSandPoint == null) {
                    blockedPoints += currentSandPoint
                    sandsRested++
                } else if (nextSandPoint.y < floorY) {
                    currentSandPoint = nextSandPoint
                } else {
                    blockedPoints += currentSandPoint
                    sandsRested++
                    break
                }
            }
        }
        return sandsRested
    }

    val input = readLines("Input14")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

private data class Point(val x: Int, val y: Int) {
    val belowLeftPoint get() = copy(x = x - 1, y = y + 1)
    val belowPoint get() = copy(y = y + 1)
    val belowRightPoint get() = copy(x = x + 1, y = y + 1)

    companion object {
        fun fromString(str: String): Point =
            str.split(",")
                .let { (x, y) ->
                    Point(x.toInt(), y.toInt())
                }
    }
}

private class Path(points: List<Point>) {
    val points: Set<Point> =
        points
            .windowed(size = 2, step = 1)
            .flatMap { (point1, point2) ->
                pointsInLine(point1, point2)
            }
            .toSet()

    private fun pointsInLine(point1: Point, point2: Point): List<Point> = when {
        point1.x == point2.x -> {
            if (point1.y < point2.y) {
                point1.y..point2.y
            } else {
                point1.y downTo point2.y
            }.map { Point(point1.x, it) }
        }
        point1.y == point2.y -> {
            if (point1.x < point2.x) {
                point1.x..point2.x
            } else {
                point1.x downTo point2.x
            }.map { Point(it, point1.y) }
        }
        else -> error("Either x or y positions of points on a line should be the same!")
    }

    companion object {
        fun fromString(str: String) = Path(
            str.split(" -> ")
                .map(Point::fromString)
        )
    }
}