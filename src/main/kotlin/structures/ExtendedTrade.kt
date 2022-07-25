package structures

import formats.Tag
import java.time.LocalDateTime

data class ExtendedTrade(
    val version : Version,
    override val direction : Direction,
    override val dateTime: LocalDateTime,
    override val itemID: String,
    override val price: Double,
    override val quantity: Int,
    override val buyer: String,
    override val seller: String,
    val nestedParameters: List<Tag>
) : AbstractTrade(direction, dateTime, itemID, price, quantity, buyer, seller) {

    enum class Version(val number: Int) {
        ONE(1);

        companion object {
            private val VALUES = values()
            fun getByNumber(value: Int) = VALUES.firstOrNull { it.number == value }
        }
    }
}