import java.io.File

fun main(args: Array<String>) {
    require(args.size == 1) {"The tool supports only one argument = path to source *.dat file"}
    val sourcePath = args.first()
    val data = Extractor.extractData(sourcePath)
    println("Trades records are successfully extracted from source file")
    data.sortTrades()
    val outputPath = "output/${File(sourcePath).name.replace(".dat", ".csv")}"
    Printer.printData(outputPath, data)
    println("Trades are loaded into $outputPath")
}