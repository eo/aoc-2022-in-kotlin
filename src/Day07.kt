// https://adventofcode.com/2022/day/7
fun main() {
    fun parseListDirectoryOutput(lines: List<String>): List<File> =
            lines.map { line ->
                val (first, second) = line.split(" ")
                if (first == "dir") {
                    Directory(second)
                } else {
                    DataFile(second, first.toInt())
                }
            }

    fun parseInput(input: String): List<Directory> {
        val parentDirectories = ArrayDeque<Directory>()
        var currentDirectory = Directory("/")
        val allDirectories = mutableListOf<Directory>()
        val commandsAndOutputs = input
                .split("$ ")
                .filterNot(String::isEmpty)
                .drop(1) // skip root
                .map(String::trimEnd)

        commandsAndOutputs.forEach { commandAndOutput ->
            val commandAndOutputLines = commandAndOutput.lines()
            val command = Command.fromString(commandAndOutputLines.first())
            when (command.name) {
                Command.CHANGE_DIRECTORY -> {
                    if (command.parameter == "..") {
                        allDirectories.add(currentDirectory)
                        currentDirectory = parentDirectories.removeLast()
                                .withUpdatedFile(currentDirectory)
                    } else {
                        parentDirectories.add(currentDirectory)
                        currentDirectory =
                                currentDirectory.files
                                        .filterIsInstance<Directory>()
                                        .first { it.name == command.parameter }
                    }
                }
                Command.LIST_DIRECTORY -> {
                    currentDirectory = Directory(
                            currentDirectory.name,
                            parseListDirectoryOutput(commandAndOutputLines.drop(1))
                    )
                }
                else -> {
                    error("Invalid command! ${command.name}")
                }
            }
        }

        allDirectories.add(currentDirectory)
        while (parentDirectories.isNotEmpty()) {
            currentDirectory = parentDirectories.removeLast()
                    .withUpdatedFile(currentDirectory)
            allDirectories.add(currentDirectory)
        }

        return allDirectories
    }

    fun printFileTree(file: File, level: Int = 0) {
        print("\t".repeat(level))
        when (file) {
            is DataFile -> {
                println("${file.name} ${file.size}")
            }
            is Directory -> {
                println("${file.name} ${file.totalSize}")
                file.files.forEach {
                    printFileTree(it, level + 1)
                }
            }
        }
    }

    fun part1(input: String): Int {
        val allDirectories = parseInput(input)
        // printFileTree(allDirectories.last())
        return allDirectories.filter { it.totalSize <= 100000 }.sumOf(Directory::totalSize)
    }

    fun part2(input: String): Int {
        val totalDiskSpace = 70000000
        val neededUnusedSpace = 30000000
        val allDirectories = parseInput(input)
        val rootDirectory = allDirectories.last()
        val unusedSpace = totalDiskSpace - rootDirectory.totalSize
        val needToDelete = neededUnusedSpace - unusedSpace

        return allDirectories.filter { it.totalSize >= needToDelete }.minOf(Directory::totalSize)
    }

    val input = readText("Input07")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

private sealed class File(val name: String)

private class DataFile(name: String, val size: Int) : File(name)

private class Directory(name: String, val files: List<File> = emptyList()) : File(name) {
    val totalSize: Int by lazy {
        files.sumOf {
            when (it) {
                is DataFile -> it.size
                is Directory -> it.totalSize
            }
        }
    }

    fun withUpdatedFile(file: File): Directory =
        Directory(name, files.map {
            if (it.name == file.name) file else it
        })
}

private class Command(val name: String, val parameter: String = "") {
    companion object {
        const val CHANGE_DIRECTORY = "cd"
        const val LIST_DIRECTORY = "ls"

        fun fromString(commandStr: String) =
                if (commandStr.startsWith(CHANGE_DIRECTORY)) {
                    Command(CHANGE_DIRECTORY, commandStr.substringAfter(" "))
                } else if (commandStr.startsWith(LIST_DIRECTORY)) {
                    Command(LIST_DIRECTORY)
                } else {
                    error("Unknown command! $commandStr")
                }
    }
}
