import kotlin.math.sign

// https://adventofcode.com/2022/day/13
fun main() {
    fun part1(input: List<String>): Int {
        return input
            .windowed(size = 2, step = 3)
            .map { (first, second) ->
                PacketListItem.fromString(first) to PacketListItem.fromString(second)
            }
            .mapIndexed { index, (first, second) ->
                if (first <= second) index + 1 else 0
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val packets = input
            .filter(String::isNotEmpty)
            .map(PacketListItem::fromString)
        val dividers = listOf(
            PacketListItem.fromString("[[2]]"),
            PacketListItem.fromString("[[6]]")
        )

        return (packets + dividers)
            .asSequence()
            .sorted()
            .withIndex()
            .filter { (_, packet) -> packet in dividers}
            .map { it.index }
            .fold(1) { acc, index -> acc * (index + 1) }
    }

    val input = readLines("Input13")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

private sealed class PacketItem {
    private fun asList() = when (this) {
        is PacketSingleItem -> PacketListItem(listOf(this))
        is PacketListItem -> this
    }

    operator fun compareTo(other: PacketItem): Int =
        if (this is PacketSingleItem && other is PacketSingleItem) {
            this.compareTo(other)
        } else {
            asList().compareTo(other.asList())
        }
}

private class PacketSingleItem(val value: Int): PacketItem() {
    override fun toString() = value.toString()

    operator fun compareTo(other: PacketSingleItem) = (value - other.value).sign
}

private class PacketListItem(
    val items: List<PacketItem>
): PacketItem(), Comparable<PacketListItem> {
    override fun toString() = "[${items.joinToString(",")}]"

    override operator fun compareTo(other: PacketListItem): Int {
        (items zip other.items).forEach { (left, right) ->
            val compareToResult = left.compareTo(right)
            if (compareToResult != 0) {
                return compareToResult
            }
        }
        return (items.size - other.items.size).sign
    }

    companion object {
        fun fromString(str: String) = fromCharDeque(ArrayDeque(str.toList()))

        private fun fromCharDeque(charDeque: ArrayDeque<Char>): PacketListItem {
            val items = mutableListOf<PacketItem>()
            if (charDeque.removeFirstOrNull() != '[') error("List should start with '['")

            while(charDeque.isNotEmpty()) {
                when (charDeque.first()) {
                    '[' -> items.add(fromCharDeque(charDeque))
                    in '0'..'9' -> {
                        var number = charDeque.removeFirst().digitToInt()
                        while (charDeque.first().isDigit()) {
                            number = number * 10 + charDeque.removeFirst().digitToInt()
                        }
                        items.add(PacketSingleItem(number))
                    }
                    ']' -> {
                        charDeque.removeFirst()
                        break
                    }
                    else -> charDeque.removeFirst()
                }
            }
            return PacketListItem(items)
        }
    }
}
