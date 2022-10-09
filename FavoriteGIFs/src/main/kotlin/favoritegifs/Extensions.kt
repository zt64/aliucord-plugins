package favoritegifs

import com.discord.widgets.chat.input.gifpicker.GifCategoryViewHolder

private val setPreviewImageMethod = GifCategoryViewHolder::class.java
    .getDeclaredMethod("setPreviewImage", String::class.java)
    .apply { isAccessible = true }

fun GifCategoryViewHolder.setPreviewImage(url: String) = setPreviewImageMethod(this, url) as Unit