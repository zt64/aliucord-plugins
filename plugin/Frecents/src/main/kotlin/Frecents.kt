@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

import FrecencyUserSettingsOuterClass.FrecencyUserSettings
import FrecencyUserSettingsOuterClass.FrecencyUserSettings.FavoriteGIFs
import android.content.Context
import android.util.Base64
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.Http
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
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.utils.RxUtils.map
import com.aliucord.utils.lazyField
import com.aliucord.wrappers.messages.AttachmentWrapper.Companion.height
import com.aliucord.wrappers.messages.AttachmentWrapper.Companion.type
import com.aliucord.wrappers.messages.AttachmentWrapper.Companion.url
import com.aliucord.wrappers.messages.AttachmentWrapper.Companion.width
import com.discord.api.message.attachment.MessageAttachmentType
import com.discord.api.message.embed.EmbedType
import com.discord.databinding.ExpressionPickerHeaderItemBinding
import com.discord.models.gifpicker.dto.ModelGif
import com.discord.stores.Dispatcher
import com.discord.stores.StoreGifPicker
import com.discord.stores.StoreMediaFavorites
import com.discord.stores.StoreMediaFavorites.Favorite
import com.discord.stores.StoreStream
import com.discord.stores.updates.ObservationDeck
import com.discord.utilities.embed.EmbedResourceUtils
import com.discord.utilities.mg_recycler.MGRecyclerDataPayload
import com.discord.utilities.persister.Persister
import com.discord.widgets.chat.input.gifpicker.GifAdapterItem
import com.discord.widgets.chat.input.gifpicker.GifCategoryItem
import com.discord.widgets.chat.input.gifpicker.GifCategoryViewHolder
import com.discord.widgets.chat.input.gifpicker.GifCategoryViewModel
import com.discord.widgets.chat.input.gifpicker.GifPickerViewModel
import com.discord.widgets.chat.input.gifpicker.GifViewHolder
import com.discord.widgets.chat.input.gifpicker.WidgetGifCategory
import com.discord.widgets.chat.input.gifpicker.WidgetGifPicker
import com.discord.widgets.chat.input.sticker.HeaderItem
import com.discord.widgets.chat.input.sticker.OwnedHeaderViewHolder
import com.discord.widgets.chat.input.sticker.StickerItem
import com.discord.widgets.chat.input.sticker.StickerPickerViewModel
import com.discord.widgets.chat.input.sticker.StickerViewHolder
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapter
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemAttachment
import com.discord.widgets.emoji.EmojiSheetViewModel
import com.discord.widgets.stickers.WidgetStickerSheet
import com.lytefast.flexinput.R
import frecents.Id
import frecents.binding
import frecents.gif.GifCategoryItemFavorites
import frecents.setPreviewImage
import frecents.starButton
import frecents.sticker.FavoritesItem
import com.discord.widgets.emoji.EmojiSheetViewModel.ViewState as EmojiSheetViewModelViewState
import j0.l.e.k as ScalarSynchronousObservable

private data class Response(val settings: String)

private data class GatewayResponse(val partial: Boolean, val settings: Settings) {
    data class Settings(val proto: String, val type: Int)
}

@AliucordPlugin
class Frecents : Plugin() {
    private val bindingField by lazyField<OwnedHeaderViewHolder>("binding")
    private val OwnedHeaderViewHolder.binding
        get() = bindingField[this] as ExpressionPickerHeaderItemBinding

    val frecencyUserSettings: FrecencyUserSettings by lazy {
        Utils.threadPool
            .submit<FrecencyUserSettings> {
                val res = get()

                FrecencyUserSettings.parseFrom(
                    // data =
                    Base64.decode(res.settings, Base64.DEFAULT)
                )
            }.get()
    }

    private fun get() = Http.Request
        .newDiscordRNRequest("/users/@me/settings-proto/2", "GET")
        .execute()
        .json(Response::class.java)

    fun ModelGif.setFavorite(favorite: Boolean): Boolean {
        GatewayAPI.onEvent<GatewayResponse>("USER_SETTINGS_PROTO_UPDATE") {
            val a = FrecencyUserSettings.parseFrom(
                Base64.decode(it.settings.proto, Base64.DEFAULT)
            )
        }

        val favoriteGif = FrecencyUserSettings.FavoriteGIF
            .newBuilder()
            .apply {
                width = this@setFavorite.width
                height = this@setFavorite.height
                src = gifImageUrl
            }.build()

        frecencyUserSettings
            .toBuilder()
            .mergeFavoriteGifs(
                FavoriteGIFs
                    .newBuilder()
                    .putGifs(gifImageUrl, favoriteGif)
                    .build()
            ).build()

        return false
    }

    @Suppress("SetTextI18n")
    override fun start(ctx: Context) {
        val starDrawable =
            ContextCompat.getDrawable(ctx, R.e.ic_emoji_picker_category_favorites_star)!!

        patcher.before<StickerPickerViewModel.ViewState.Stickers>(
            String::class.java,
            List::class.java,
            List::class.java,
            Boolean::class.java,
            Boolean::class.java
        ) { (param, _: Any, a: List<MGRecyclerDataPayload>) ->
            param.args[1] = listOf(HeaderItem(FavoritesItem)) + a
        }

        patcher.before<WidgetGifPicker>(
            "handleViewState",
            GifPickerViewModel.ViewState::class.java
        ) { (param, viewState: GifPickerViewModel.ViewState) ->
            param.args[0] = GifPickerViewModel.ViewState(
                listOf(GifCategoryItemFavorites(null)) + viewState.gifCategoryItems
            )
        }

        patcher.after<GifViewHolder.Gif>(
            "configure",
            GifAdapterItem.GifItem::class.java,
            Int::class.java,
            Function1::class.java
        ) { (_, item: GifAdapterItem.GifItem) ->
            itemView.setOnLongClickListener {
                // toggleFavorite(item.model)

                true
            }
        }

        patcher.after<GifCategoryViewHolder>(
            "configure",
            GifCategoryItem::class.java,
            Function1::class.java
        ) { (_, gifCategoryItem: GifCategoryItem?) ->
            if (gifCategoryItem !is GifCategoryItemFavorites) return@after

            setPreviewImage("")
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

                is GifCategoryItem.Trending -> {
                    resources.getString(Id.gif_picker_result_type_trending_gifs)
                }

                is GifCategoryItemFavorites -> "Favorites"

                else -> throw NoWhenBranchMatchedException()
            }

            null
        }

        patcher.after<WidgetChatListAdapterItemAttachment>(WidgetChatListAdapter::class.java) {
            binding.starButton = ImageButton(binding.root.context).apply {
                visibility = View.GONE
                layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    gravity = Gravity.END
                    setMargins(0, 8.dp, 8.dp, 0)
                }

                setImageDrawable(starDrawable)
            }

            (binding.root as ViewGroup).addView(binding.starButton)
        }

        patcher.instead<GifCategoryViewModel.Companion>(
            "observeStoreState",
            GifCategoryItem::class.java,
            StoreGifPicker::class.java
        ) { (_, item: GifCategoryItem, store: StoreGifPicker) ->
            when (item) {
                is GifCategoryItem.Standard -> {
                    store.observeGifsForSearchQuery(item.gifCategory.categoryName)
                }

                is GifCategoryItem.Trending -> store.observeTrendingCategoryGifs()

                is GifCategoryItemFavorites -> {
                    ScalarSynchronousObservable(
                        frecencyUserSettings.favoriteGifs
                            .gifsMap
                            .asSequence()
                            .sortedByDescending { it.value.order }
                            .map { (key, v) ->
                                ModelGif(
                                    v.src,
                                    key,
                                    v.width,
                                    v.height
                                )
                            }.toList()
                    )
                }

                else -> throw NoWhenBranchMatchedException()
            }.map(GifCategoryViewModel::StoreState)
        }

        patcher.after<StickerViewHolder>(
            "configureSticker",
            MGRecyclerDataPayload::class.java
        ) { (param, payload: MGRecyclerDataPayload) ->

            StickerViewHolder.`access$getBinding$p`(this).b.setOnLongClickListener {
                // toggleFavorite(item.model)
                val stickerItem = payload as StickerItem

                val favorite =

                    frecencyUserSettings.favoriteStickers
                        .toBuilder()
                        .addStickerIds(stickerItem.sticker.id)
                        .buildPartial()
                true
            }
        }
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

        patcher.after<OwnedHeaderViewHolder>(
            "onConfigure",
            Int::class.java,
            MGRecyclerDataPayload::class.java
        ) { (_, _: Int, data: MGRecyclerDataPayload) ->
            if (data !is HeaderItem) return@after

            if (data.headerType == FavoritesItem) binding.b.text = "Favorites"
        }

        patcher.after<WidgetChatListAdapterItemAttachment>(
            "configureUI",
            WidgetChatListAdapterItemAttachment.Model::class.java
        ) { (_, model: WidgetChatListAdapterItemAttachment.Model) ->
            val attachment = model.attachmentEntry.attachment

            if (attachment.type == MessageAttachmentType.FILE) return@after

            val embedType = when (attachment.type.ordinal) {
                0 -> EmbedType.VIDEO
                1 -> EmbedType.IMAGE
                2 -> EmbedType.FILE
                else -> throw NoWhenBranchMatchedException()
            }

            val url = attachment.url

            if (EmbedResourceUtils.INSTANCE.isAnimated(embedType, url)) {
                binding.starButton.visibility = View.VISIBLE
            } else {
                binding.starButton.visibility = View.GONE
                return@after
            }

            val visible =
                WidgetChatListAdapterItemAttachment.Companion.`access$isInlinedAttachment$p`(
                    WidgetChatListAdapterItemAttachment.Companion,
                    attachment
                ) ||
                        StoreStream.getUserSettings().isAttachmentMediaInline

            if (!visible) return@after

            val modelGif = ModelGif(url, url, attachment.width!!, attachment.height!!)

            binding.starButton.apply {
                // if (model in favoriteGifs) drawable.setTint(context.getColor(R.c.status_yellow))

                // setOnClickListener {
                //     imageTintList = when {
                //         toggleFavorite(modelGif) -> null
                //         else -> ColorStateList.valueOf(context.getColor(R.c.status_yellow))
                //     }
                // }
            }
        }

        patcher.after<EmojiSheetViewModel>(
            "setFavorite",
            Boolean::class.java
        ) { (param, favorite: Boolean) ->
            val data = when (val state = viewState) {
                is EmojiSheetViewModelViewState.EmojiCustom -> state.emojiCustom.id.toString()
                is EmojiSheetViewModelViewState.EmojiUnicode -> state.emojiUnicode.firstName
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

        // Patch the store to fetch favorite emojis
        patcher.after<StoreMediaFavorites>(
            ObservationDeck::class.java,
            Dispatcher::class.java,
            Persister::class.java
        ) {
            StoreMediaFavorites.`access$getFavorites$p`(this).apply {
                clear()
                add(Favorite.FavUnicodeEmoji("👍"))
                markChanged()
            }
        }

        patcher.after<WidgetStickerSheet>("onViewCreated") {
            
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}

@Suppress("NOTHING_TO_INLINE")
private inline fun FrecencyUserSettings.FavoriteGIF.toModelGif(): ModelGif {
    return ModelGif(src, src, width, height)
}
