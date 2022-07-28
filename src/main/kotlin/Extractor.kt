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
        get() = "Unable to extract data from record[$index]: \"$record\", ${super.message} "
}

class Extractor {

    companion object {
        private const val tagLength = 5
        private lateinit var fileVersion: Header.Version
        private const val supportedHeaderTag = "HEADR"
        private const val supportedFooterTag = "FOOTR"

        private val tradeTransformerToTag = mapOf(
            Pair("TRADE", TradeTransformer),
            Pair("EXTRD", ExtendedTradeTransformer))

        fun extractData(path: String): Data {
            val records = File(path).readLines()
            val headerRecord = records.first()
            val header = extractHeader(headerRecord)
            fileVersion = header.version

            val tradesCount = (records.size - 2).toLong()
            var tradesCharsCount: Long = 0
            val trades = mutableListOf<AbstractTrade>()
            val errors = mutableListOf<Exception>()
            for (i in 1..records.size - 2) {
                val tradeRecord = records[i]
                try {
                    trades.add(extractTrade(tradeRecord, i))
                    tradesCharsCount += tradeRecord.length
                }
                catch (ex: Exception) {
                    errors.add(RecordExtractionException(tradeRecord, i, ex))
                }
            }
            if (errors.isNotEmpty()) {
                throw IllegalArgumentException("Some trade records are incorrect: ${System.lineSeparator()}${errors.joinToString(System.lineSeparator())}")
            }

            val footerRecord = records.last()
            val footer = extractFooter(footerRecord, tradesCount, tradesCharsCount)
            return Data(header, trades, footer)
        }

        private fun getTag(record: String): String {
            try {
                return record.substring(0, tagLength)
            } catch (e: Exception) {
                throw IllegalArgumentException("Unable to get tag of the record: $record")
            }
        }

        private fun extractHeader(headerRecord: String): Header {
            val headerTag = getTag(headerRecord)
            require(headerTag == supportedHeaderTag) { "Unrecognized header tag: \"$headerTag\", supported tag for header: \"$supportedHeaderTag\"" }
            return try {
                HeaderTransformer.getFromString(headerRecord.substring(tagLength))
            } catch (ex: Exception) {
                throw RecordExtractionException(headerRecord, 0, ex)
            }
        }

        private fun extractTrade(tradeRecord: String, index: Int): AbstractTrade {
            val tradeTag = getTag(tradeRecord)
            require(tradeTransformerToTag.containsKey(tradeTag)){ "record[$index], record: $tradeRecord\nUnrecognized trade tag: \"$tradeTag\", supported tags for trade structure: ${tradeTransformerToTag.keys}" }
            return (tradeTransformerToTag[tradeTag]!!.getFromString(tradeRecord.substring(tagLength)))
        }

        private fun extractFooter(footerRecord: String, tradesCount: Long, tradesCharsCount: Long): Footer {
            val footerTag = getTag(footerRecord)
            require(footerTag == supportedFooterTag) { "Unrecognized footer tag: \"$footerTag\", supported tag for header: \"$supportedFooterTag\"" }
            return try {
                FooterTransformer(fileVersion, tradesCount, tradesCharsCount)
                    .getFromString(footerRecord.substring(tagLength)
                )
            } catch (ex: Exception) {
                throw RecordExtractionException(footerRecord, 0, ex)
            }
        }
    }
}