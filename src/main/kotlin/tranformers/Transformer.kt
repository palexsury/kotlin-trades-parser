package tranformers

interface Transformer<T> {
    fun getFromString(input: String) : T

    fun validateLength(input: String)

}