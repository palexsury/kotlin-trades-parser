import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import structures.*
import utils.FormatUtils
import java.io.FileWriter
import java.io.IOException


class Printer {

    companion object {
        fun printData(path: String, data: Data) {
            try {
                CSVPrinter(FileWriter(path), CSVFormat.EXCEL.builder().setCommentMarker('/').setNullString("N/A").build()).use { printer ->
                    val header = data.header
                    printHeader(printer, header)
                    printer.println()
                    val trades = data.trades
                    if (trades.isNotEmpty()) {
                        printTrades(printer, trades)
                    }
                    printer.println()
                    val footer = data.footer
                    printFooter(printer, footer)
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

        private fun printHeader(printer: CSVPrinter, header: Header) {
            printer.printComment("HEADER")
            printer.printRecord("File version of source *.dat file", "File creation date and time", "File comment")
            printer.printRecord(header.version.number, header.dateTime, header.comment)
        }

        private fun printTrades(printer: CSVPrinter, trades: List<AbstractTrade>) {
            printer.printComment("TRADES")
            printer.printRecord("Type", "Direction", "Trade date and time", "Item ID", "Price", "Quantity", "Total value", "Buyer", "Seller", "Comment", "Nested parameters")
            for (trade in trades) {
                when (trade) {
                    is Trade ->
                        printer.printRecord("TRADE", trade.direction, trade.dateTime, trade.itemID, trade.price, trade.quantity, trade.value, trade.buyer, trade.seller, trade.comment, null)
                    is ExtendedTrade ->
                        printer.printRecord("EXTRD(${trade.version.number})", trade.direction, trade.dateTime, trade.itemID, trade.price, trade.quantity, trade.value, trade.buyer, trade.seller, null, FormatUtils.getPrettyStringFromTags(trade.nestedParameters))
                }
            }
        }

        private fun printFooter(printer: CSVPrinter, footer: Footer) {
            printer.printComment("FOOTER")
            printer.printRecord("Number of TRADE and EXTRD structures", "Number of characters in TRADE and EXTRD structures")
            printer.printRecord(footer.tradeCount, footer.tradesCharsCount)
        }
    }

}