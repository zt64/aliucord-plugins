package dev.zt64.aliucord.plugins.frecents

import com.discord.models.domain.emoji.Emoji
import discord_protos.discord_users.v1.FrecencyUserSettings.FrecencyItem

object FrecencyCalculator {
    private const val RECENT_EMOJIS_MAX = 32
    private const val RECENT_STICKERS_MAX = 22

    private const val MAX_SAMPLES = 10
    private const val BONUS = 1.0

    /**
     * @param frecencyMap A map of emoji IDs to their frecency data.
     * @param emojiIdsMap A map of emoji IDs to their corresponding Emoji objects.
     * @param unicodeEmojisMap A map of unicode emoji strings to their corresponding Emoji objects
     */
    fun sortEmojis(
        frecencyMap: Map<String, FrecencyItem>,
        emojiIdsMap: Map<String, Emoji>,
        unicodeEmojisMap: Map<String, Emoji>
    ): List<Emoji> {
        return sortByFrecency(frecencyMap, RECENT_EMOJIS_MAX) { key ->
            emojiIdsMap[key] ?: unicodeEmojisMap[key]
        }
    }

    /**
     * @param frecencyMap A map of sticker IDs to their frecency data.
     */
    fun sortStickers(frecencyMap: Map<Long, FrecencyItem>): List<Long> {
        return sortByFrecency(frecencyMap, RECENT_STICKERS_MAX) { it }
    }

    private inline fun <K : Any, T : Any> sortByFrecency(
        frecencyMap: Map<K, FrecencyItem>,
        maxItems: Int,
        crossinline lookup: (K) -> T?
    ): List<T> {
        val now = System.currentTimeMillis()
        return frecencyMap
            .asSequence()
            .mapNotNull { (key, entry) ->
                lookup(key)?.let { item ->
                    calculateFrecencyScore(entry, now)?.let { frecency -> item to frecency }
                }
            }
            .sortedByDescending { it.second }
            .take(maxItems)
            .map { it.first }
            .toList()
    }

    private fun calculateFrecencyScore(entry: FrecencyItem, now: Long): Double? {
        if (entry.total_uses <= 0) return null

        val recentUses = entry.recent_uses

        val score = recentUses.take(MAX_SAMPLES).sumOf { timestamp ->
            val daysAgo = (now - timestamp) / (1000.0 * 60.0 * 60.0 * 24.0)
            BONUS * computeWeight(daysAgo)
        }

        return if (score > 0 && recentUses.isNotEmpty()) {
            entry.total_uses * (score / (100.0 * recentUses.size))
        } else {
            entry.total_uses.toDouble()
        }
    }

    private fun computeWeight(daysAgo: Double): Int = when {
        daysAgo <= 3.0 -> 100
        daysAgo <= 15.0 -> 70
        daysAgo <= 30.0 -> 50
        daysAgo <= 45.0 -> 30
        daysAgo <= 80.0 -> 10
        else -> 0
    }
}