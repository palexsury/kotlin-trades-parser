package tranformers

import structures.Footer
import structures.Header.Version

class FooterTransformer(
    private val version: Version,
    private val verifiedTradesCount: Int,
    private val verifiedRTradesCharsCount: Int?
    ) : Transformer<Footer> {

    companion object {
        private const val tradesCountLength = 10
        private const val tradesCharsCountLength = 10
        private const val minimumRequiredInputLength = tradesCountLength
    }

    override fun getFromString(input: String): Footer {
        validateLength(input)
        var index = 0;
        validateTradesCount(input.substring(index, index + tradesCountLength))
        index += tradesCountLength
        return if (version.number < 5) {
            require(index == input.length) {"Footer content length doesn't match the required length: (5),(10)"}
            Footer(verifiedTradesCount)
        } else {
            validateTradesCharsCount(input.substring(index, index + tradesCountLength))
            index += tradesCharsCountLength
            require(index == input.length) {"Footer content length doesn't match the required length: (5),(10),(10)"}
            Footer(verifiedTradesCount, verifiedRTradesCharsCount)
        }
    }

    override fun validateLength(input: String) {
        require(input.length >= minimumRequiredInputLength) { "The length of the input string \"$input\" doesn't math the required match format: (5),(10),(10)?" }
    }

    private fun validateTradesCount(input: String) {
        require(!input.startsWith('+') && !input.startsWith('-')) {"Number of TRADE and EXTRD structures \"$input\" doesn't match the required format: U(10)"}
        val tradeCount = input.toIntOrNull()
        requireNotNull(tradeCount) {"Number of TRADE and EXTRD structures \"$input\" doesn't match the required format: U(10)"}
        require(verifiedTradesCount == tradeCount) {"Number of TRADE and EXTRD structures \"$tradeCount\" not equal verified value \"$verifiedTradesCount\""}
    }

    private fun validateTradesCharsCount(input: String) {
        require(!input.startsWith('+') && !input.startsWith('-')) {"Number of characters in TRADE and EXTRD structures \"$input\" doesn't match the required format: U(10)"}
        val tradeCharsCount = input.toIntOrNull()
        requireNotNull(tradeCharsCount) {"Number of characters in TRADE and EXTRD structures \"$input\" doesn't match the required format: U(10)"}
        require(verifiedRTradesCharsCount == tradeCharsCount) {"Number of characters in TRADE and EXTRD structures \"$tradeCharsCount\" not equal verified value \"$verifiedRTradesCharsCount\""}
    }

}