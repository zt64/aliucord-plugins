package favoritegifs

import com.discord.widgets.chat.input.gifpicker.GifCategoryItem

class GifCategoryItemFavorites(private var previewUrl: String?) : GifCategoryItem(null) {
    fun getGifPreviewUrl() = previewUrl ?: ""
}