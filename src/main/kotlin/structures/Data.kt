package structures

data class Data(
    val header: Header,
    var trades: List<AbstractTrade>,
    val footer: Footer
) {
    fun sortTrades() {
        trades = trades.sortedByDescending { it.value }
    }
}