import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.before
import com.aliucord.patcher.instead
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.wrappers.embeds.MessageEmbedWrapper.Companion.rawVideo
import com.aliucord.wrappers.embeds.MessageEmbedWrapper.Companion.url
import com.aliucord.wrappers.embeds.VideoWrapper.Companion.url
import com.aliucord.wrappers.messages.AttachmentWrapper.Companion.type
import com.aliucord.wrappers.messages.AttachmentWrapper.Companion.url
import com.discord.api.message.attachment.MessageAttachment
import com.discord.api.message.embed.EmbedType
import com.discord.api.message.embed.MessageEmbed
import com.discord.models.gifpicker.dto.ModelGif
import com.discord.stores.StoreGifPicker
import com.discord.utilities.embed.EmbedResourceUtils
import com.discord.utilities.mg_recycler.MGRecyclerDataPayload
import com.discord.widgets.chat.input.gifpicker.*
import com.discord.widgets.chat.input.sticker.*
import com.discord.widgets.chat.list.InlineMediaView
import com.google.gson.reflect.TypeToken
import com.lytefast.flexinput.R
import favoritegifs.*

@AliucordPlugin
class FavoriteGIFs : Plugin() {
    private val favoriteGifsType = TypeToken.getParameterized(List::class.java, ModelGif::class.java).getType()

    private val InlineMediaView.binding
        get() = InlineMediaView.`access$getBinding$p`(this)

    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        val favoriteGifs = settings.getObject<MutableList<ModelGif>>("favoriteGifs", mutableListOf(), favoriteGifsType)
        val starButtonId = View.generateViewId()

        fun toggleFavorite(model: ModelGif) = favoriteGifs.remove(model).also { isRemoved ->
            if (isRemoved) {
                Utils.showToast("Unfavorited gif")
            } else {
                favoriteGifs.add(0, model)
                Utils.showToast("Favorited gif")
            }

            settings.setObject("favoriteGifs", favoriteGifs)
        }

        fun createStarButton(context: Context, model: ModelGif) = ImageView(context).apply {
            id = starButtonId
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END
                setMargins(0, 8.dp, 8.dp, 0)
            }

            val drawable = ContextCompat.getDrawable(context, R.e.ic_emoji_picker_category_favorites_star)!!
                .mutate()

            if (favoriteGifs.contains(model)) drawable.setTint(context.getColor(R.c.status_yellow))

            setImageDrawable(drawable)
            setOnClickListener {
                if (toggleFavorite(model)) {
                    drawable.setTintList(null)
                } else {
                    drawable.setTint(context.getColor(R.c.status_yellow))
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        patcher.before<StickerPickerViewModel.ViewState.Stickers>(String::class.java, List::class.java, List::class.java, Boolean::class.java, Boolean::class.java) {
            it.args[1] = listOf(HeaderItem(FavoritesItem)) + it.args[1] as List<MGRecyclerDataPayload>
        }

        patcher.before<WidgetGifPicker>("handleViewState", GifPickerViewModel.ViewState::class.java) {
            val viewState = it.args[0] as GifPickerViewModel.ViewState

            it.args[0] = GifPickerViewModel.ViewState(
                listOf(GifCategoryItemFavorites(favoriteGifs.randomOrNull()?.gifImageUrl)) + viewState.gifCategoryItems
            )
        }

        patcher.after<GifViewHolder.Gif>("configure", GifAdapterItem.GifItem::class.java, Int::class.java, Function1::class.java) {
            val (model) = it.args[0] as GifAdapterItem.GifItem

            itemView.setOnLongClickListener {
                toggleFavorite(model)

                true
            }
        }

        patcher.after<GifCategoryViewHolder>("configure", GifCategoryItem::class.java, Function1::class.java) {
            val gifCategoryItem = it.args[0] as? GifCategoryItemFavorites ?: return@after
            val starDrawable = ResourcesCompat.getDrawable(itemView.resources, R.e.ic_emoji_picker_category_favorites_star, null)

            itemView.findViewById<ImageView>(Id.gif_category_item_icon).setImageDrawable(starDrawable)
            itemView.findViewById<TextView>(Id.gif_category_item_title).text = "Favorites"

            setPreviewImage(gifCategoryItem.getGifPreviewUrl())
        }

        patcher.instead<WidgetGifCategory>("setUpTitle") {
            val binding = WidgetGifCategory.`access$getBinding$p`(this)
            val textView = binding.root.findViewById<TextView>(Id.gif_category_title)!!
            val gifCategoryItem = WidgetGifCategory.`access$getGifCategory`(this)

            textView.text = when (gifCategoryItem) {
                is GifCategoryItem.Standard -> gifCategoryItem.gifCategory.categoryName
                is GifCategoryItem.Trending -> resources.getString(Id.gif_picker_result_type_trending_gifs)
                is GifCategoryItemFavorites -> "Favorites"
                else -> throw NoWhenBranchMatchedException()
            }

            null
        }

        patcher.instead<GifCategoryViewModel.Companion>("observeStoreState", GifCategoryItem::class.java, StoreGifPicker::class.java) {
            val gifCategoryItem = it.args[0] as GifCategoryItem
            val storeGifPicker = it.args[1] as StoreGifPicker

            val observable = when (gifCategoryItem) {
                is GifCategoryItem.Standard -> storeGifPicker.observeGifsForSearchQuery(gifCategoryItem.gifCategory.categoryName)
                is GifCategoryItem.Trending -> storeGifPicker.observeTrendingCategoryGifs()
                // Creates ScalarSynchronousObservable with the value to emit
                is GifCategoryItemFavorites -> j0.l.e.k(favoriteGifs)
                else -> throw NoWhenBranchMatchedException()
            }

            // Observable#map
            observable.G { gifList -> GifCategoryViewModel.StoreState(gifList) }
        }

        patcher.after<InlineMediaView>("updateUIWithEmbed", MessageEmbed::class.java, Int::class.javaObjectType, Int::class.javaObjectType, Boolean::class.javaPrimitiveType!!) {
            val messageEmbed = it.args[0] as MessageEmbed
            val width = it.args[1] as Int
            val height = it.args[2] as Int

            val root = binding.root as ViewGroup

            if (EmbedResourceUtils.INSTANCE.isAnimated(messageEmbed)) {
                val gifUrl = messageEmbed.rawVideo?.url?.replace("AAAPo", "AAAAC") ?: messageEmbed.url
                val model = ModelGif(gifUrl, messageEmbed.url, width, height)

                if (root.findViewById<View>(starButtonId) == null) root.addView(createStarButton(context, model))
            } else {
                root.removeView(root.findViewById(starButtonId))
            }
        }

        patcher.after<InlineMediaView>("updateUIWithAttachment", MessageAttachment::class.java, Int::class.javaObjectType, Int::class.javaObjectType, Boolean::class.javaPrimitiveType!!) {
            val messageAttachment = it.args[0] as MessageAttachment
            val width = it.args[1] as Int? ?: return@after
            val height = it.args[2] as Int? ?: return@after

            val root = binding.root as ViewGroup

            val embedType = when (messageAttachment.type.ordinal) {
                0 -> EmbedType.VIDEO
                1 -> EmbedType.IMAGE
                2 -> EmbedType.FILE
                else -> throw NoWhenBranchMatchedException()
            }

            val url = messageAttachment.url

            if (EmbedResourceUtils.INSTANCE.isAnimated(embedType, url)) {
                val model = ModelGif(url, url, width, height)

                if (root.findViewById<View>(starButtonId) == null) root.addView(createStarButton(context, model))
            } else {
                root.removeView(root.findViewById(starButtonId))
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}