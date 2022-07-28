package tranformers

import structures.AbstractTrade
import utils.FormatUtils
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.time.LocalDateTime


abstract class AbstractTradeTransformer : Transformer<AbstractTrade> {

    companion object {
        const val dateTimeLength = 17
        const val itemIDLength = 12
        const val priceLength = 15
        const val priceScale = 4
        const val quantityLength = 11
        const val firmIDLength = 4
        val itemIDRegex = "[A-Z]{3}[A-Z0-9]{9}".toRegex()
        val firmIDRegex = "[A-Z0-9a-z_]{4}".toRegex()
    }

    lateinit var dateTime: LocalDateTime
    lateinit var direction: AbstractTrade.Direction
    lateinit var itemID: String
    var price: BigDecimal = ZERO
    var quantity: Long = 0

    lateinit var buyer: String
    lateinit var seller: String

    protected abstract fun validateDirection(direction: String)

    protected fun validateDateTime(dateTime: String) {
        val temDateTime = FormatUtils.getDateTime(dateTime)
        requireNotNull(temDateTime) { "Trade date and time \"$dateTime\" don't match the required format: YYYYMMddHHmmssSSS" }
        this.dateTime = temDateTime
    }

    protected fun validateItemID(itemID: String) {
        require(itemIDRegex.matches(itemID)) {"Item ID \"$itemID\" doesn't match the required format: \"$itemIDRegex\""}
        this.itemID = itemID
    }

    protected fun validatePrice(price: String) {
        val tempPrice = FormatUtils.getDecimalFromString(price, priceScale, true)
        requireNotNull(tempPrice) {"Price \"$price\" doesn't match the required format: Decimal(15:4)"}
        this.price = tempPrice
    }

    protected fun validateQuantity(quantity: String) {
        val tempQuantity = FormatUtils.getLongFromString(quantity, true)
        requireNotNull(tempQuantity) {"Quantity \"$quantity\" doesn't match the required format: Integer(S:10)"}
        require(tempQuantity > 0) {"Quantity \"$quantity\" is zero ot negative"}
        this.quantity = tempQuantity
    }

    private fun validateFirmID(firmID: String) {
        require(firmIDRegex.matches(firmID)) {"Firm ID \"$firmID\" doesn't match the required format: \"$firmIDRegex\""}
    }

    protected fun validateBuyer(buyer: String) {
        validateFirmID(buyer)
        this.buyer = buyer
    }

    protected fun validateSeller(seller: String) {
        validateFirmID(seller)
        this.seller = seller
    }

}