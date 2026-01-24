package dev.zt64.aliucord.plugins.frecents.gif

import java.util.regex.Pattern

object GifUtil {
    private val pattern = Pattern.compile("(https/.*?)$")

    /**
     * Converts a Tenor GIF URL to a medium quality GIF URL that displays faster
     */
    fun mqGifUrl(src: String): String {
        return when {
            src.startsWith("//") -> {
                "https:$src"
            }
            else -> {
                val matcher = pattern.matcher(src)
                if (matcher.find()) {
                    matcher.group(0)!!.replace("https/", "https://")
                } else {
                    src
                }
            }
        }
            .replace("AAAPo", "AAAAM")
            .replace(".mp4", ".gif")

            .replace("AAAPs", "AAAAM")
            .replace(".webm", ".gif")
    }
}