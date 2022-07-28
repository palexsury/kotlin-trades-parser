package structures

import java.math.BigDecimal
import java.time.LocalDateTime

data class Trade(
    override val direction: Direction,
    override val dateTime: LocalDateTime,
    override val itemID: String,
    override val price: BigDecimal,
    override val quantity: Long,
    override val buyer: String,
    override val seller: String,
    val comment: String
    ) : AbstractTrade(direction, dateTime, itemID, price, quantity, buyer, seller) {

}