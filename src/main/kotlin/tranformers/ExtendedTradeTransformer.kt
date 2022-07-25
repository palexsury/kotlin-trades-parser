package tranformers

import structures.AbstractTrade
import structures.AbstractTrade.Direction
import structures.ExtendedTrade.Version
import formats.Tag
import structures.ExtendedTrade

object ExtendedTradeTransformer : AbstractTradeTransformer() {

    private const val versionLength = 4
    private const val directionLength = 4
    private const val nestedTagsMinLength = 2
    private val directionsMap = mapOf(Pair("BUY_", Direction.BUY), Pair("SELL", Direction.SELL))
    private const val requiredMinInputLength = dateTimeLength + directionLength + itemIDLength + priceLength + quantityLength + firmIDLength * 2 + nestedTagsMinLength


    private lateinit var version: Version
    private var nestedParams: MutableList<Tag> = mutableListOf()

    override fun validateDirection(direction: String) {
        require(directionsMap.containsKey(direction)) {"Unsupported trade direction \"$direction\" format, supported versions: $directionsMap"}
        this.direction = directionsMap[direction]!!
    }

    override fun getFromString(input: String): AbstractTrade {
        validateLength(input)
        var index = 0
        validateVersion(input.substring(index, index + versionLength))
        index += versionLength
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
        validateNestedParams(input.substring(index))
        return ExtendedTrade(version, direction, dateTime, itemID, price, qantity, buyer, seller, nestedParams.toList())
    }

    override fun validateLength(input: String) {
        require(input.length >= requiredMinInputLength) {"The length of the input string \"$input\" doesn't math the required match format for <Extended Trade> structure"}
    }

    private fun validateVersion(version: String) {
        require(!version.startsWith('+') && !version.startsWith('-')) {"Extended trade version \"$version\" doesn't match the required format: U(4)"}
        val versionNumber = version.toIntOrNull()
        requireNotNull(versionNumber) {"Extended trade version \"$version\" doesn't match the required format: U(4)"}
        val tempVersion =  Version.getByNumber(versionNumber)
        requireNotNull(tempVersion) {"Unsupported Extended trade version \"$version\", supported versions: ${Version.values().contentToString()}"}
        this.version = tempVersion
    }

    private fun validateNestedParams(input: String) {
        nestedParams = mutableListOf()
        require(input.startsWith("{") && input.endsWith("}")) {"Nested parameters must be enclosed in curly braces"}
        splitParams(input.substring(1, input.length - 1)).forEach{
            nestedParams.add(getTagFromNestedString(it))
        }
        val a = 9
    }

    private fun getTagFromNestedString(nested: String): Tag {
        val bracePos = nested.indexOf('{')
        if (bracePos == -1) {
            return Tag(nested)
        }
        val innerTags: MutableList<Tag> = mutableListOf()
        splitParams(nested.substring(bracePos + 1, nested.length - 1)).forEach {
            innerTags.add(getTagFromNestedString(it))
        }
        return Tag(nested.substring(0, bracePos), innerTags)
    }

    private fun splitParams(nested: String) : List<String> {
        var bracesCount = 0
        var pos = 0
        val nestedStrings = mutableListOf<String>()
        for (i in 1 until nested.length) {
            when (nested[i]) {
                '}' -> {
                    if (bracesCount <= 0)
                        throw IllegalArgumentException("braces in nested parameters $nested are not balanced (position: $i)")
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
        require(bracesCount == 0) {"braces in nested parameters $nested are not balanced"}
        if (pos != nested.length) {
            nestedStrings.add(nested.substring(pos, nested.length))
        }
        return nestedStrings
    }




}