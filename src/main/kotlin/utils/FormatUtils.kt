package utils

import formats.Tag
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object FormatUtils {

    fun getLongFromString(string: String, signed: Boolean) : Long? {
        require(signed == isSigned(string)) {
            return null
        }
        return string.toLongOrNull()
    }

    fun getDecimalFromString(string: String, scale: Int, signed: Boolean) : BigDecimal? {
        require(signed == isSigned(string)) {
            return null
        }
        return try {
            BigDecimal(BigInteger(string), scale)
        } catch (ex: Exception) {
            null
        }
    }

    private fun isSigned(string: String) : Boolean {
        return string.startsWith('+') || string.startsWith('-')
    }

    fun getNestedParams(input: String): List<Tag> {
        val nestedParams = mutableListOf<Tag>()
        require(input.startsWith("{") && input.endsWith("}")) {"Nested parameters $input must be enclosed in curly braces"}
        splitNestedParams(input.substring(1, input.length - 1)).forEach{
            nestedParams.add(getTagFromNestedString(it))
        }
        return nestedParams
    }

    private fun getTagFromNestedString(nested: String): Tag {
        val bracePos = nested.indexOf('{')
        if (bracePos == -1) {
            return Tag(nested)
        }
        val innerTags: MutableList<Tag> = mutableListOf()
        splitNestedParams(nested.substring(bracePos + 1, nested.length - 1)).forEach {
            innerTags.add(getTagFromNestedString(it))
        }
        return Tag(nested.substring(0, bracePos), innerTags)
    }

    private fun splitNestedParams(nested: String): List<String> {
        var bracesCount = 0
        var pos = 0
        val nestedStrings = mutableListOf<String>()
        for (i in 1 until nested.length) {
            when (nested[i]) {
                '}' -> {
                    if (bracesCount <= 0)
                        throw IllegalArgumentException("Braces in nested parameters $nested are not balanced (position: $i)")
                    bracesCount--
                    if (bracesCount == 0) {
                        nestedStrings.add(nested.substring(pos, i + 1))
                        pos = i + 1
                    }
                    continue
                }
                '{' -> {
                    bracesCount++
                    continue
                }
                '|' -> {
                    if (bracesCount == 0) {
                        nestedStrings.add(nested.substring(pos, i))
                        pos = i + 1
                        continue
                    }
                }
            }
        }
        require(bracesCount == 0) {"Braces in nested parameters $nested are not balanced"}
        if (pos != nested.length) {
            nestedStrings.add(nested.substring(pos, nested.length))
        }
        return nestedStrings
    }

    fun getPrettyStringFromTags(tags: List<Tag>): String {
        val prettyTagsStrings = mutableListOf<String>()
        tags.forEach{
            prettyTagsStrings.add(prettyTag(it, 0))
        }
        var result = ""
        prettyTagsStrings.forEach {
            result += it
        }
        return result
    }

    private fun prettyTag(tag: Tag, offSet: Int): String {
        if (tag.innerTags.isEmpty()) {
            return "${"".padStart(offSet)}${tag.value}\n"
        }
        var result = "${"".padStart(offSet)}${tag.value}:\n"
        tag.innerTags.forEach{
            result += prettyTag(it, offSet + 4)
        }
        return result
    }


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