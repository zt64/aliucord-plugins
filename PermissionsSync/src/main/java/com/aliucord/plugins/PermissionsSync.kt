package com.aliucord.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.utils.RxUtils.await
import com.aliucord.utils.RxUtils.subscribe
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.aliucord.wrappers.ChannelWrapper.Companion.name
import com.aliucord.wrappers.ChannelWrapper.Companion.parentId
import com.aliucord.wrappers.PermissionOverwriteWrapper.Companion.id
import com.aliucord.wrappers.PermissionOverwriteWrapper.Companion.type
import com.discord.api.channel.Channel
import com.discord.databinding.WidgetChannelSettingsPermissionsAdvancedBinding
import com.discord.restapi.RestAPIParams
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.utilities.rest.RestAPI
import com.discord.widgets.channels.permissions.WidgetChannelSettingsPermissionsAdvanced
import com.lytefast.flexinput.R

@AliucordPlugin
class PermissionsSync : Plugin() {
    private val Channel.permissionOverwrites
        get() = s()

    private val getBindingMethod = WidgetChannelSettingsPermissionsAdvanced::class.java.getDeclaredMethod("getViewBinding")
        .apply { isAccessible = true }

    private fun WidgetChannelSettingsPermissionsAdvanced.getBinding() = getBindingMethod(this) as WidgetChannelSettingsPermissionsAdvancedBinding

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        val textViewId = View.generateViewId()

        patcher.after<WidgetChannelSettingsPermissionsAdvanced>("configureUI", WidgetChannelSettingsPermissionsAdvanced.Model::class.java) {
            val model = it.args[0] as WidgetChannelSettingsPermissionsAdvanced.Model?
            if (model == null || !model.canManage || model.channel.parentId == 0L) return@after

            val binding = getBinding()
            val parent = ((binding.root as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0) as LinearLayout
            val ctx = parent.context

            val textView = parent.findViewById(textViewId) ?: TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                id = textViewId
                parent.addView(this, 0)
            }

            val categoryName = StoreStream.getChannels().getChannel(model.channel.parentId).name

            (WidgetChannelSettingsPermissionsAdvanced.Model.Companion).get(model.channel.parentId).subscribe {
                val parentModel = this
                val synced = model.memberItems == parentModel.memberItems && model.roleItems == parentModel.roleItems

                parentModel.channel.s()
                textView.apply {
                    val icon: Drawable?

                    if (synced) {
                        text = "Permissions synced with: $categoryName"
                        icon = ContextCompat.getDrawable(ctx, R.e.ic_info_24dp)?.mutate()?.apply {
                            setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
                        }
                    } else {
                        text = "Permissions not synced with: $categoryName\nTap to sync"
                        icon = ContextCompat.getDrawable(ctx, R.e.ic_warning_circle_24dp)?.mutate()?.apply {
                            setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
                        }

                        setOnClickListener {
                            isClickable = false

                            Utils.showToast("Syncing permissions")
                            Utils.threadPool.execute {
                                model.channel.permissionOverwrites.forEach { permissionOverwrite ->
                                    val th = RestAPI.api.deletePermissionOverwrites(model.channel.id, permissionOverwrite.id).await().second

                                    if (th != null) Utils.showToast("Failed to delete permission ${permissionOverwrite.type}: ${permissionOverwrite.id}")
                                }

                                parentModel.channel.permissionOverwrites.forEach { permissionOverwrite ->
                                    val th = RestAPI.api.updatePermissionOverwrites(
                                        model.channel.id,
                                        permissionOverwrite.id,
                                        (RestAPIParams.ChannelPermissionOverwrites.Companion).fromPermissionOverwrite(permissionOverwrite)
                                    ).await().second

                                    if (th != null) Utils.showToast("Failed to add permission ${permissionOverwrite.type}: ${permissionOverwrite.id}")
                                }

                                Utils.mainThread.post { WidgetChannelSettingsPermissionsAdvanced.`access$configureUI`(this@after, parentModel) }
                            }
                        }
                    }

                    typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium)

                    setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
                }
            }
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}