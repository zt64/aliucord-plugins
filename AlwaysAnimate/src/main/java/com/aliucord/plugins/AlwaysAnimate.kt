package com.aliucord.plugins

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.PowerManager
import android.widget.ImageView
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.patcher.PreHook
import com.aliucord.plugins.alwaysanimate.PluginSettings
import com.aliucord.utils.DimenUtils
import com.discord.models.presence.Presence
import com.discord.utilities.icon.IconUtils
import com.discord.utilities.images.MGImages
import com.discord.utilities.presence.PresenceUtils
import com.facebook.drawee.view.SimpleDraweeView

@AliucordPlugin
class AlwaysAnimate : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    override fun start(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        if (settings.getBool("batterySaver", false) && powerManager.isPowerSaveMode) return

        if (settings.getBool("guildIcons", true)) {
            patcher.patch(IconUtils::class.java.getDeclaredMethod("getForGuild", Long::class.javaObjectType, String::class.javaObjectType, String::class.javaObjectType, Boolean::class.java, Int::class.javaObjectType), PreHook {
                it.args[3] = true
            })
        }

        if (settings.getBool("avatars", true)) {
            patcher.patch(IconUtils::class.java.getDeclaredMethod("getForUser", Long::class.javaObjectType, String::class.javaObjectType, Int::class.javaObjectType, Boolean::class.javaPrimitiveType, Int::class.javaObjectType), PreHook {
                it.args[3] = true
            })

            patcher.patch(IconUtils::class.java.getDeclaredMethod("setIcon", ImageView::class.java, String::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType, Function1::class.java, MGImages.ChangeDetector::class.java), Hook {
                val simpleDraweeView = it.args[0] as SimpleDraweeView
                simpleDraweeView.apply {
                    clipToOutline = true
                    if (settings.getBool("roundedAvatars", true)) {
                        background =
                            ShapeDrawable(OvalShape()).apply { paint.color = Color.TRANSPARENT }
                    } else {
                        background =
                            GradientDrawable().apply { shape = GradientDrawable.RECTANGLE }.apply {
                                cornerRadius = DimenUtils.dpToPx(3).toFloat()
                                setColor(Color.TRANSPARENT)
                            }
                    }
                }
            })
        }

        if (settings.getBool("status", true)) {
            patcher.patch(PresenceUtils::class.java.getDeclaredMethod("getStatusDraweeSpanStringBuilder", Context::class.java, Presence::class.java, Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType), PreHook {
                it.args[5] = true
            })
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}