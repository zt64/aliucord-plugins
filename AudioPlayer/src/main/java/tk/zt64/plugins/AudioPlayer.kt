package tk.zt64.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.wrappers.messages.AttachmentWrapper.Companion.url
import com.discord.api.message.attachment.MessageAttachment
import com.discord.utilities.textprocessing.MessageRenderContext
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemAttachment
import com.google.android.material.card.MaterialCardView
import com.lytefast.flexinput.R
import tk.zt64.plugins.audioplayer.ControlsLayout
import tk.zt64.plugins.audioplayer.PluginSettings
import java.text.SimpleDateFormat
import java.util.*

@AliucordPlugin
class AudioPlayer : Plugin() {
    private val WidgetChatListAdapterItemAttachment.binding
        get() = WidgetChatListAdapterItemAttachment.`access$getBinding$p`(this)

    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        val playerBarId = View.generateViewId()
        val attachmentCardId = Utils.getResId("chat_list_item_attachment_card", "id")

        patcher.after<WidgetChatListAdapterItemAttachment>("configureFileData", MessageAttachment::class.java, MessageRenderContext::class.java) {
            val root = binding.root as ConstraintLayout
            val card = root.findViewById<MaterialCardView>(attachmentCardId)

            if (card.findViewById<LinearLayout>(playerBarId) != null) return@after

            val messageAttachment = it.args[0] as MessageAttachment
            val ctx = root.context

            val retriever = MediaMetadataRetriever().apply {
                setDataSource(messageAttachment.url, hashMapOf())
            }
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()

            retriever.release()

            card.addView(ControlsLayout(ctx).apply {
                id = playerBarId

                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = 60.dp
                    gravity = Gravity.BOTTOM
                }

                8.dp.let { dp -> setPadding(dp, dp, dp, dp) }

                addView(ImageButton(ctx).apply {
                    var paused = false
                    background = AppCompatResources.getDrawable(ctx, com.google.android.exoplayer2.ui.R.b.exo_controls_play)
                    8.dp.let { dp -> setPadding(dp, dp, dp, dp) }
                    setOnClickListener {
                        paused = !paused
                        background = if (paused) AppCompatResources.getDrawable(ctx, com.google.android.exoplayer2.ui.R.b.exo_controls_pause) else
                            AppCompatResources.getDrawable(ctx, com.google.android.exoplayer2.ui.R.b.exo_controls_play)
                    }
                })
                addView(TextView(ctx, null, 0, R.i.UiKit_TextView).apply {
                    text = SimpleDateFormat("HH:mm:ss").format(duration)
                    8.dp.let { dp -> setPadding(dp, dp, dp, dp) }
                })
                addView(SeekBar(ctx, null, 0, R.i.UiKit_SeekBar).apply {
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        weight = 0.5f
                    }
                    background = AppCompatResources.getDrawable(ctx, R.e.drawable_settings_progress)
                    progressDrawable = AppCompatResources.getDrawable(ctx, R.e.drawable_progress_green)
                    progress = 0
                    max = 200
                    12.dp.let { dp -> setPadding(dp, 2.dp, dp, 2.dp) }
                })
            })
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}