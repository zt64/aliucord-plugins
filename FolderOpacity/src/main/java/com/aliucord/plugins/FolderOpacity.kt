package com.aliucord.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.patcher.InsteadHook
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.views.Divider
import com.discord.databinding.WidgetGuildFolderSettingsBinding
import com.discord.databinding.WidgetGuildsListItemFolderBinding
import com.discord.widgets.guilds.WidgetGuildFolderSettings
import com.discord.widgets.guilds.WidgetGuildFolderSettingsViewModel
import com.discord.widgets.guilds.`WidgetGuildFolderSettings$configureUI$3`
import com.discord.widgets.guilds.list.GuildListItem
import com.discord.widgets.guilds.list.GuildListViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lytefast.flexinput.R
import java.util.*
import kotlin.collections.HashMap

@AliucordPlugin
class FolderOpacity : Plugin() {
    private val folderViewHolderMap = HashMap<Long, GuildListViewHolder.FolderViewHolder>()

    private val folderBindingField = GuildListViewHolder.FolderViewHolder::class.java.getDeclaredField("binding").apply { isAccessible = true }
    private val GuildListViewHolder.FolderViewHolder.binding get() = folderBindingField[this] as WidgetGuildsListItemFolderBinding

    private val getBindingMethod = WidgetGuildFolderSettings::class.java.getDeclaredMethod("getBinding").apply { isAccessible = true }
    private fun WidgetGuildFolderSettings.getBinding() = getBindingMethod.invoke(this) as WidgetGuildFolderSettingsBinding

    private val folderContainerId = Utils.getResId("guilds_item_folder_container", "id")

    private fun setAlpha(folder: GuildListViewHolder.FolderViewHolder, alpha: Int) {
        val root = folder.binding.root
        val color = ColorUtils.setAlphaComponent(folder.color ?: Color.WHITE, alpha)
        val background = ContextCompat.getDrawable(root.context, R.e.drawable_squircle_white_alpha_30)

        background?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC)
        root.findViewById<FrameLayout>(folderContainerId).background = background
    }

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        val folderColorPickerId = Utils.getResId("guild_folder_settings_color", "id")
        val saveButtonId = Utils.getResId("guild_folder_settings_save", "id")
        val seekBarId = View.generateViewId()

        // Disable decoration drawing
        patcher.patch(GuildListViewHolder.FolderViewHolder::class.java.getDeclaredMethod("shouldDrawDecoration"), InsteadHook.returnConstant(false))

        // Set the background color with alpha
        patcher.patch(GuildListViewHolder.FolderViewHolder::class.java.getDeclaredMethod("configure", GuildListItem.FolderItem::class.java), Hook {
            val thisObject = it.thisObject as GuildListViewHolder.FolderViewHolder
            val folderItem = it.args[0] as GuildListItem.FolderItem

            folderViewHolderMap[folderItem.folderId] = thisObject

            setAlpha(thisObject, settings.getInt(folderItem.folderId.toString() + "opacity", 30))
        })

        // Patch folder settings
        patcher.patch(WidgetGuildFolderSettings::class.java.getDeclaredMethod("configureUI", WidgetGuildFolderSettingsViewModel.ViewState::class.java), Hook {
            val thisObject = it.thisObject as WidgetGuildFolderSettings

            val root = thisObject.getBinding().root
            val ctx = thisObject.requireContext()
            val linearLayout = root.findViewById<RelativeLayout>(folderColorPickerId).parent as LinearLayout
            if (linearLayout.findViewById<View>(seekBarId) != null) return@Hook

            val opacity = settings.getInt(WidgetGuildFolderSettings.`access$getViewModel$p`(thisObject).folderId.toString() + "opacity", 30)
            val currentOpacity = TextView(ctx, null, 0, R.i.UiKit_TextView).apply {
                text = opacity.toString()
                width = 28.dp
            }
            with(linearLayout) {
                addView(Divider(ctx))
                addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
                    text = "Folder Opacity"
                })
                addView(LinearLayout(ctx, null, 0, R.i.UiKit_Settings_Item).apply {
                    addView(currentOpacity)
                    addView(SeekBar(ctx, null, 0, R.i.UiKit_SeekBar).apply {
                        id = seekBarId
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        max = 255
                        progress = opacity
                        12.dp.let { dp -> setPadding(dp, 0, dp, 0) }
                        setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                                currentOpacity.text = progress.toString()
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar) {
                                root.findViewById<FloatingActionButton>(saveButtonId).visibility = View.VISIBLE
                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar) {}
                        })
                    })
                })
            }
        })

        // Patch save button
        patcher.patch(`WidgetGuildFolderSettings$configureUI$3`::class.java.getDeclaredMethod("onClick", View::class.java), Hook {
            val thisObject = it.thisObject as `WidgetGuildFolderSettings$configureUI$3`
            val widgetGuildFolderSettings = thisObject.`this$0`
            val folderId = WidgetGuildFolderSettings.`access$getViewModel$p`(widgetGuildFolderSettings).folderId
            val seekBar = widgetGuildFolderSettings.getBinding().root.findViewById<SeekBar>(seekBarId)

            settings.setInt(folderId.toString() + "opacity", seekBar.progress)

            setAlpha(folderViewHolderMap[folderId]!!, seekBar.progress)
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}