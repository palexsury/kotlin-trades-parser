package structures

import java.math.BigDecimal
import java.time.LocalDateTime

open class AbstractTrade (
    open val direction : Direction,
    open val dateTime : LocalDateTime,
    open val itemID: String,
    open val price : BigDecimal,
    open val quantity : Long,
    open val buyer : String,
    open val seller : String,
    open val value: BigDecimal = price.times(BigDecimal(quantity))
    ) {
    enum class Direction {
        SELL, BUY
    }
}