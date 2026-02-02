package okio

// Serves as a workaround for Discord okio dependency issues
class ByteString(val data: ByteArray) {
    val size get() = data.size

    fun size() = size

    companion object {
        @JvmField
        val EMPTY: ByteString = ByteString(byteArrayOf())
    }
}