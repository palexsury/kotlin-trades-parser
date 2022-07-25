package structures

import java.time.LocalDateTime

data class Header(
    val version : Version,
    val dateTime : LocalDateTime,
    val comment : String?
) {

    enum class Version(val number : Int) {
        FOUR(4),
        FIVE(5);

        companion object {
            private val VALUES = values()
            fun getByNumber(value: Int) = VALUES.firstOrNull { it.number == value }
        }
    }

}