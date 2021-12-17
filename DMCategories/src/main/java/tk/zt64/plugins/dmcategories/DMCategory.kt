package tk.zt64.plugins.dmcategories

data class DMCategory(
        val userId: Long,
        var name: String,
        val channelIds: ArrayList<Long> = ArrayList(),
        var collapsed: Boolean = false
)