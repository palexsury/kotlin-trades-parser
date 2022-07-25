package tranformers

import structures.AbstractTrade
import utils.FormatUtils
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

abstract class AbstractTradeTransformer : Transformer<AbstractTrade> {

    companion object {
        const val dateTimeLength = 17
        const val itemIDLength = 12
        const val priceLength = 15
        const val priceScale = 4
        const val quantityLength = 10
        const val firmIDLength = 4
    }

    lateinit var dateTime: LocalDateTime
    lateinit var direction: AbstractTrade.Direction
    lateinit var itemID: String
    var price: Double = 0.0
    var qantity: Int = 0

    lateinit var buyer: String
    lateinit var seller: String

    protected abstract fun validateDirection(direction: String)

    protected fun validateDateTime(dateTime: String) {
        val temDateTime = FormatUtils.getDateTime(dateTime)
        requireNotNull(temDateTime) { "Trade date and time \"$dateTime\" don't match the required format: YYYYMMddHHmmssSSS" }
        this.dateTime = temDateTime
    }

    protected fun validateItemID(itemID: String) {
        require("[A-Z]{3}[A-Z,0-9]{9}".toRegex().matches(itemID)) {"Item ID \"$itemID\" doesn't match the required format: \"[A-Z]{3}[A-Z,0-9]{9}\""}
        this.itemID = itemID
    }

    protected fun validatePrice(price: String) {
        require(price.startsWith('-') || price.startsWith('+')) {"Price \"$price\" doesn't math the required format: Decimal(15:4) - first character must be occupied by sign(+ or -)"}
        val result: Double?
        try {
            result = BigDecimal(BigInteger(price), priceScale).toDouble()
        } catch (e: java.lang.NumberFormatException) {
            throw IllegalArgumentException("Price \"$price\" doesn't math the required format: Decimal(15:4)")
        }
        this.price = result
    }

    protected fun validateQuantity(quantity: String) {
        require(quantity.startsWith('+') || quantity.startsWith(('-'))) {"Quantity \"$quantity\" doesn't math the required format: Integer(S:10)"}
        val result: Int?
        try {
            result = BigInteger(quantity).toInt()
        } catch (e: java.lang.NumberFormatException) {
            throw IllegalArgumentException("Quantity \"$quantity\" doesn't math the required format: Integer(S:10)")
        }
        if (result <=0 ) {
            throw IllegalArgumentException("Quantity \"$quantity\" is zero ot negative")
        }
        this.qantity = result
    }

    private fun validateFirmID(firmID: String) {
        require("[A-Z,0-9,a-z_]{4}".toRegex().matches(firmID)) {"Firm ID \"$firmID\" doesn't match the required format: \"[A-Z,0-9,_]{4}\""}
    }

    protected fun validateBuyer(buyer: String) {
        validateFirmID(buyer);
        this.buyer = buyer
    }

    protected fun validateSeller(seller: String) {
        validateFirmID(seller)
        this.seller = seller
    }

}