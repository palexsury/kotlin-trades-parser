package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object FormatUtils {

    fun getDateTime(dateTime : String) : LocalDateTime? {
        val formatter =  DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
        return try {
            LocalDateTime.parse(dateTime, formatter)
        } catch (e : DateTimeParseException) {
            null
        }
    }

    fun getStringFromText(text : String) : String? {
        if (!text.startsWith("{")) {
            return null
        }
        var n = ""
        var i = 1
        while (i < text.length) {
            if (text[i] == '}') break
            n += text[i]
            i++
        }
        if (i >= text.length) {
            return null
        }
        if (text[i] != '}') {
            return null
        }
        val length = n.toIntOrNull() ?: return null
        if (n.length + 2 + length != text.length) {
            return null
        }
        return text.substring(2 + n.length)
    }

}