package formats

data class Tag(val value: String, val innerTags: List<Tag> = emptyList()) {
    init {
        require(value.length in 1..4 && "[A-Z,0-9]+".toRegex().matches(value))
        {"Tag value \"$value\" doesn't match the required format: 1 - 4 characters, only uppercase letters and numbers are allowed"}
    }

}