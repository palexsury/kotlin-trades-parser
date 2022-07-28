package tranformers

import structures.Header
import structures.Header.Version
import utils.FormatUtils
import java.time.LocalDateTime

object HeaderTransformer : Transformer<Header> {

    private const val versionLength = 4
    private const val dateTimeLength = 17
    private const val minimumRequiredInputLength = versionLength + dateTimeLength
    private lateinit var version: Version
    private lateinit var dateTime: LocalDateTime
    private var comment: String? = null

    override fun getFromString(input: String): Header {
        validateLength(input)
        var index = 0
        validateVersion(input.substring(index, index + versionLength))
        index += versionLength
        validateDateTime(input.substring(index, index + dateTimeLength))
        index += dateTimeLength
        return if (version.number < 5) {
            require(index == input.length) {"HEADER content length doesn't match the required length: (5),(4),(17)"}
            Header(version, dateTime, null)
        } else {
            validateComment(input.substring(index))
            Header(version, dateTime, comment)
        }
    }

    override fun validateLength(input: String) {
        require(input.length >= minimumRequiredInputLength) {"The length of the input string \"$input\" doesn't math the required match format: (5),(4),(17),Text?"}
    }

    private fun validateVersion(version : String) {
        val versionNumber = FormatUtils.getLongFromString(version, false)
        requireNotNull(versionNumber) {"File version \"$version\" doesn't match the required format: U(4)"}
        val tempVersion = Version.getByNumber(versionNumber.toInt())
        requireNotNull(tempVersion) {"Unsupported file version \"$version\", supported versions: ${Version.values().contentToString()}"}
        this.version = tempVersion
    }

    private fun validateDateTime(dateTime : String) {
        val temDateTime = FormatUtils.getDateTime(dateTime)
        requireNotNull(temDateTime) {"File creation date and time \"$dateTime\" don't match the required format: YYYYMMddHHmmssSSS"}
        this.dateTime = temDateTime
    }

    private fun validateComment(commentText : String) {
        comment = FormatUtils.getStringFromText(commentText)
        requireNotNull(comment) {"File comment \"$commentText\" doesn't match the required format: Text"}
    }

}