package com.aliucord.plugins

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.aliucord.PluginManager
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.aliucord.patcher.PinePrePatchFn
import com.aliucord.plugins.alwaysanimate.PluginSettings
import com.discord.utilities.icon.IconUtils
import com.discord.utilities.images.MGImages
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R

@AliucordPlugin
class AlwaysAnimate : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun start(context: Context) {
        if (settings.getBool("guildIcons", true)) {
            patcher.patch(IconUtils::class.java.getDeclaredMethod("getForGuild", Long::class.javaObjectType, String::class.javaObjectType, String::class.javaObjectType, Boolean::class.java, Int::class.javaObjectType), PinePrePatchFn {
                it.args[3] = true
            })
        }

        if (settings.getBool("avatars", true)) {
            patcher.patch(IconUtils::class.java.getDeclaredMethod("getForUser", Long::class.javaObjectType, String::class.javaObjectType, Int::class.javaObjectType, Boolean::class.javaPrimitiveType, Int::class.javaObjectType), PinePrePatchFn {
                it.args[3] = true
            })

            patcher.patch(IconUtils::class.java.getDeclaredMethod("setIcon", ImageView::class.java, String::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType, Function1::class.java, MGImages.ChangeDetector::class.java), PinePatchFn {
                if (PluginManager.isPluginEnabled("SquareAvatars")) return@PinePatchFn

                val simpleDraweeView = it.args[0] as SimpleDraweeView
                simpleDraweeView.apply {
                    background = ContextCompat.getDrawable(context, R.d.drawable_circle_transparent)
                    clipToOutline = true
                }
            })
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}