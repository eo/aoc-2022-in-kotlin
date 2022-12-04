import java.io.File

/**
 * Reads the input file content as a list of lines.
 */
fun readLines(name: String) = inputFile(name).readLines()

/**
 * Reads the entire content of input file as a String.
 */
fun readText(name: String) = inputFile(name).readText()

private fun inputFile(name: String) = File("src", name)
