package tk.zt64.plugins.customstatuspresets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.discord.models.domain.emoji.Emoji
import com.discord.models.domain.emoji.ModelEmojiCustom
import com.discord.models.domain.emoji.ModelEmojiUnicode
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.utilities.icon.IconUtils
import com.discord.utilities.images.MGImages
import com.discord.widgets.chat.input.emoji.EmojiPickerContextType
import com.discord.widgets.chat.input.emoji.EmojiPickerNavigator
import com.discord.widgets.user.WidgetUserSetCustomStatus
import com.discord.widgets.user.profile.UserStatusPresenceCustomView
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R
import tk.zt64.plugins.CustomStatusPresets

class PresetAdapter(private val widgetUserSetCustomStatus: WidgetUserSetCustomStatus, private val presets: ArrayList<UserStatusPresenceCustomView.ViewState.WithStatus>) : RecyclerView.Adapter<PresetViewHolder>() {
    private fun saveAccounts() = CustomStatusPresets.mSettings.setObject("presets", presets)
    fun addPreset(preset: UserStatusPresenceCustomView.ViewState.WithStatus): Boolean = presets.add(preset).also { if (it) saveAccounts() }
    private fun removePreset(preset: UserStatusPresenceCustomView.ViewState.WithStatus): Boolean = presets.remove(preset).also { if (it) saveAccounts() }

    private val statusViewId = Utils.getResId("view_user_status_presence_custom", "layout")
    private val emojiPreviewSize = Utils.getResId("custom_status_emoji_preview_size", "dimen")

    private fun setEmoji(customEmoji: SimpleDraweeView, emoji: UserStatusPresenceCustomView.Emoji) {
        val ctx = customEmoji.context
        var str: String? = null

        if (emoji.id != null)
            str = ModelEmojiCustom.getImageUri(emoji.id.toLong(), emoji.isAnimated, IconUtils.getMediaProxySize(ctx.resources.getDimensionPixelSize(emojiPreviewSize)))
        else {
            val modelEmojiUnicode = StoreStream.getEmojis().unicodeEmojiSurrogateMap[emoji.name]
            if (modelEmojiUnicode != null) str = ModelEmojiUnicode.getImageUri(modelEmojiUnicode.codePoints, ctx)
        }

        MGImages.setImage(customEmoji, str)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PresetViewHolder(this, LayoutInflater.from(parent.context).inflate(statusViewId, parent, false) as LinearLayout)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) = presets[position].let {
        if (it.statusText.isEmpty()) {
            holder.customText.apply {
                text = "No Status Text"
                setTextColor(ColorCompat.getThemedColor(this, R.b.colorTextMuted))
            }
        } else holder.customText.text = it.statusText

        if (it.emoji != null) setEmoji(holder.customEmoji, it.emoji)
    }

    override fun getItemCount(): Int = presets.size

    fun onRemove(position: Int) = presets[position].let {
        removePreset(it)
        notifyItemRemoved(position)
    }

    fun onClick(position: Int) = presets[position].let {
        with (WidgetUserSetCustomStatus.`access$getViewModel$p`(widgetUserSetCustomStatus)) {
            clearStatusTextAndEmoji()
            if (it.statusText != null) setStatusText(it.statusText)
            if (it.emoji != null)
                setStatusEmoji(
                    if (it.emoji.id == null)
                        StoreStream.getEmojis().unicodeEmojiSurrogateMap[it.emoji.name]
                    else
                        StoreStream.getEmojis().getCustomEmojiInternal(it.emoji.id.toLong())
                )
        }
    }

    fun editEmoji(customEmoji: SimpleDraweeView, position: Int) = presets[position].let {
        val function = { modelEmoji: Emoji ->
            val emoji = StoreStream.getEmojis().getCustomEmojiInternal(modelEmoji.uniqueId.toLong()).let { modelEmojiCustom ->
                UserStatusPresenceCustomView.Emoji(modelEmojiCustom.id.toString(), modelEmojiCustom.name, modelEmojiCustom.isAnimated)
            }
            setEmoji(customEmoji, emoji)
            removePreset(it)
            addPreset(it.copy(emoji, it.statusText))
            Unit.a
        }
        EmojiPickerNavigator.launchBottomSheet(Utils.appActivity.supportFragmentManager, function, EmojiPickerContextType.Global.INSTANCE, null)
    }
}