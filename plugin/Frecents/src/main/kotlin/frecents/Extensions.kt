@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package frecents

import android.widget.ImageButton
import com.discord.databinding.WidgetChatListAdapterItemAttachmentBinding
import com.discord.databinding.WidgetStickerSheetBinding
import com.discord.widgets.chat.input.gifpicker.GifCategoryViewHolder
import com.discord.widgets.chat.list.InlineMediaView
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemAttachment
import com.discord.widgets.stickers.WidgetStickerSheet
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private val setPreviewImageMethod = GifCategoryViewHolder::class.java
    .getDeclaredMethod("setPreviewImage", String::class.java)
    .apply { isAccessible = true }

fun GifCategoryViewHolder.setPreviewImage(url: String) {
    setPreviewImageMethod(this, url)
}

val InlineMediaView.binding
    get() = InlineMediaView.`access$getBinding$p`(this)

val WidgetChatListAdapterItemAttachment.binding: WidgetChatListAdapterItemAttachmentBinding
    get() = WidgetChatListAdapterItemAttachment.`access$getBinding$p`(this)

var WidgetChatListAdapterItemAttachmentBinding.starButton: ImageButton by Delegates.notNull()
val WidgetStickerSheet.binding: WidgetStickerSheetBinding by property("binding")

private inline fun <reified T : Any, V> property(name: String): ReadWriteProperty<T, V> {
    return object : ReadWriteProperty<T, V> {
        private val field = T::class.java
            .getDeclaredField(name)
            .apply { isAccessible = true }

        override fun getValue(thisRef: T, property: KProperty<*>): V {
            @Suppress("UNCHECKED_CAST")
            return field[thisRef] as V
        }

        override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
            field[thisRef] = value
        }
    }
}