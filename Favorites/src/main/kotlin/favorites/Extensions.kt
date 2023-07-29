package favorites

import android.widget.ImageButton
import com.discord.databinding.WidgetChatListAdapterItemAttachmentBinding
import com.discord.widgets.chat.input.gifpicker.GifCategoryViewHolder
import com.discord.widgets.chat.list.InlineMediaView
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemAttachment
import kotlin.properties.Delegates

private val setPreviewImageMethod = GifCategoryViewHolder::class.java
    .getDeclaredMethod("setPreviewImage", String::class.java)
    .apply { isAccessible = true }

fun GifCategoryViewHolder.setPreviewImage(url: String) {
    setPreviewImageMethod(this, url)
}

val InlineMediaView.binding
    get() = InlineMediaView.`access$getBinding$p`(this)

val WidgetChatListAdapterItemAttachment.binding
    get() = WidgetChatListAdapterItemAttachment.`access$getBinding$p`(this)

var WidgetChatListAdapterItemAttachmentBinding.starButton: ImageButton by Delegates.notNull()