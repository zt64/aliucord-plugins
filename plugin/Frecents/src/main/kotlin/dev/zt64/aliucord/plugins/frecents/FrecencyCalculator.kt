package dev.zt64.aliucord.plugins.frecents

import com.discord.api.sticker.Sticker
import com.discord.models.domain.emoji.Emoji
import discord_protos.discord_users.v1.FrecencyUserSettingsOuterClass.FrecencyUserSettings.FrecencyItem
import kotlin.math.ceil

object FrecencyCalculator {
    const val RECENT_EMOJIS_MAX = 32
    const val RECENT_STICKERS_MAX = 22

    fun sortEmojis(
        frecencyMap: Map<String, FrecencyItem>,
        emojiIdsMap: Map<String, Emoji>,
        unicodeEmojisMap: Map<String, Emoji>
    ): List<Emoji> {
        val now = System.currentTimeMillis()

        return frecencyMap
            .mapNotNull { (key, entry) ->
                calculateFrecencyScore(key, entry, now)?.let { frecency ->
                    val emoji = emojiIdsMap[key] ?: unicodeEmojisMap[key]
                    emoji?.let { it to frecency }
                }
            }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(RECENT_EMOJIS_MAX)
    }

    fun sortStickers(frecencyMap: Map<Long, FrecencyItem>, stickersMap: Map<Long, Sticker>): List<Sticker> {
        val now = System.currentTimeMillis()

        return frecencyMap
            .mapNotNull { (key, entry) ->
                calculateFrecencyScore(key, entry, now)?.let { frecency ->
                    stickersMap[key]?.let { it to frecency }
                }
            }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(RECENT_STICKERS_MAX)
    }

    fun calculateFrecencyScore(key: String, entry: FrecencyItem, now: Long): Double? {
        return calculateFrecencyScore(computeBonus(key) / 100.0, entry, now)
    }

    fun calculateFrecencyScore(key: Long, entry: FrecencyItem, now: Long): Double? {
        return calculateFrecencyScore(computeBonus(key) / 100.0, entry, now)
    }

    fun calculateFrecencyScore(bonus: Double, entry: FrecencyItem, now: Long): Double? {
        val recentUses = entry.recentUsesList.ifEmpty { return null }

        val score = recentUses.take(10).sumOf { timestamp ->
            val daysAgo = (now - timestamp) / (1000.0 * 60.0 * 60.0 * 24.0)
            bonus * computeWeight(daysAgo)
        }

        if (score <= 0) return null

        return ceil(entry.totalUses * (score / recentUses.size))
    }

    // Might be used later on, I'm not sure
    @Suppress("unused")
    private fun computeBonus(key: Long) = 100

    // Might be used later on, I'm not sure
    @Suppress("unused")
    private fun computeBonus(key: String) = 100

    private fun computeWeight(daysAgo: Double): Int = when {
        daysAgo <= 3.0 -> 100
        daysAgo <= 15.0 -> 70
        daysAgo <= 30.0 -> 50
        daysAgo <= 45.0 -> 30
        daysAgo <= 80.0 -> 10
        else -> 0
    }
}