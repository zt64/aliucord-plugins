@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package dev.zt64.aliucord.plugins.frecents

import com.discord.databinding.InlineMediaViewBinding
import com.discord.databinding.WidgetStickerSheetBinding
import com.discord.widgets.chat.input.gifpicker.GifCategoryViewHolder
import com.discord.widgets.chat.list.InlineMediaView
import com.discord.widgets.stickers.WidgetStickerSheet

private val setPreviewImageMethod = GifCategoryViewHolder::class.java
    .getDeclaredMethod("setPreviewImage", String::class.java)
    .apply { isAccessible = true }

fun GifCategoryViewHolder.setPreviewImage(url: String) {
    setPreviewImageMethod(this, url)
}

val InlineMediaView.binding: InlineMediaViewBinding
    get() = InlineMediaView.`access$getBinding$p`(this)

private val stickerSheetGetBindingMethod = WidgetStickerSheet::class.java
    .getDeclaredMethod("getBinding")
    .apply { isAccessible = true }
val WidgetStickerSheet.binding: WidgetStickerSheetBinding
    get() = stickerSheetGetBindingMethod(this) as WidgetStickerSheetBinding