import structures.AbstractTrade
import structures.Data
import structures.Footer
import structures.Header
import tranformers.*
import java.io.File


class RecordExtractionException(
    private val record: String,
    private val index: Int,
    ex: Exception
): Exception(ex) {
    override val message: String
        get() = "Unable to extract data from record[$index]: \"$record\""
}

class Extractor {

    companion object {
        private const val tagLength = 5
        private const val supportedHeaderTag = "HEADR"
        private const val supportedFooterTag = "FOOTR"

        private val tradeTransformerToTag = mapOf(
            Pair("TRADE", TradeTransformer),
            Pair("EXTRD", ExtendedTradeTransformer))

        fun extractData(path: String): Data {
            val records = File(path).readLines()
            val headerRecord = records.first()
            val headerTag = getTag(headerRecord)
            require(headerTag == supportedHeaderTag) { "Unrecognized header tag: \"$headerTag\", supported tag for header: \"$supportedHeaderTag\"" }
            val header: Header
            try {
                header = HeaderTransformer.getFromString(headerRecord.substring(tagLength))
            } catch (ex: Exception) {
                throw RecordExtractionException(headerRecord, 0, ex)
            }
            val fileVersion = header.version
            val tradesCount = records.size - 2
            var tradesCharsCount = 0
            val trades = mutableListOf<AbstractTrade>()
            for (i in 1..records.size - 2) {
                val tradeRecord = records[i]
                val tradeTag = getTag(tradeRecord)
                require(tradeTransformerToTag.containsKey(tradeTag)) { "record[$i], record: $tradeRecord\nUnrecognized trade tag: \"$tradeTag\", supported tags for trade structure: ${tradeTransformerToTag.keys}" }
                try {
                    trades.add(tradeTransformerToTag[tradeTag]!!.getFromString(tradeRecord.substring(tagLength)))
                } catch (ex: Exception) {
                    throw RecordExtractionException(tradeRecord, i, ex)
                }
                tradesCharsCount += tradeRecord.length
            }
            val footerRecord = records.last()
            val footerTag = getTag(footerRecord)
            require(footerTag == supportedFooterTag) { "Unrecognized footer tag: \"$footerTag\", supported tag for header: \"$supportedFooterTag\"" }
            val footer: Footer
            try {
                footer = FooterTransformer(fileVersion, tradesCount, tradesCharsCount).getFromString(
                    footerRecord.substring(tagLength)
                )
            } catch (ex: Exception) {
                throw RecordExtractionException(footerRecord, 0, ex)
            }
            return Data(header, trades, footer)
        }

        private fun getTag(record: String): String {
            try {
                return record.substring(0, tagLength)
            } catch (e: Exception) {
                throw IllegalArgumentException("Unable to get tag of the record: $record")
            }
        }
    }
}