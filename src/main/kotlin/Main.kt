import java.io.File

fun main(args: Array<String>) {
    println("Enter *.dat source file path")
    val sourcePath = readLine()!!

    val data = Extractor.extractData(sourcePath)
    data.sortTrades()

    val outputPath = "output/${File(sourcePath).name.replace(".dat", ".csv")}"
    Printer.printData(outputPath, data)
    println("Successfully extracted trades from $sourcePath and loaded sorted data into $outputPath")
}