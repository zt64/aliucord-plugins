@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

import FrecencyUserSettingsOuterClass.FrecencyUserSettings
import android.content.Context
import android.util.Base64
import android.widget.*
import androidx.core.content.ContextCompat
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.GatewayAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.utils.RxUtils.map
import com.aliucord.utils.RxUtils.switchMap
import com.aliucord.utils.lazyField
import com.discord.databinding.ExpressionPickerHeaderItemBinding
import com.discord.models.domain.emoji.Emoji
import com.discord.models.gifpicker.dto.ModelGif
import com.discord.stores.*
import com.discord.stores.StoreMediaFavorites.Favorite
import com.discord.stores.StoreMediaFavorites.FavoriteEmoji
import com.discord.utilities.mg_recycler.MGRecyclerDataPayload
import com.discord.widgets.chat.input.gifpicker.*
import com.discord.widgets.chat.input.sticker.*
import com.discord.widgets.emoji.EmojiSheetViewModel
import com.discord.widgets.emoji.EmojiSheetViewModel.ViewState
import com.lytefast.flexinput.R
import dev.zt64.aliucord.plugins.frecents.*
import dev.zt64.aliucord.plugins.frecents.gif.GifCategoryItemFavorites
import dev.zt64.aliucord.plugins.frecents.sticker.FavoritesItem
import rx.Observable
import rx.subjects.BehaviorSubject
import java.util.regex.Pattern
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import j0.l.e.k as ScalarSynchronousObservable

private const val ROUTE = "/users/@me/settings-proto/2"

private const val PATCH_FRECENT_EMOJIS = false
private const val PATCH_FRECENT_STICKERS = false

private data class Response(val settings: String)

private data class GatewayResponse(val settings: Settings, val partial: Boolean) {
    data class Settings(val proto: String, val type: Int)
}

private data class Patch(val settings: String)

@AliucordPlugin
class Frecents : Plugin() {
    private val bindingField by lazyField<OwnedHeaderViewHolder>("binding")
    private val OwnedHeaderViewHolder.binding
        get() = bindingField[this] as ExpressionPickerHeaderItemBinding

    private val frecencyUserSettingsSubject = BehaviorSubject.k0<FrecencyUserSettings>()

    private var frecencyUserSettings: FrecencyUserSettings by LazyMutable {
        Utils.threadPool
            .submit<FrecencyUserSettings> {
                val res = get()

                FrecencyUserSettings.parseFrom(Base64.decode(res.settings, Base64.DEFAULT))
            }.get()
            .also {
                frecencyUserSettingsSubject.onNext(it)
            }
    }

    private fun getFrecencyUserSettingsObservable(): Observable<FrecencyUserSettings> {
        return frecencyUserSettingsSubject
        // return BehaviorSubject.k0<FrecencyUserSettings>().also { subject ->
        //     frecencyUserSettings
        //     Utils.threadPool.submit {
        //         val res = get()
        //         val initialSettings = FrecencyUserSettings.parseFrom(Base64.decode(res.settings, Base64.DEFAULT))
        //         subject.onNext(initialSettings)
        //     }
        // }
    }

    private fun get() = Http.Request
        .newDiscordRNRequest(ROUTE, "GET")
        .execute()
        .json(Response::class.java)

    private fun patchSettings() {
        val encoded = Base64.encodeToString(frecencyUserSettings.toByteArray(), Base64.DEFAULT)

        Utils.threadPool.execute {
            val res = Http.Request
                .newDiscordRNRequest(ROUTE, "PATCH")
                .executeWithJson(Patch(encoded))

            res.assertOk()
        }
    }

    override fun start(ctx: Context) {
        val starDrawable = ContextCompat.getDrawable(ctx, R.e.ic_emoji_picker_category_favorites_star)!!

        GatewayAPI.onEvent<GatewayResponse>("USER_SETTINGS_PROTO_UPDATE") {
            val new = FrecencyUserSettings.parseFrom(Base64.decode(it.settings.proto, Base64.DEFAULT))

            frecencyUserSettings = if (it.partial) frecencyUserSettings.toBuilder().mergeFrom(new).build() else new
        }

        if (PATCH_FRECENT_EMOJIS) {
            // Patch to make favorite emotes use the frecency user settings
            patcher.instead<StoreMediaFavorites>("observeFavorites", Set::class.java) {
                getFrecencyUserSettingsObservable().switchMap { frecents ->
                    val pattern = Pattern.compile("\\d+")
                    ScalarSynchronousObservable(
                        frecents.favoriteEmojis.emojisList
                            .map {
                                if (pattern.matcher(it).matches()) {
                                    Favorite.FavCustomEmoji(it)
                                } else {
                                    @Suppress("USELESS_CAST") // IDE doesn't like without this cast
                                    Favorite.FavUnicodeEmoji(it) as Favorite
                                }
                            }.toSet()
                    )
                }
            }

            // Patch to make adding a favorite emoji send an API request
            patcher.instead<`StoreMediaFavorites$addFavorite$1`>("invoke") {
                val favorite = `$favorite` as FavoriteEmoji
                frecencyUserSettings.copy {
                    favoriteEmojis.emojisList.add(favorite.emojiUniqueId)
                }

                `this$0`.markChanged()
                null
            }

            // Patch to make removing a favorite emoji send an API request
            patcher.instead<`StoreMediaFavorites$removeFavorite$1`>("invoke") {
                frecencyUserSettings.copy {
                    favoriteEmojis.emojisList.remove((`$favorite` as FavoriteEmoji).emojiUniqueId)
                }

                `this$0`.markChanged()
                null
            }

            // Patch to make clicking an emoji update the frecency user settings
            patcher.instead<StoreEmoji>("onEmojiUsed", String::class.java) {
                frecencyUserSettings.emojiFrecency.copy { }
            }

            patcher.instead<StoreEmoji>("getFrequentlyUsedEmojis", Map::class.java) { param ->
                val emojiIdsMap = param.args[0] as Map<String, Emoji>

                val now = System.currentTimeMillis()
                val history = mutableMapOf<String, Emoji>()

                frecencyUserSettings.emojiFrecency.emojisMap.forEach { t, u ->
                    u.recentUsesList.forEach { }
                }

                frecencyUserSettings.emojiFrecency.emojisMap
                    .asSequence()
                    .filter { it.value.score > 0 }
                    .sortedByDescending {
                        var frec = it.value.frecency

                        frec
                    }
                    .take(32)
                    .mapNotNull {
                        // key is the emoji id/name
                        emojiIdsMap.getOrElse(it.key) {
                            unicodeEmojisNamesMap[it.key]
                        }
                    }.toList()
            }

            patcher.after<EmojiSheetViewModel>(
                "setFavorite",
                Boolean::class.java
            ) { (param, favorite: Boolean) ->
                val data = when (val state = viewState) {
                    is ViewState.EmojiCustom -> state.emojiCustom.id.toString()
                    is ViewState.EmojiUnicode -> state.emojiUnicode.firstName
                    else -> return@after
                }

                val builder = frecencyUserSettings.favoriteEmojis.toBuilder()
                if (favorite) {
                    builder.addEmojis(data)
                } else {
                    builder.emojisList.remove(data)
                }
                builder.build()
            }
        }

        if (PATCH_FRECENT_STICKERS) {
            patcher.before<StickerPickerViewModel.ViewState.Stickers>(
                String::class.java,
                List::class.java,
                List::class.java,
                Boolean::class.java,
                Boolean::class.java
            ) { (param, _: Any, a: List<MGRecyclerDataPayload>) ->
                val storeState = (StoreStream.Companion).stickers

                param.args[1] = listOf(HeaderItem(FavoritesItem)) + a
            }

            // Patch to use the frequently used stickers from the frecency user settings
            patcher.instead<StoreStickers>("observeFrequentlyUsedStickerIds") {
                getFrecencyUserSettingsObservable().switchMap {
                    ScalarSynchronousObservable(
                        it.stickerFrecency.stickersMap
                            .asSequence()
                            .sortedByDescending { (k, v) ->
                                v.frecency
                            }.map {
                                it.key
                            }.toList()
                    )
                }
            }

            // Patch to make clicking a sticker send an API request
            patcher.instead<`StoreStickers$onStickerUsed$1`>("invoke") {
                val sticker = `$sticker`

                frecencyUserSettings.stickerFrecency
                    .toBuilder()
                    .buildPartial()

                null
            }

            patcher.after<StickerViewHolder>("configureSticker", MGRecyclerDataPayload::class.java) { (_, payload: MGRecyclerDataPayload) ->
                StickerViewHolder.`access$getBinding$p`(this).b.setOnLongClickListener {
                    // toggleFavorite(item.model)
                    val stickerItem = payload as StickerItem

                    val favorite = frecencyUserSettings.favoriteStickers
                        .toBuilder()
                        .addStickerIds(stickerItem.sticker.id)
                        .buildPartial()

                    Utils.showToast("Added to favorites")

                    true
                }
            }

            patcher.after<OwnedHeaderViewHolder>(
                "onConfigure",
                Int::class.java,
                MGRecyclerDataPayload::class.java
            ) { (_, _: Int, data: MGRecyclerDataPayload) ->
                if (data !is HeaderItem) return@after

                if (data.headerType == FavoritesItem) binding.b.text = "Favorites"
            }
        }

        patcher.before<WidgetGifPicker>(
            "handleViewState",
            GifPickerViewModel.ViewState::class.java
        ) { (param, viewState: GifPickerViewModel.ViewState) ->
            if (frecencyUserSettings.favoriteStickers.stickerIdsCount != 0) {
                param.args[0] = GifPickerViewModel.ViewState(
                    listOf(GifCategoryItemFavorites(null)) + viewState.gifCategoryItems
                )
            }
        }

        patcher.after<GifViewHolder.Gif>(
            "configure",
            GifAdapterItem.GifItem::class.java,
            Int::class.java,
            Function1::class.java
        ) { (_, item: GifAdapterItem.GifItem) ->
            itemView.setOnLongClickListener {
                val gif = item.gif

                frecencyUserSettings = frecencyUserSettings.copy {
                    favoriteGifs = favoriteGifs.copy {
                        if (gif.tenorGifUrl in gifs) {
                            gifs.remove(gif.tenorGifUrl)
                            Utils.showToast("Removed GIF from favorites")
                        } else {
                            gifs[gif.tenorGifUrl] = FrecencyUserSettingsKt.favoriteGIF {
                                format = FrecencyUserSettings.GIFType.IMAGE
                                width = gif.width
                                height = gif.height
                                src = gif.gifImageUrl
                                order = this@copy.gifs.values.maxOf { it.order } + 1
                            }

                            Utils.showToast("Added GIF to favorites")
                        }
                    }
                }

                patchSettings()

                true
            }
        }

        patcher.after<GifCategoryViewHolder>(
            "configure",
            GifCategoryItem::class.java,
            Function1::class.java
        ) { (_, gifCategoryItem: GifCategoryItem?) ->
            if (gifCategoryItem !is GifCategoryItemFavorites) return@after

            setPreviewImage(frecencyUserSettings.favoriteGifs.gifsMap.values.random().src)
            itemView
                .findViewById<ImageView>(Id.gif_category_item_icon)
                .setImageDrawable(starDrawable)
            itemView.findViewById<TextView>(Id.gif_category_item_title).text = "Favorites"

            gifCategoryItem.previewUrl?.let(::setPreviewImage)
        }

        patcher.instead<WidgetGifCategory>("setUpTitle") {
            val binding = WidgetGifCategory.`access$getBinding$p`(this)
            val gifCategoryItem = WidgetGifCategory.`access$getGifCategory`(this)

            binding.e.text = when (gifCategoryItem) {
                is GifCategoryItem.Standard -> gifCategoryItem.gifCategory.categoryName
                is GifCategoryItem.Trending -> resources.getString(Id.gif_picker_result_type_trending_gifs)
                is GifCategoryItemFavorites -> "Favorites"
                else -> throw NoWhenBranchMatchedException()
            }

            null
        }

        // TODO: Find a better way to enable favoriting
        // patcher.after<WidgetChatListAdapterItemAttachment>(WidgetChatListAdapter::class.java) {
        //     binding.starButton = ImageButton(binding.root.context).apply {
        //         visibility = View.GONE
        //         layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
        //             gravity = Gravity.END
        //             setMargins(0, 8.dp, 8.dp, 0)
        //         }
        //
        //         setImageDrawable(starDrawable)
        //     }
        //
        //     (binding.root as ViewGroup).addView(binding.starButton)
        // }

        // patcher.after<WidgetChatListAdapterItemAttachment>(
        //     "configureUI",
        //     WidgetChatListAdapterItemAttachment.Model::class.java
        // ) { (_, model: WidgetChatListAdapterItemAttachment.Model) ->
        //     val attachment = model.attachmentEntry.attachment
        //
        //     if (attachment.type == MessageAttachmentType.FILE) return@after
        //
        //     val embedType = when (attachment.type.ordinal) {
        //         0 -> EmbedType.VIDEO
        //         1 -> EmbedType.IMAGE
        //         2 -> EmbedType.FILE
        //         else -> throw NoWhenBranchMatchedException()
        //     }
        //
        //     val url = attachment.url
        //
        //     if (EmbedResourceUtils.INSTANCE.isAnimated(embedType, url)) {
        //         binding.starButton.visibility = View.VISIBLE
        //     } else {
        //         binding.starButton.visibility = View.GONE
        //         return@after
        //     }
        //
        //     val visible = WidgetChatListAdapterItemAttachment.Companion.`access$isInlinedAttachment$p`(
        //         WidgetChatListAdapterItemAttachment.Companion,
        //         attachment
        //     ) ||
        //             StoreStream.getUserSettings().isAttachmentMediaInline
        //
        //     if (!visible) return@after
        //
        //     val modelGif = ModelGif(url, url, attachment.width!!, attachment.height!!)
        //
        //     binding.starButton.apply {
        //         // if (model in favoriteGifs) drawable.setTint(context.getColor(R.c.status_yellow))
        //
        //         // setOnClickListener {
        //         //     imageTintList = when {
        //         //         toggleFavorite(modelGif) -> null
        //         //         else -> ColorStateList.valueOf(context.getColor(R.c.status_yellow))
        //         //     }
        //         // }
        //     }
        // }

        patcher.instead<GifCategoryViewModel.Companion>(
            "observeStoreState",
            GifCategoryItem::class.java,
            StoreGifPicker::class.java
        ) { (_, item: GifCategoryItem, store: StoreGifPicker) ->
            when (item) {
                is GifCategoryItem.Standard -> store.observeGifsForSearchQuery(item.gifCategory.categoryName)
                is GifCategoryItem.Trending -> store.observeTrendingCategoryGifs()
                is GifCategoryItemFavorites -> {
                    val pattern = Pattern.compile("(https/.*?)$")
                    val favs = frecencyUserSettings.favoriteGifs
                        .gifsMap
                        .asSequence()
                        .sortedByDescending { it.value.order }
                        .map { (tenorUrl, v) ->
                            val srcUrl = when {
                                v.src.startsWith("//") -> "https:${v.src}"
                                else -> {
                                    val matcher = pattern.matcher(v.src)
                                    if (matcher.find()) {
                                        matcher.group(0)!!.replace("https/", "https://")
                                    } else {
                                        v.src
                                    }
                                }
                            }.replace("AAAPo", "AAAAM")
                                .replace(".mp4", ".gif")

                            ModelGif(srcUrl, tenorUrl, v.width, v.height)
                        }.toList()

                    ScalarSynchronousObservable(favs)
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

        // patcher.after<InlineMediaView>(
        //     "updateUIWithEmbed",
        //     MessageEmbed::class.java,
        //     Int::class.javaObjectType,
        //     Int::class.javaObjectType,
        //     Boolean::class.javaPrimitiveType!!
        // ) { (_, messageEmbed: MessageEmbed, width: Int, height: Int) ->
        //     val root = binding.root as ViewGroup
        //     val find = root.findViewById<View>(starButtonId)
        //
        //     if (EmbedResourceUtils.INSTANCE.isAnimated(messageEmbed)) {
        //         val gifUrl = messageEmbed.rawVideo?.url
        //             ?.replace("AAAPo", "AAAAC") ?: messageEmbed.url
        //         val model = ModelGif(gifUrl, messageEmbed.url, width, height)
        //
        //         if (find == null) root.addView(createStarButton(context, model))
        //     } else {
        //         root.removeView(find)
        //     }
        // }
        //
        // patcher.after<InlineMediaView>(
        //     "updateUIWithAttachment",
        //     MessageAttachment::class.java,
        //     Int::class.javaObjectType,
        //     Int::class.javaObjectType,
        //     Boolean::class.javaPrimitiveType!!
        // ) { (_, messageAttachment: MessageAttachment, width: Int?, height: Int?) ->
        //     if (width == null || height == null) return@after
        //
        //     val root = binding.root as ViewGroup
        //
        //     val embedType = when (messageAttachment.type.ordinal) {
        //         0 -> EmbedType.VIDEO
        //         1 -> EmbedType.IMAGE
        //         2 -> EmbedType.FILE
        //         else -> throw NoWhenBranchMatchedException()
        //     }
        //
        //     val url = messageAttachment.url
        //     val find = root.findViewById<View>(starButtonId)
        //
        //     if (EmbedResourceUtils.INSTANCE.isAnimated(embedType, url)) {
        //         val model = ModelGif(url, url, width, height)
        //
        //         if (find == null) root.addView(createStarButton(context, model))
        //     } else {
        //         root.removeView(find)
        //     }
        // }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}

class LazyMutable<T>(val initializer: () -> T) : ReadWriteProperty<Any?, T> {
    private object UNINITIALIZED_VALUE

    private var prop: Any? = UNINITIALIZED_VALUE

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return if (prop == UNINITIALIZED_VALUE) {
            synchronized(this) {
                return if (prop == UNINITIALIZED_VALUE) initializer().also { prop = it } else prop as T
            }
        } else {
            prop as T
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        synchronized(this) {
            prop = value
        }
    }
}