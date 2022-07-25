package structures

import java.time.LocalDateTime

open class AbstractTrade (
    open val direction : Direction,
    open val dateTime : LocalDateTime,
    open val itemID: String,
    open val price : Double,
    open val quantity : Int,
    open val buyer : String,
    open val seller : String,
    open val value: Double = price * quantity
    ) {
    enum class Direction {
        SELL, BUY
    }
}