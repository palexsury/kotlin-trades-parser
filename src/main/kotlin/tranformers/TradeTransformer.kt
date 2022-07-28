package tranformers

import structures.AbstractTrade
import structures.AbstractTrade.Direction
import structures.Trade

object TradeTransformer : AbstractTradeTransformer() {

    private const val directionLength = 1
    private const val commentLength = 32
    private val directionsMap = mapOf(Pair("B", Direction.BUY), Pair("S", Direction.SELL))
    private const val requiredInputLength = dateTimeLength + directionLength + itemIDLength + priceLength + quantityLength + firmIDLength * 2 + commentLength

    lateinit var comment: String

    override fun validateDirection(direction: String) {
        require(directionsMap.containsKey(direction)) {"Unsupported trade direction \"$direction\" format, supported versions: $directionsMap"}
        this.direction = directionsMap[direction]!!
    }

    override fun getFromString(input: String): AbstractTrade {
        validateLength(input)
        var index = 0
        validateDateTime(input.substring(index, index + dateTimeLength))
        index += dateTimeLength
        validateDirection(input.substring(index, index + directionLength))
        index += directionLength
        validateItemID(input.substring(index, index + itemIDLength))
        index += itemIDLength
        validatePrice(input.substring(index, index + priceLength))
        index += priceLength
        validateQuantity(input.substring(index, index + quantityLength))
        index += quantityLength
        validateBuyer(input.substring(index, index + firmIDLength))
        index += firmIDLength
        validateSeller(input.substring(index, index + firmIDLength))
        index += firmIDLength
        validateComment(input.substring(index, index + commentLength))
        return Trade(direction, dateTime, itemID, price, quantity, buyer, seller, comment)
    }

    override fun validateLength(input: String) {
        require(input.length == requiredInputLength) {"The length of the input string \"$input\" doesn't math the required length for TRADE structure"}
    }

    private fun validateComment(comment: String) {
        this.comment = comment.trim()
    }


}