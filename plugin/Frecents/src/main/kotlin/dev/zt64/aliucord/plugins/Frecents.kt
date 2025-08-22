@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package dev.zt64.aliucord.plugins

import android.content.Context
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.GatewayAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.utils.RxUtils.map
import com.aliucord.utils.RxUtils.switchMap
import com.aliucord.utils.lazyField
import com.aliucord.wrappers.embeds.MessageEmbedWrapper.Companion.rawVideo
import com.aliucord.wrappers.embeds.MessageEmbedWrapper.Companion.url
import com.aliucord.wrappers.embeds.VideoWrapper.Companion.url
import com.aliucord.wrappers.messages.AttachmentWrapper.Companion.type
import com.aliucord.wrappers.messages.AttachmentWrapper.Companion.url
import com.discord.api.message.attachment.MessageAttachment
import com.discord.api.message.embed.EmbedType
import com.discord.api.message.embed.MessageEmbed
import com.discord.databinding.ExpressionPickerHeaderItemBinding
import com.discord.models.domain.emoji.Emoji
import com.discord.models.gifpicker.dto.ModelGif
import com.discord.stores.*
import com.discord.stores.StoreMediaFavorites.Favorite
import com.discord.utilities.embed.EmbedResourceUtils
import com.discord.widgets.chat.input.gifpicker.*
import com.discord.widgets.chat.input.sticker.*
import com.discord.widgets.chat.list.InlineMediaView
import com.discord.widgets.emoji.EmojiSheetViewModel
import com.discord.widgets.emoji.EmojiSheetViewModel.ViewState
import com.lytefast.flexinput.R
import dev.zt64.aliucord.plugins.frecents.*
import dev.zt64.aliucord.plugins.frecents.gif.GifCategoryItemFavorites
import dev.zt64.aliucord.plugins.frecents.gif.GifUtil
import discord_protos.discord_users.v1.FrecencyUserSettingsKt
import discord_protos.discord_users.v1.FrecencyUserSettingsOuterClass.FrecencyUserSettings
import discord_protos.discord_users.v1.copy
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.regex.Pattern
import j0.l.e.k as ScalarSynchronousObservable

data class GatewayResponse(val settings: Settings, val partial: Boolean) {
    data class Settings(val proto: String, val type: Int)
}

@AliucordPlugin(requiresRestart = true)
class Frecents : Plugin() {
    private val bindingField by lazyField<OwnedHeaderViewHolder>("binding")
    private val OwnedHeaderViewHolder.binding
        get() = bindingField[this] as ExpressionPickerHeaderItemBinding

    private var frecencySettings = FrecencySettingsManager()

    private fun toggleFavoriteGif(model: ModelGif) {
        val tenorGifUrl = URLDecoder.decode(model.tenorGifUrl, Charset.defaultCharset().name())
        val isFavorited = frecencySettings.settings.favoriteGifs.gifsMap.containsKey(tenorGifUrl)
        frecencySettings.updateSettings {
            it.copy {
                favoriteGifs = favoriteGifs.copy {
                    val gifs = gifs
                    if (isFavorited) {
                        gifs.remove(tenorGifUrl)
                    } else {
                        gifs[tenorGifUrl] = FrecencyUserSettingsKt.favoriteGIF {
                            format = FrecencyUserSettings.GIFType.GIFTYPE_IMAGE
                            this.width = model.width
                            this.height = model.height
                            src = model.gifImageUrl
                            order = if (gifs.isEmpty()) 1 else gifs.values.maxOf { it.order } + 1
                        }
                    }
                }
            }
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

    override fun start(ctx: Context) {
        GatewayAPI.onEvent<GatewayResponse>("USER_SETTINGS_PROTO_UPDATE", frecencySettings::handleGatewayUpdate)

        // Patch to make favorite emotes use the frecency user settings
        patcher.instead<StoreMediaFavorites>("observeFavorites", Set::class.java) {
            val pattern = Pattern.compile("\\d+")
            val emojiStore = StoreStream.getEmojis()
            frecencySettings.observeSettings().switchMap { frecents ->
                ScalarSynchronousObservable(
                    frecents.favoriteEmojis.emojisList.map {
                        if (pattern.matcher(it).matches()) {
                            Favorite.FavCustomEmoji(it)
                        } else {
                            @Suppress("USELESS_CAST") // IDE doesn't like without this cast
                            Favorite.FavUnicodeEmoji(emojiStore.unicodeEmojisNamesMap[it]!!.uniqueId) as Favorite
                        }
                    }.toSet()
                )
            }
        }

        // Patch to make clicking an emoji update the frecency user settings
        // patcher.instead<StoreEmoji>("onEmojiUsed", String::class.java) { (param, emojiKey: String) ->
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
            val settings = frecencySettings.settings

            FrecencyCalculator.sortEmojis(
                frecencyMap = settings.emojiFrecency.emojisMap,
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
                it.copy {
                    favoriteEmojis = favoriteEmojis.copy {
                        if (favorite) {
                            emojis.add(data)
                        } else {
                            val newList = emojis.filter { it != data }
                            emojis.clear()
                            emojis.addAll(newList)
                        }
                    }
                }
            }
            frecencySettings.patchSettingsAsync(
                onError = { _, e ->
                    Utils.showToast("Failed to update favorite emoji: ${e.message}")
                }
            )
        }

        // patcher.before<StickerPickerViewModel.ViewState.Stickers>(
        //     String::class.java,
        //     List::class.java,
        //     List::class.java,
        //     Boolean::class.java,
        //     Boolean::class.java
        // ) { (param, _: Any, a: List<MGRecyclerDataPayload>) ->
        //     param.args[1] = listOf(HeaderItem(FavoritesItem)) + a
        // }

        // Patch to use the frequently used stickers from the frecency user settings
        patcher.instead<StoreStickers>("observeFrequentlyUsedStickerIds") {
            frecencySettings.observeSettings().map { settings ->
                val now = System.currentTimeMillis()

                settings.stickerFrecency.stickersMap
                    .mapNotNull { (key, entry) ->
                        FrecencyCalculator.calculateFrecencyScore(key, entry, now)?.let { frecency ->
                            key to frecency
                        }
                    }
                    .sortedByDescending { it.second }
                    .map { it.first }
                    .take(FrecencyCalculator.RECENT_STICKERS_MAX)
                    .toList()
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
        // patcher.after<StickerViewHolder>("configureSticker", MGRecyclerDataPayload::class.java) { (_, payload: MGRecyclerDataPayload) ->
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

        // Add favorites section to the sticker categories list
        // patcher.after<OwnedHeaderViewHolder>(
        //     "onConfigure",
        //     Int::class.java,
        //     MGRecyclerDataPayload::class.java
        // ) { (_, _: Int, data: MGRecyclerDataPayload) ->
        //     if (data !is HeaderItem) return@after
        //
        //     if (data.headerType == FavoritesItem) binding.b.text = "Favorites (coming soon)"
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

        // Long click gif in search results to favorite/unfavorite
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

        // Add favorites section to the gif categories list
        patcher.after<GifCategoryViewHolder>(
            "configure",
            GifCategoryItem::class.java,
            Function1::class.java
        ) { (_, gifCategoryItem: GifCategoryItem?) ->
            if (gifCategoryItem !is GifCategoryItemFavorites) return@after

            if (frecencySettings.settings.favoriteGifs.gifsCount > 0) {
                setPreviewImage(GifUtil.mqGifUrl(frecencySettings.settings.favoriteGifs.gifsMap.values.random().src))
            } else {
                // Clear it out, so it doesn't show the last preview
                setPreviewImage("")
            }

            itemView.findViewById<ImageView>(Resources.gif_category_item_icon).apply {
                visibility = View.VISIBLE
                setImageDrawable(ContextCompat.getDrawable(ctx, R.e.ic_emoji_picker_category_favorites_star)!!)
            }

            itemView.findViewById<TextView>(Resources.gif_category_item_title).text = "Favorites"
        }

        // Patch to set the title of the gif category
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
                    it.favoriteGifs.gifsMap
                        .asSequence()
                        .sortedByDescending { it.value.order }
                        .map { (tenorUrl, v) ->
                            ModelGif(GifUtil.mqGifUrl(v.src), tenorUrl, v.width, v.height)
                        }
                        .toList()
                }
                else -> throw NoWhenBranchMatchedException()
            }.map(GifCategoryViewModel::StoreState)
        }

        // Experiment using ExoPlayer for mp4 gifs
        // val bindingField = GifViewHolder.Gif::class.java
        //     .getDeclaredField("binding")
        //     .apply { isAccessible = true }

        // val m = b.a.p.i.a(ctx)
        // lateinit var playerView: PlayerView
        //
        // patcher.after<GifViewHolder.Gif>(GifItemViewBinding::class.java) { (_, binding: GifItemViewBinding) ->
        //     playerView = PlayerView(binding.root.context, null).apply {
        //         layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        //         visibility = View.GONE
        //         resizeMode = 1
        //         useController = false
        //     }
        //
        //     (binding.root as ViewGroup).addView(playerView)
        // }
        //
        // patcher.instead<GifViewHolder.Gif>("setGifImage", ModelGif::class.java) { (_, gif: ModelGif) ->
        //     val binding = bindingField[this] as GifItemViewBinding
        //     val url = gif.gifImageUrl
        //
        //     if (url.endsWith("mp4")) {
        //         playerView.visibility = View.VISIBLE
        //         binding.a.visibility = View.GONE
        //         val mediaSource = b.c.a.a0.d.P(MediaType.VIDEO, url, "javaClass")
        //         m.a(mediaSource, true, true, 0, playerView, null)
        //     } else {
        //         playerView.visibility = View.GONE
        //         binding.a.visibility = View.VISIBLE
        //         MGImages.`setImage$default`(binding.b, listOf(url), 0, 0, false, null, null, null, 252, null)
        //     }
        // }

        // NOTE: These views are recycled, so it's important to perform condition checks inside the listeners so data is up to date
        // Patch to favorite/unfavorite embedded gifs from a URL on long click
        patcher.after<InlineMediaView>(
            "updateUIWithEmbed",
            MessageEmbed::class.java,
            Int::class.javaObjectType,
            Int::class.javaObjectType,
            Boolean::class.javaPrimitiveType!!
        ) { (_, messageEmbed: MessageEmbed, width: Int, height: Int) ->
            binding.a.setOnLongClickListener {
                if (!EmbedResourceUtils.INSTANCE.isAnimated(messageEmbed)) return@setOnLongClickListener false

                val gifUrl = messageEmbed.rawVideo?.url
                    ?.replace("AAAPo", "AAAAC") ?: messageEmbed.url
                val model = ModelGif(gifUrl, messageEmbed.url, width, height)

                toggleFavoriteGif(model)

                true
            }
        }

        // Favorite/unfavorite gif file attachment on long click
        patcher.after<InlineMediaView>(
            "updateUIWithAttachment",
            MessageAttachment::class.java,
            Int::class.javaObjectType,
            Int::class.javaObjectType,
            Boolean::class.javaPrimitiveType!!
        ) { (_, messageAttachment: MessageAttachment, width: Int?, height: Int?) ->
            binding.c.setOnLongClickListener {
                if (width == null || height == null) return@setOnLongClickListener false

                val embedType = when (messageAttachment.type.ordinal) {
                    0 -> EmbedType.VIDEO
                    1 -> EmbedType.IMAGE
                    2 -> EmbedType.FILE
                    else -> throw NoWhenBranchMatchedException()
                }

                val url = messageAttachment.url

                if (!EmbedResourceUtils.INSTANCE.isAnimated(embedType, url)) return@setOnLongClickListener false

                val model = ModelGif(url, url, width, height)

                toggleFavoriteGif(model)
                true
            }
        }

        // Patch to add a favorite button to the URL actions widget
        // Can't implement unless I find a way to get the width and height of the gif
        // val getBinding = WidgetUrlActions::class.java
        //     .getDeclaredMethod("getBinding").apply { isAccessible = true }
        //
        // patcher.after<WidgetUrlActions>("onViewCreated", View::class.java, Bundle::class.java) {
        //     val binding = getBinding(this) as WidgetUrlActionsBinding
        //     val url = WidgetUrlActions.`access$getUrl$p`(this)
        //
        //     binding.a.addView(
        //         TextView(binding.root.context, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
        //             var isFavorited = frecencyUserSettings.favoriteGifs.gifsMap.containsKey(url)
        //             val icon = ContextCompat
        //                 .getDrawable(
        //                     context,
        //                     R.e.ic_guild_invite_24dp
        //                 )!!
        //                 .mutate()
        //                 .apply {
        //                     setTint(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal))
        //                 }
        //
        //             text = if (isFavorited) {
        //                 "Unfavorite GIF"
        //             } else {
        //                 "Favorite GIF"
        //             }
        //
        //             setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
        //             setOnClickListener {
        //                 toggleFavorite(
        //                     ModelGif(
        //                         mqGifUrl(url),
        //                         url,
        //                         width,
        //                         height
        //                     )
        //                 )
        //             }
        //         }
        //     )
        // }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}