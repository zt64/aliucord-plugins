package com.aliucord.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Logger
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.aliucord.utils.DimenUtils
import com.aliucord.utils.ReflectUtils
import com.aliucord.views.Divider
import com.discord.databinding.WidgetGuildFolderSettingsBinding
import com.discord.databinding.WidgetGuildsListItemFolderBinding
import com.discord.widgets.guilds.WidgetGuildFolderSettings
import com.discord.widgets.guilds.WidgetGuildFolderSettingsViewModel
import com.discord.widgets.guilds.`WidgetGuildFolderSettings$configureUI$3`
import com.discord.widgets.guilds.list.FolderItemDecoration
import com.discord.widgets.guilds.list.GuildListItem
import com.discord.widgets.guilds.list.GuildListViewHolder
import com.lytefast.flexinput.R
import top.canyie.pine.Pine.CallFrame
import top.canyie.pine.callback.MethodReplacement
import java.lang.reflect.InvocationTargetException

@AliucordPlugin
class FolderOpacity : Plugin() {
    private val logger = Logger("FolderOpacity")
    private val folderViewHolderMap = HashMap<Long, View>()

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        patcher.patch(GuildListViewHolder.FolderViewHolder::class.java.getDeclaredMethod("shouldDrawDecoration"), MethodReplacement.returnConstant(false))

        // Removing this line breaks the above patch for some reason so I guess it stays
        patcher.patch(FolderItemDecoration::class.java.getDeclaredMethod("onDraw", Canvas::class.java, RecyclerView::class.java, RecyclerView.State::class.java), PinePatchFn { })
        patcher.patch(GuildListViewHolder.FolderViewHolder::class.java.getDeclaredMethod("configure", GuildListItem.FolderItem::class.java), PinePatchFn { callFrame: CallFrame ->
            val folderItem = callFrame.args[0] as GuildListItem.FolderItem
            val folderId = folderItem.folderId
            if (!folderViewHolderMap.containsKey(folderId)) folderViewHolderMap[folderId] = (callFrame.thisObject as GuildListViewHolder.FolderViewHolder).itemView
            try {
                val binding = (ReflectUtils.getField(callFrame.thisObject, "binding") as WidgetGuildsListItemFolderBinding?)!!
                val opacity = settings.getInt(folderItem.folderId.toString() + "opacity", 30)
                val color = folderItem.color
                val background = ContextCompat.getDrawable(binding.root.context, R.d.drawable_squircle_white_alpha_30)!!
                background.setColorFilter(ColorUtils.setAlphaComponent(color
                    ?: Color.WHITE, opacity), PorterDuff.Mode.SRC)
                binding.c.background = background
            } catch (e: NoSuchFieldException) {
                logger.error(e)
            } catch (e: IllegalAccessException) {
                logger.error(e)
            }
        })
        val seekBarId = View.generateViewId()
        val getBinding = WidgetGuildFolderSettings::class.java.getDeclaredMethod("getBinding")
            .apply { isAccessible = true }

        // Patch folder settings
        patcher.patch(WidgetGuildFolderSettings::class.java.getDeclaredMethod("configureUI", WidgetGuildFolderSettingsViewModel.ViewState::class.java), PinePatchFn { callFrame: CallFrame ->
            try {
                val _this = callFrame.thisObject as WidgetGuildFolderSettings
                val binding = getBinding.invoke(_this) as WidgetGuildFolderSettingsBinding
                val ctx = _this.requireContext()
                val linearLayout = binding.d.parent as LinearLayout
                if (linearLayout.findViewById<View?>(seekBarId) != null) return@PinePatchFn

                val opacity = settings.getInt(WidgetGuildFolderSettings.`access$getViewModel$p`(_this).folderId.toString() + "opacity", 30)
                val opacityHeader = TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Header).apply {
                    text = "Folder Opacity"
                }
                val currentOpacity = TextView(ctx, null, 0, R.h.UiKit_TextView).apply {
                    text = opacity.toString()
                    width = DimenUtils.dpToPx(28)
                }
                val seekbar = SeekBar(ctx, null, 0, R.h.UiKit_SeekBar).apply {
                    id = seekBarId
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    max = 255
                    progress = opacity
                    setPadding(DimenUtils.dpToPx(12), 0, DimenUtils.dpToPx(12), 0)
                    setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            currentOpacity.text = progress.toString()
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar) {
                            binding.f.visibility = View.VISIBLE
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    })
                }
                val opacitySection = LinearLayout(ctx, null, 0, R.h.UiKit_Settings_Item).apply {
                    addView(currentOpacity)
                    addView(seekbar)
                }
                linearLayout.addView(Divider(ctx))
                linearLayout.addView(opacityHeader)
                linearLayout.addView(opacitySection)
            } catch (e: IllegalAccessException) {
                logger.error(e)
            } catch (e: InvocationTargetException) {
                logger.error(e)
            }
        })

        // Patch save button
        patcher.patch(`WidgetGuildFolderSettings$configureUI$3`::class.java.getDeclaredMethod("onClick", View::class.java), PinePatchFn { callFrame: CallFrame ->
            val widgetGuildFolderSettings = (callFrame.thisObject as `WidgetGuildFolderSettings$configureUI$3`).`this$0`
            val viewModel = WidgetGuildFolderSettings.`access$getViewModel$p`((callFrame.thisObject as `WidgetGuildFolderSettings$configureUI$3`).`this$0`)
            try {
                val folderId = viewModel.folderId
                val seekBar = (getBinding.invoke(widgetGuildFolderSettings) as WidgetGuildFolderSettingsBinding).root.findViewById<SeekBar>(seekBarId)
                settings.setInt(folderId.toString() + "opacity", seekBar.progress)
                folderViewHolderMap[folderId]?.invalidate()
            } catch (e: IllegalAccessException) {
                logger.error(e)
            } catch (e: InvocationTargetException) {
                logger.error(e)
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}