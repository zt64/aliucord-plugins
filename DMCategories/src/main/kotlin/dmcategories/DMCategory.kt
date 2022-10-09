package dmcategories

data class DMCategory(
    val userId: Long,
    var name: String,
    val channelIds: MutableList<Long> = mutableListOf(),
    var collapsed: Boolean = false
)