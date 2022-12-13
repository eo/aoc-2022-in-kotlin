// https://adventofcode.com/2022/day/12
fun main() {
    fun shortestPathLength(
        grid: ElevationGrid,
        sourceCell: Cell,
        isDestination: (Cell) -> Boolean,
        canReachToCell: (Cell, Cell) -> Boolean
    ): Int {
        val queue = ArrayDeque<Cell>()
        val visited = HashSet<Cell>()
        queue += sourceCell
        visited += sourceCell

        var steps = 0
        while (queue.isNotEmpty()) {
            repeat(queue.size) {
                val cell = queue.removeFirst()
                if (isDestination(cell)) return steps

                cell.adjacentCells.forEach { adjacent ->
                    if(
                        adjacent in grid &&
                        adjacent !in visited &&
                        canReachToCell(cell, adjacent)
                    ) {
                        queue += adjacent
                        visited += adjacent
                    }
                }
            }
            steps++
        }

        return -1
    }

    fun part1(input: String): Int {
        val grid = ElevationGrid.fromString(input)
        val sourceCell = grid.findOrNull('S') ?: error("S is not in grid!")
        val destinationCell = grid.findOrNull('E') ?: error("E is not in grid!")

        return shortestPathLength(
            grid,
            sourceCell,
            { it == destinationCell },
            { fromCell, toCell -> grid.elevationAt(toCell) <= grid.elevationAt(fromCell) + 1 }
        )
    }

    fun part2(input: String): Int {
        val grid = ElevationGrid.fromString(input)
        val sourceCell = grid.findOrNull('E') ?: error("E is not in grid!")

        return shortestPathLength(
            grid,
            sourceCell,
            { grid.elevationAt(it) == 'a' },
            { fromCell, toCell -> grid.elevationAt(fromCell) <= grid.elevationAt(toCell) + 1 }
        )
    }

    val input = readText("Input12")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

private class ElevationGrid(private val chars: List<List<Char>>) {
    val rowCount get() = chars.size
    val colCount get() = chars[0].size

    operator fun contains(cell: Cell): Boolean =
        cell.row in 0 until rowCount && cell.col in 0 until colCount

    fun elevationAt(cell: Cell): Char =
        when (val ch = chars[cell.row][cell.col]) {
            'S' -> 'a'
            'E' -> 'z'
            else -> ch
        }

    fun findOrNull(charToFind: Char): Cell? {
        chars.forEachIndexed { row, rowChars ->
            rowChars.forEachIndexed { col, ch ->
                if (ch == charToFind) return Cell(row, col)
            }
        }
        return null
    }

    companion object {
        fun fromString(input: String) = ElevationGrid(
            input.lines().map(String::toList)
        )
    }
}

private data class Cell(val row: Int, val col: Int) {
    val adjacentCells: List<Cell>
        get() = listOf(
            copy(col = col - 1),
            copy(row = row - 1),
            copy(col = col + 1),
            copy(row = row + 1)
        )
}
