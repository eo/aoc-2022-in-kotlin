// https://adventofcode.com/2022/day/8
fun main() {
    fun parseInput(input: List<String>): Array<IntArray> =
            input.map { it.chunked(1).map(String::toInt).toIntArray() }.toTypedArray()

    fun part1(input: List<String>): Int {
        val grid = parseInput(input)

        val leftMaxGrid = Grid(grid.rowCount, grid.colCount)
        for (row in 0 until grid.rowCount) {
            for (col in 0 until grid.colCount) {
                leftMaxGrid[row, col] = if (col == 0) {
                    Int.MIN_VALUE
                } else {
                    maxOf(leftMaxGrid[row, col - 1], grid[row, col - 1])
                }
            }
        }

        val rightMaxGrid = Grid(grid.rowCount, grid.colCount)
        for (row in grid.rowCount - 1 downTo  0) {
            for (col in grid.colCount - 1 downTo 0) {
                rightMaxGrid[row, col] = if (col == grid.colCount - 1) {
                    Int.MIN_VALUE
                } else {
                    maxOf(rightMaxGrid[row, col + 1], grid[row, col + 1])
                }
            }
        }

        val topMaxGrid = Grid(grid.rowCount, grid.colCount)
        for (row in 0 until grid.rowCount) {
            for (col in 0 until grid.colCount) {
                topMaxGrid[row, col] = if (row == 0) {
                    Int.MIN_VALUE
                } else {
                    maxOf(topMaxGrid[row - 1, col], grid[row - 1, col])
                }
            }
        }

        val bottomMaxGrid = Grid(grid.rowCount, grid.colCount)
        for (row in grid.rowCount - 1 downTo  0) {
            for (col in grid.colCount - 1 downTo 0) {
                bottomMaxGrid[row, col] = if (row == grid.rowCount - 1) {
                    Int.MIN_VALUE
                } else {
                    maxOf(bottomMaxGrid[row + 1, col], grid[row + 1, col])
                }
            }
        }

        return grid.count { row, col, treeHeight ->
            treeHeight > leftMaxGrid[row, col] ||
                    treeHeight > rightMaxGrid[row, col] ||
                    treeHeight > topMaxGrid[row, col] ||
                    treeHeight > bottomMaxGrid[row, col]
        }
    }

    fun scenicScore(grid: Grid, row: Int, col: Int): Int {
        val treeHeight = grid[row, col]
        var leftTrees = 0
        for (c in col - 1 downTo 0) {
            leftTrees++
            if (grid[row, c] >= treeHeight) break
        }

        var rightTrees = 0
        for (c in col + 1 until grid.colCount) {
            rightTrees++
            if (grid[row, c] >= treeHeight) break
        }

        var topTrees = 0
        for (r in row - 1 downTo 0) {
            topTrees++
            if (grid[r, col] >= treeHeight) break
        }

        var bottomTrees = 0
        for (r in row + 1 until grid.rowCount) {
            bottomTrees++
            if (grid[r, col] >= treeHeight) break
        }

        return leftTrees * rightTrees * topTrees * bottomTrees
    }

    fun part2(input: List<String>): Int {
        val grid = parseInput(input)

        var maxScenicScore = 0
        for (row in 0 until grid.rowCount) {
            for (col in 0 until grid.colCount) {
                maxScenicScore = maxOf(
                        maxScenicScore,
                        scenicScore(grid, row, col)
                )
            }
        }
        return maxScenicScore
    }

    val input = readLines("Input08")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

typealias Grid = Array<IntArray>

fun Grid(rowCount: Int, colCount: Int) = Array(rowCount) { IntArray(colCount) }

val Grid.rowCount: Int get() = size

val Grid.colCount: Int get() = first().size

operator fun Grid.get(row: Int, col: Int): Int = this[row][col]

operator fun Grid.set(row: Int, col: Int, value: Int){
    this[row][col] = value
}

fun Grid.count(predicate: (Int, Int, Int) -> Boolean) =
        foldIndexed(0) { row, totalCount, rowValues ->
            rowValues.foldIndexed(totalCount) { col, count, cellValue ->
                count + if (predicate(row, col, cellValue)) 1 else 0
            }
        }