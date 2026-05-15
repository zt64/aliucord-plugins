package dev.zt64.aliucord.plugins.frecents

import android.content.Context
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.GatewayAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.before
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.aliucord.patcher.component3
import com.aliucord.patcher.instead
import com.aliucord.utils.RxUtils.map
import com.aliucord.utils.RxUtils.switchMap
import com.discord.models.domain.emoji.Emoji
import com.discord.models.gifpicker.dto.ModelGif
import com.discord.player.MediaSource
import com.discord.player.MediaType
import com.discord.stores.StoreEmoji
import com.discord.stores.StoreGifPicker
import com.discord.stores.StoreMediaFavorites
import com.discord.stores.StoreMediaFavorites.Favorite
import com.discord.stores.StoreStickers
import com.discord.stores.StoreStream
import com.discord.widgets.chat.input.gifpicker.GifAdapterItem
import com.discord.widgets.chat.input.gifpicker.GifCategoryItem
import com.discord.widgets.chat.input.gifpicker.GifCategoryViewHolder
import com.discord.widgets.chat.input.gifpicker.GifCategoryViewModel
import com.discord.widgets.chat.input.gifpicker.GifPickerViewModel
import com.discord.widgets.chat.input.gifpicker.GifViewHolder
import com.discord.widgets.chat.input.gifpicker.WidgetGifCategory
import com.discord.widgets.chat.input.gifpicker.WidgetGifPicker
import com.discord.widgets.emoji.EmojiSheetViewModel
import com.discord.widgets.emoji.EmojiSheetViewModel.ViewState
import com.discord.widgets.media.WidgetMedia
import com.lytefast.flexinput.R
import dev.zt64.aliucord.plugins.frecents.gif.GifCategoryItemFavorites
import dev.zt64.aliucord.plugins.frecents.gif.GifUtil
import discord_protos.discord_users.v1.FrecencyUserSettings
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.regex.Pattern
import j0.l.e.k as ScalarSynchronousObservable

data class GatewayResponse(val settings: Settings, val partial: Boolean) {
    data class Settings(val proto: String, val type: Int)
}

@AliucordPlugin(requiresRestart = true)
class Frecents : Plugin() {
    // private val bindingField by lazyField<OwnedHeaderViewHolder>("binding")
    // private val OwnedHeaderViewHolder.binding
    //     get() = bindingField[this] as ExpressionPickerHeaderItemBinding

    private val frecencySettings = FrecencySettingsManager()

    private companion object {
        private val STAR_ITEM_ID = View.generateViewId()
    }

    init {
        settingsTab = SettingsTab(FrecentsSettings::class.java).withArgs(frecencySettings)
    }

    private fun toggleFavoriteGif(model: ModelGif) {
        val tenorGifUrl = URLDecoder.decode(model.tenorGifUrl, Charset.defaultCharset().name())
        val gifs = frecencySettings.settings.favorite_gifs?.gifs.orEmpty().toMutableMap()
        val isFavorited = tenorGifUrl in gifs

        frecencySettings.updateSettings {
            if (isFavorited) {
                gifs.remove(tenorGifUrl)
            } else {
                gifs[tenorGifUrl] = FrecencyUserSettings.FavoriteGIF(
                    format = if (model.gifImageUrl.endsWith(".mp4")) {
                        FrecencyUserSettings.GIFType.GIF_TYPE_VIDEO
                    } else {
                        FrecencyUserSettings.GIFType.GIF_TYPE_IMAGE
                    },
                    src = model.gifImageUrl,
                    width = model.width,
                    height = model.height,
                    order = if (gifs.isEmpty()) 1 else gifs.values.maxOf { it.order } + 1
                )
            }

            logger.debug("Updated gif: $model")

            copy(favorite_gifs = favorite_gifs?.copy(gifs = gifs))
        }

        frecencySettings.patchSettingsAsync(
            onSuccess = {
                if (isFavorited) {
                    Utils.showToast("Removed GIF from favorites")
                } else {
                    Utils.showToast("Added GIF to favorites")
                }
            },
            onError = { _, e ->
                if (isFavorited) {
                    Utils.showToast("Failed to remove GIF from favorites: ${e.message}")
                } else {
                    Utils.showToast("Failed to add GIF to favorites: ${e.message}")
                }
            }
        )
    }

    override fun start(context: Context) {
        GatewayAPI.onEvent<GatewayResponse>("USER_SETTINGS_PROTO_UPDATE", frecencySettings::handleGatewayUpdate)

        // Patch to make favorite emotes use the frecency user settings
        patcher.instead<StoreMediaFavorites>("observeFavorites", Set::class.java) {
            val pattern = Pattern.compile("\\d+")
            val emojiStore = StoreStream.getEmojis()

            frecencySettings.observeSettings().switchMap { frecents ->
                ScalarSynchronousObservable(
                    frecents.favorite_emojis?.emojis.orEmpty().mapNotNull {
                        if (pattern.matcher(it).matches()) {
                            Favorite.FavCustomEmoji(it)
                        } else {
                            emojiStore.unicodeEmojisNamesMap[it]?.let { model ->
                                Favorite.FavUnicodeEmoji(model.uniqueId)
                            }
                        }
                    }.toSet()
                )
            }
        }

        // Patch to make clicking an emoji update the frecency user settings
        // TODO: Figure out how to properly update the score
        // patcher.instead<StoreEmoji>("onEmojiUsed", String::class.java2) { (param, emojiKey: String) ->
        //     frecencySettings.updateSettings {
        //         it.copy {
        //             val frecency = emojiFrecency.getEmojisOrDefault(
        //                 emojiKey,
        //                 FrecencyUserSettingsKt.frecencyItem { }
        //             ).copy {
        //                 val now = System.currentTimeMillis()
        //                 totalUses++
        //                 recentUses += now
        //                 frecency = EmojiFrequencyCalculator.calculateFrecencyScore(emojiKey, recentUses, now) ?: 0
        //                 score
        //             }
        //
        //             logger.info(
        //                 "Emoji used: $emojiKey, frecency: ${frecency.frecency}, total uses: ${frecency.totalUses}"
        //             )
        //
        //             emojiFrecency = emojiFrecency.copy {
        //                 emojis[emojiKey] = frecency
        //             }
        //         }
        //     }
        //
        //     frecencySettings.patchSettingsAsync()
        // }

        // Replace the frequently used emojis with the ones from the frecency user settings
        patcher.instead<StoreEmoji>("getFrequentlyUsedEmojis", Map::class.java) { param ->
            @Suppress("UNCHECKED_CAST")
            val emojiIdsMap = param.args[0] as Map<String, Emoji>

            FrecencyCalculator.sortEmojis(
                frecencyMap = frecencySettings.settings.emoji_frecency?.emojis.orEmpty(),
                emojiIdsMap = emojiIdsMap,
                unicodeEmojisMap = unicodeEmojisNamesMap
            )
        }

        // Patch to make the emoji sheet view model use the frecency user settings when favorites are set
        patcher.after<EmojiSheetViewModel>(
            "setFavorite",
            Boolean::class.java
        ) { (_, favorite: Boolean) ->
            val data = when (val state = viewState) {
                is ViewState.EmojiCustom -> state.emojiCustom.id.toString()
                is ViewState.EmojiUnicode -> state.emojiUnicode.firstName
                else -> return@after
            }

            frecencySettings.updateSettings {
                copy(
                    favorite_emojis = favorite_emojis?.copy(
                        emojis = if (favorite) {
                            favorite_emojis.emojis + data
                        } else {
                            favorite_emojis.emojis.filter { it != data }
                        }
                    )
                )
            }

            frecencySettings.patchSettingsAsync(
                onError = { _, e ->
                    Utils.showToast("Failed to update favorite emoji: ${e.message}")
                }
            )
        }

        // // Add favorites section to the sticker categories list
        // patcher.before<StickerPickerViewModel.ViewState.Stickers>(
        //     String::class.java,
        //     List::class.java,
        //     List::class.java,
        //     Boolean::class.java,
        //     Boolean::class.java
        // ) { (param, _: Any, a: List<MGRecyclerDataPayload>) ->
        //     param.args[1] = listOf(HeaderItem(FavoritesItem)) + a
        // }

        // // Ensure the favorites header shows the correct title
        // patcher.after<OwnedHeaderViewHolder>(
        //     "onConfigure",
        //     Int::class.java,
        //     MGRecyclerDataPayload::class.java
        // ) { (_, _: Int, data: MGRecyclerDataPayload) ->
        //     if (data !is HeaderItem) return@after
        //
        //     if (data.headerType == FavoritesItem) binding.b.text = "Favorites"
        // }

        // Patch to use the frequently used stickers from the frecency user settings
        patcher.instead<StoreStickers>("observeFrequentlyUsedStickerIds") {
            frecencySettings.observeSettings().map { settings ->
                FrecencyCalculator.sortStickers(settings.sticker_frecency?.stickers.orEmpty())
            }
        }

        // Patch to make clicking a sticker send an API request
        // patcher.instead<`StoreStickers$onStickerUsed$1`>("invoke") {
        //     val sticker = `$sticker`
        //
        //     frecencySettings.updateSettings {
        //         it.copy {
        //             val stickerFrecency = stickerFrecency.getStickersOrDefault(
        //                 sticker.id,
        //                 FrecencyUserSettingsKt.frecencyItem { }
        //             ).copy {
        //                 val now = System.currentTimeMillis()
        //                 totalUses++
        //                 recentUses += now
        //             }
        //             logger.info(
        //                 "Sticker used: ${sticker.id}, frecency: ${stickerFrecency.frecency}, total uses: ${stickerFrecency.totalUses}"
        //             )
        //         }
        //     }
        //
        //     null
        // }

        // Patch to make long clicking a sticker add it to favorites
        // patcher.after<StickerViewHolder>("configureSticker", MGRecyclerDataPayload::class.java2) { (_, payload: MGRecyclerDataPayload) ->
        //     StickerViewHolder.`access$getBinding$p`(this).a.setOnLongClickListener {
        //         val stickerItem = payload as StickerItem
        //         val isFavorited = stickerItem.sticker.id in frecencySettings.settings.favoriteStickers.stickerIdsList
        //
        //         frecencySettings.updateSettings {
        //             it.copy {
        //                 if (isFavorited) {
        //                     favoriteStickers = favoriteStickers
        //                         .toBuilder().apply {
        //                             val tmp = favoriteStickers.stickerIdsList.apply {
        //                                 remove(stickerItem.sticker.id)
        //                             }
        //                             clearStickerIds()
        //                             addAllStickerIds(tmp)
        //                         }.build()
        //                 } else {
        //                     favoriteStickers = favoriteStickers
        //                         .toBuilder()
        //                         .addStickerIds(stickerItem.sticker.id)
        //                         .buildPartial()
        //                 }
        //             }
        //         }
        //
        //         frecencySettings.patchSettingsAsync(
        //             onSuccess = {
        //                 if (isFavorited) {
        //                     Utils.showToast("Removed sticker from favorites")
        //                 } else {
        //                     Utils.showToast("Added sticker to favorites")
        //                 }
        //             },
        //             onError = { _, e -> Utils.showToast("Failed to add sticker to favorites: ${e.message}") }
        //         )
        //
        //         true
        //     }
        // }

        // Add favorites section to the gif categories list
        patcher.before<WidgetGifPicker>(
            "handleViewState",
            GifPickerViewModel.ViewState::class.java
        ) { (param, viewState: GifPickerViewModel.ViewState) ->
            param.args[0] = GifPickerViewModel.ViewState(
                listOf(GifCategoryItemFavorites) + viewState.gifCategoryItems
            )
        }

        // Long click GIF in search results to favorite/unfavorite
        patcher.after<GifViewHolder.Gif>(
            "configure",
            GifAdapterItem.GifItem::class.java,
            Int::class.java,
            Function1::class.java
        ) { (_, item: GifAdapterItem.GifItem) ->
            itemView.setOnLongClickListener {
                toggleFavoriteGif(item.gif)
                true
            }
        }

        // Add favorites section to the GIF categories list
        patcher.after<GifCategoryViewHolder>(
            "configure",
            GifCategoryItem::class.java,
            Function1::class.java
        ) { (_, gifCategoryItem: GifCategoryItem?) ->
            if (gifCategoryItem !is GifCategoryItemFavorites) return@after

            val favoriteGifs = frecencySettings.settings.favorite_gifs?.gifs.orEmpty()
            if (favoriteGifs.isNotEmpty()) {
                setPreviewImage(GifUtil.mqGifUrl(favoriteGifs.values.random().src))
            } else {
                // Clear it out, so it doesn't show the last preview
                setPreviewImage("")
            }

            itemView.findViewById<ImageView>(Resources.gif_category_item_icon).apply {
                visibility = View.VISIBLE
                setImageDrawable(ContextCompat.getDrawable(context, R.e.ic_emoji_picker_category_favorites_star)!!)
            }

            itemView.findViewById<TextView>(Resources.gif_category_item_title).text = "Favorites"
        }

        // Patch to set the title of the GIF category
        patcher.instead<WidgetGifCategory>("setUpTitle") {
            val binding = WidgetGifCategory.`access$getBinding$p`(this)
            val gifCategoryItem = WidgetGifCategory.`access$getGifCategory`(this)

            binding.e.text = when (gifCategoryItem) {
                is GifCategoryItem.Standard -> gifCategoryItem.gifCategory.categoryName
                is GifCategoryItem.Trending -> resources.getString(Resources.gif_picker_result_type_trending_gifs)
                is GifCategoryItemFavorites -> "Favorites"
                else -> error("Unknown gif category item. This should never happen.")
            }

            null
        }

        // Patch to use the frecency user settings for the favorite gifs
        patcher.instead<GifCategoryViewModel.Companion>(
            "observeStoreState",
            GifCategoryItem::class.java,
            StoreGifPicker::class.java
        ) { (_, item: GifCategoryItem, store: StoreGifPicker) ->
            when (item) {
                is GifCategoryItem.Standard -> store.observeGifsForSearchQuery(item.gifCategory.categoryName)
                is GifCategoryItem.Trending -> store.observeTrendingCategoryGifs()
                is GifCategoryItemFavorites -> frecencySettings.observeSettings().map {
                    it.favorite_gifs?.gifs
                        .orEmpty()
                        .asSequence()
                        .sortedByDescending { (_, gif) -> gif.order }
                        .map { (tenorUrl, v) ->
                            ModelGif(GifUtil.mqGifUrl(v.src), tenorUrl, v.width, v.height)
                        }
                        .toList()
                }
                else -> throw NoWhenBranchMatchedException()
            }.map(GifCategoryViewModel::StoreState)
        }

        // Add star button to media viewer for GIFs
        patcher.after<WidgetMedia>("onViewBoundOrOnResume") {
            val mediaSource = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mostRecentIntent.getParcelableExtra("INTENT_MEDIA_SOURCE", MediaSource::class.java)
            } else {
                @Suppress("DEPRECATION")
                mostRecentIntent.getParcelableExtra("INTENT_MEDIA_SOURCE")
            }

            val mediaUrl = mostRecentIntent.getStringExtra("INTENT_MEDIA_URL")!!.substringBefore('?')
                .replace("media.discordapp.net", "cdn.discordapp.com")
            // media source is only present on links to GIFs, so it can be used for checking if the media is a GIF
            // otherwise fallback to checking if the URL ends with .gif, which is likely a file attachment
            val url = mediaSource?.takeIf { it.l == MediaType.GIFV }?.j?.toString()
                ?: mostRecentIntent
                    .getStringExtra("INTENT_IMAGE_URL")
                    ?.takeIf {
                        it
                            .substringBefore('?')
                            .endsWith(".gif", ignoreCase = true)
                    }
                ?: return@after

            val width = mostRecentIntent.getIntExtra("INTENT_MEDIA_WIDTH", 0)
            val height = mostRecentIntent.getIntExtra("INTENT_MEDIA_HEIGHT", 0)
            var starred = mediaUrl in frecencySettings.settings.favorite_gifs?.gifs.orEmpty()

            val starredDrawable = ContextCompat.getDrawable(context, R.e.ic_emoji_picker_category_favorites_star)!!.mutate()

            fun starTitle() = if (starred) "Unfavorite" else "Favorite"
            fun updateStarTint() {
                if (starred) {
                    starredDrawable.setTint(ContextCompat.getColor(context, R.c.status_yellow))
                } else {
                    starredDrawable.setTintList(null)
                }
            }

            updateStarTint()

            val menuItem = with(WidgetMedia.`access$getBinding$p`(this).root.findViewById<Toolbar>(R.f.action_bar_toolbar).menu) {
                removeItem(STAR_ITEM_ID)
                add(Menu.NONE, STAR_ITEM_ID, Menu.NONE, starTitle())
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    .setIcon(starredDrawable)
            }

            menuItem.setOnMenuItemClickListener {
                toggleFavoriteGif(ModelGif(url, mediaUrl, width, height))
                starred = !starred
                menuItem.title = starTitle()
                updateStarTint()
                true
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}