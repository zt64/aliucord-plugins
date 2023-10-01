
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.aliucord.Utils.getResId
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.instead
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.views.Divider
import com.discord.databinding.WidgetGuildFolderSettingsBinding
import com.discord.databinding.WidgetGuildsListItemFolderBinding
import com.discord.widgets.guilds.WidgetGuildFolderSettings
import com.discord.widgets.guilds.`WidgetGuildFolderSettings$configureUI$3`
import com.discord.widgets.guilds.WidgetGuildFolderSettingsViewModel
import com.discord.widgets.guilds.list.GuildListItem
import com.discord.widgets.guilds.list.GuildListViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lytefast.flexinput.R

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
class FolderOpacity : Plugin() {
    private val folderViewHolderMap = HashMap<Long, GuildListViewHolder.FolderViewHolder>()

    private val folderBindingField = GuildListViewHolder.FolderViewHolder::class.java
        .getDeclaredField("binding")
        .apply { isAccessible = true }
    private val GuildListViewHolder.FolderViewHolder.binding get() = folderBindingField[this] as WidgetGuildsListItemFolderBinding

    private val getBindingMethod = WidgetGuildFolderSettings::class.java
        .getDeclaredMethod("getBinding")
        .apply { isAccessible = true }

    private fun WidgetGuildFolderSettings.getBinding() = getBindingMethod(this) as WidgetGuildFolderSettingsBinding

    private val folderContainerId = getResId("guilds_item_folder_container", "id")

    private fun GuildListViewHolder.FolderViewHolder.setAlpha(alpha: Int) {
        val root = binding.root
        val color = ColorUtils.setAlphaComponent(color ?: Color.WHITE, alpha)
        val background = ContextCompat.getDrawable(root.context, R.e.drawable_squircle_white_alpha_30)

        background?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC)
        root.findViewById<FrameLayout>(folderContainerId).background = background
    }

    @Suppress("SetTextI18n")
    override fun start(context: Context) {
        val folderColorPickerId = getResId("guild_folder_settings_color", "id")
        val saveButtonId = getResId("guild_folder_settings_save", "id")
        val seekBarId = View.generateViewId()

        // Disable decoration drawing
        patcher.instead<GuildListViewHolder.FolderViewHolder>("shouldDrawDecoration") { false }

        // Set the background color with alpha
        patcher.after<GuildListViewHolder.FolderViewHolder>("configure", GuildListItem.FolderItem::class.java) {
            val folderItem = it.args[0] as GuildListItem.FolderItem

            folderViewHolderMap[folderItem.folderId] = this

            setAlpha(settings.getInt("${folderItem.folderId}opacity", 30))
        }

        // Patch folder settings
        patcher.after<WidgetGuildFolderSettings>("configureUI", WidgetGuildFolderSettingsViewModel.ViewState::class.java) {
            val root = getBinding().root
            val ctx = requireContext()
            val linearLayout = root.findViewById<RelativeLayout>(folderColorPickerId).parent as LinearLayout
            if (linearLayout.findViewById<View>(seekBarId) != null) return@after

            val opacity = settings.getInt("${WidgetGuildFolderSettings.`access$getViewModel$p`(this).folderId}opacity", 30)
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
                        layoutParams =
                            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
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
        }

        // Patch save button
        patcher.after<`WidgetGuildFolderSettings$configureUI$3`>("onClick", View::class.java) {
            val widgetGuildFolderSettings = `this$0`
            val folderId = WidgetGuildFolderSettings.`access$getViewModel$p`(widgetGuildFolderSettings).folderId
            val seekBar = widgetGuildFolderSettings.getBinding().root.findViewById<SeekBar>(seekBarId)

            settings.setInt(folderId.toString() + "opacity", seekBar.progress)

            folderViewHolderMap[folderId]!!.setAlpha(seekBar.progress)
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}