package dev.zt64.aliucord.plugins.frecents.gif

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.discord.utilities.display.DisplayUtils
import com.lytefast.flexinput.R.h.it
import discord_protos.discord_users.v1.FrecencyUserSettings
import java.util.regex.Pattern

object GifUtil {
    private val pattern = Pattern.compile("(https/.*?)$")

    /**
     * Fixup gif urls to display and animate correctly
     **/
    fun fixupGifUrl(src: String): String {
        return when {
            src.contains("media.tenor.com") -> mqGifUrl(src)
            src.contains(".discordapp.") -> addProxyParameters(src)
            else -> src
        }
    }

    /**
     * Adds parameters for discord's proxy to convert most media, like avifs and webps, into
     * animated webps, which Aliu could display.
     *
     * Unfortunately Discord's proxy can not convert mp4 into animated webps, and the view
     * displaying the "GIF" can not play videos. The proxy could however display a static frame
     * of the video, which is still pretty useful.
     */
    @SuppressLint("BuildListAdds")
    fun addProxyParameters(src: String): String {
        // Mirror CoreFixes in ensuring that avif, webp, and any other format supported by Discord's media proxy
        // would be properly animated and displayed
        @SuppressLint("UseKtx")
        return Uri.parse(src).let { uri ->
            val filteredQueries = buildSet {
                add("animated")
                add("format")
            }
            val queryParams = uri.queryParameterNames - filteredQueries
            uri.buildUpon()
                .clearQuery()
                .apply { queryParams.forEach { appendQueryParameter(it, uri.getQueryParameter(it)) } }
                .appendQueryParameter("animated", "true")
                .appendQueryParameter("format", "webp")
                .build()
                .toString()
        }
    }

    /**
     * Converts a Tenor GIF URL to a medium quality GIF URL that displays faster
     */
    private fun mqGifUrl(src: String): String {
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