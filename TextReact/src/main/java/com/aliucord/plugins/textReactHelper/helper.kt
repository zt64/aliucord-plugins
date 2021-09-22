package com.aliucord.plugins.textReactHelper

// huge thanks to https://github.com/Juby210/text-react/blob/master/index.js

class helper {
    private data class ReactionsType(
        val single: MutableMap<String, ArrayList<String>>,
        val multiple: MutableMap<String, ArrayList<String>>,
    )

    private val reactions: ReactionsType = ReactionsType(
        single=mutableMapOf(
            "a" to arrayListOf("\uD83C\uDDE6", "\uD83C\uDD70"),
            "b" to arrayListOf("\uD83C\uDDE7", "\uD83C\uDD71"),
            "c" to arrayListOf("\uD83C\uDDE8", "©"),
            "d" to arrayListOf("\uD83C\uDDE9"),
            "e" to arrayListOf("\uD83C\uDDEA", "\uD83D\uDCE7"),
            "f" to arrayListOf("\uD83C\uDDEB"),
            "g" to arrayListOf("\uD83C\uDDEC"),
            "h" to arrayListOf("\uD83C\uDDED", "♓"),
            "i" to arrayListOf("\uD83C\uDDEE", "ℹ"),
            "j" to arrayListOf("\uD83C\uDDEF"),
            "k" to arrayListOf("\uD83C\uDDF0"),
            "l" to arrayListOf("\uD83C\uDDF1"),
            "m" to arrayListOf("\uD83C\uDDF2", "Ⓜ", "♏", "♍"),
            "n" to arrayListOf("\uD83C\uDDF3", "♑"),
            "o" to arrayListOf("\uD83C\uDDF4", "\uD83C\uDD7E", "⭕"),
            "p" to arrayListOf("\uD83C\uDDF5", "\uD83C\uDD7F"),
            "q" to arrayListOf("\uD83C\uDDF6"),
            "r" to arrayListOf("\uD83C\uDDF7", "®"),
            "s" to arrayListOf("\uD83C\uDDF8"),
            "t" to arrayListOf("\uD83C\uDDF9", "✝"),
            "u" to arrayListOf("\uD83C\uDDFA"),
            "v" to arrayListOf("\uD83C\uDDFB", "♈"),
            "w" to arrayListOf("\uD83C\uDDFC"),
            "x" to arrayListOf("\uD83C\uDDFD", "❎", "❌", "✖"),
            "y" to arrayListOf("\uD83C\uDDFE"),
            "z" to arrayListOf("\uD83C\uDDFF"),
            "0" to arrayListOf("0️⃣"),
            "1" to arrayListOf("1️⃣"),
            "2" to arrayListOf("2️⃣"),
            "3" to arrayListOf("3️⃣"),
            "4" to arrayListOf("4️⃣"),
            "5" to arrayListOf("5️⃣"),
            "6" to arrayListOf("6️⃣"),
            "7" to arrayListOf("7️⃣"),
            "8" to arrayListOf("8️⃣"),
            "9" to arrayListOf("9️⃣"),
            "?" to arrayListOf("❔", "❓"),
            "+" to arrayListOf("➕"),
            "-" to arrayListOf("➖", "⛔", "\uD83D\uDCDB"),
            "!" to arrayListOf("❕", "❗"),
            "*" to arrayListOf("*️⃣"),
            "$" to arrayListOf("\uD83D\uDCB2"),
            "#" to arrayListOf("#️⃣"),
            " " to arrayListOf("▪", "◾", "➖", "◼", "⬛", "⚫", "\uD83D\uDDA4", "\uD83D\uDD76")
        ),
        multiple=mutableMapOf(
            "wc" to arrayListOf("\uD83D\uDEBE"),
            "back" to arrayListOf("\uD83D\uDD19"),
            "end" to arrayListOf("\uD83D\uDD1A"),
            "on!" to arrayListOf("\uD83D\uDD1B"),
            "soon" to arrayListOf("\uD83D\uDD1C"),
            "top" to arrayListOf("\uD83D\uDD1D"),
            "!!" to arrayListOf("‼"),
            "!?" to arrayListOf("⁉"),
            "tm" to arrayListOf("™"),
            "10" to arrayListOf("\uD83D\uDD1F"),
            "cl" to arrayListOf("\uD83C\uDD91"),
            "cool" to arrayListOf("\uD83C\uDD92"),
            "free" to arrayListOf("\uD83C\uDD93"),
            "id" to arrayListOf("\uD83C\uDD94"),
            "new" to arrayListOf("\uD83C\uDD95"),
            "ng" to arrayListOf("\uD83C\uDD96"),
            "ok" to arrayListOf("\uD83C\uDD97"),
            "sos" to arrayListOf("\uD83C\uDD98"),
            "up!" to arrayListOf("\uD83C\uDD99"),
            "vs" to arrayListOf("\uD83C\uDD9A"),
            "abc" to arrayListOf("\uD83D\uDD24"),
            "ab" to arrayListOf("\uD83C\uDD8E"),
            "18" to arrayListOf("\uD83D\uDD1E"),
            "100" to arrayListOf("\uD83D\uDCAF"),
            "atm" to arrayListOf("\uD83C\uDFE7")
        )
    )

    fun generateEmojiArray(string: String): Pair<List<String>, Boolean> {
        val unusedReactions = reactions
        var msg = string.lowercase()
        val newReactions = ArrayList<String>()

        val allReactions = ArrayList((unusedReactions.single + unusedReactions.multiple).keys)
        var incomplete = false

        while (msg.isNotEmpty()) {
            if (!allReactions.contains(msg[0].toString())) {
                msg = msg.slice(1 until msg.length)
                incomplete = true
            }
            for (reactionName in unusedReactions.multiple.keys) {
                if (msg.isNotEmpty() && msg.startsWith(reactionName) && unusedReactions.multiple[reactionName]!!.isNotEmpty()) {
                    val reactionValue = unusedReactions.multiple[reactionName]!!.first()
                    newReactions.add(reactionValue)
                    unusedReactions.multiple[reactionName] = ArrayList(unusedReactions.multiple[reactionName]!!.filter { value -> value != reactionValue })
                    msg = msg.replace(reactionName, "")
                } else if (msg.isNotEmpty() && msg.startsWith(reactionName) && unusedReactions.multiple[reactionName]!!.isEmpty()) {
                    msg = msg.replaceFirst(reactionName, "")
                    incomplete = true
                }
            }
            for (reactionName in unusedReactions.single.keys) {
                if (msg.isNotEmpty() && msg.startsWith(reactionName) && unusedReactions.single[reactionName]!!.isNotEmpty()) {
                    val reactionValue = unusedReactions.single[reactionName]!!.first()
                    newReactions.add(reactionValue)
                    unusedReactions.single[reactionName] = ArrayList(unusedReactions.single[reactionName]!!.filter { value -> value != reactionValue })
                    msg = msg.replaceFirst(reactionName, "")
                } else if (msg.isNotEmpty() && msg.startsWith(reactionName) && unusedReactions.single[reactionName]!!.isEmpty()) {
                    msg = msg.replaceFirst(reactionName, "")
                    incomplete = true
                }
            }
        }
        return Pair(newReactions, incomplete)
    }
}