package tk.zt64.plugins.pindms

data class DMGroup(
    var name: String,
    val channelIds: ArrayList<Long> = ArrayList(),
    var collapsed: Boolean = false
)