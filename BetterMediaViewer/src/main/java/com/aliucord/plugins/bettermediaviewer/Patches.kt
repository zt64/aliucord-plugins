package com.aliucord.plugins.bettermediaviewer

import android.animation.ValueAnimator
import android.app.DownloadManager
import android.content.Context
import android.graphics.Matrix
import android.net.Uri
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import com.airbnb.lottie.parser.AnimatableValueParser
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.PatcherAPI
import com.aliucord.api.SettingsAPI
import com.aliucord.patcher.PineInsteadFn
import com.aliucord.patcher.PinePatchFn
import com.aliucord.plugins.BetterMediaViewer
import com.aliucord.utils.ReflectUtils
import com.aliucord.utils.RxUtils
import com.discord.databinding.WidgetMediaBinding
import com.discord.utilities.rx.ObservableExtensionsKt
import com.discord.widgets.media.WidgetMedia
import com.discord.widgets.media.`WidgetMedia$configureAndStartControlsAnimation$$inlined$apply$lambda$1`
import com.discord.widgets.media.`WidgetMedia$showControls$1`
import com.discord.widgets.media.`WidgetMedia$showControls$2`
import com.lytefast.flexinput.R
import top.canyie.pine.callback.MethodReplacement
import java.util.concurrent.TimeUnit

class Patches(private val patcher: PatcherAPI) {
    private var settingsAPI: SettingsAPI? = PluginManager.plugins["BetterMediaViewer"]?.settings
    private val logger = BetterMediaViewer.logger

    private var WidgetMedia.controlsAnimator
        get() = ReflectUtils.getField(this, "controlsAnimator") as ValueAnimator
        set(valueAnimator) = ReflectUtils.setField(this, "controlsAnimator", valueAnimator)

    private val WidgetMedia.mediaSource
        get() = ReflectUtils.getField(this, "mediaSource")

    private fun WidgetMedia.getBinding(): WidgetMediaBinding = WidgetMedia.`access$getBinding$p`(this)

    fun patchMenu() {
        val downloadManager = Utils.appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("onViewBoundOrOnResume"), PinePatchFn {
            val ctx = Utils.appContext
            val widgetMedia = it.thisObject as WidgetMedia
            with(Utils.appActivity.t.menu, {
//                findItem(Utils.getResId("menu_media_download", "id")).setOnMenuItemClickListener {
//                    val uri = Uri.parse(settingsAPI?.getString("downloadDir", ctx.getExternalFilesDir("Downloads")
//                        .toString()))
//
//                    try {
//                        val inputStream = ctx.contentResolver.openInputStream(uri)
//                    } catch (e: FileNotFoundException) {
//                        logger.error(e)
//                    }
//
//                    downloadManager.enqueue(downloadManager.Request(Uri.parse("http://speedtest.ftp.otenet.gr/files/test10Mb.db"))
//                            .setDestinationUri(uri)
//                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE))
//                    false
//                }
                findItem(Utils.getResId("menu_media_share", "id")).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

                with(Utils.getResId("menu_media_browser", "id"), {
                    if (settingsAPI!!.getBool("showOpenInBrowser", true)) {
                        findItem(this).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    } else {
                        removeItem(this)
                    }
                })

                add("More options").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS).setIcon(R.d.ic_overflow_dark_24dp)
                    .setOnMenuItemClickListener {
                        MediaSheet(widgetMedia).show(widgetMedia.parentFragmentManager, MediaSheet::class.java.name)
                        false
                    }
            })
        })
    }


    fun patchControls() {
        val configureAndStartControlsAnimationMethod = WidgetMedia::class.java.getDeclaredMethod("configureAndStartControlsAnimation", ValueAnimator::class.java)
            .apply { isAccessible = true }
        val getToolbarTranslationYMethod = WidgetMedia::class.java.getDeclaredMethod("getToolbarTranslationY")
            .apply { isAccessible = true }

        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("showControls"), PineInsteadFn {
            val widgetMedia = it.thisObject as WidgetMedia
            val binding = widgetMedia.getBinding()
            if (settingsAPI!!.getBool("autoHideControls", true)) {
                binding.f.h()
                WidgetMedia.`access$getControlsVisibilitySubscription$p`(widgetMedia)?.unsubscribe()
                val timer = RxUtils.timer(settingsAPI!!.getLong("controlsTimeout", 3000), TimeUnit.MILLISECONDS)
                ObservableExtensionsKt.`appSubscribe$default`(ObservableExtensionsKt.`ui$default`(timer, widgetMedia, null, 2, null), WidgetMedia::class.java, null as Context?, `WidgetMedia$showControls$1`(widgetMedia), null as Function1<*, *>?, null as Function0<*>?, null as Function0<*>?, `WidgetMedia$showControls$2`(widgetMedia), 58, null as Any?)
            } else {
                binding.f.c()
            }

            val controlsAnimationAction = WidgetMedia.`access$getControlsAnimationAction$p`(widgetMedia)
            val controlsAnimationAction2 = WidgetMedia.ControlsAnimationAction.SHOW
            if (controlsAnimationAction != controlsAnimationAction2) {
                ReflectUtils.setField(widgetMedia, "controlsAnimationAction", controlsAnimationAction2)
                widgetMedia.controlsAnimator.cancel()

                with(ValueAnimator.ofFloat((getToolbarTranslationYMethod.invoke(widgetMedia) as Float), 0.0f)) {
                    configureAndStartControlsAnimationMethod.invoke(widgetMedia, this)
                    widgetMedia.controlsAnimator = this
                }
            }
        })
    }

    fun patchImmersiveMode() {
        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("onViewBoundOrOnResume"), PinePatchFn {
            val root = WidgetMedia.`access$getBinding$p`(it.thisObject as WidgetMedia).root
            when (settingsAPI!!.getInt("immersiveModeType", 0)) {
                0 -> root.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
                1 -> root.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
                2 -> root.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
            }
        })
    }

    fun patchBackButton() {
        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("onViewBoundOrOnResume"), PinePatchFn {
            (it.thisObject as WidgetMedia).setActionBarDisplayHomeAsUpEnabled(false)
        })
    }

    fun patchToolbar() {
        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("onViewBoundOrOnResume"), PinePatchFn {
            with(it.thisObject as WidgetMedia) {
                if (mediaSource != null) {
                    (getBinding().b.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.BOTTOM
                }
            }
        })

        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("getToolbarTranslationY"), PinePatchFn {
            with(it.thisObject as WidgetMedia) {
                if (this.mediaSource == null) it.result = -this.getBinding().b.translationY
            }
        })

        patcher.patch(`WidgetMedia$configureAndStartControlsAnimation$$inlined$apply$lambda$1`::class.java.getDeclaredMethod("onAnimationUpdate", ValueAnimator::class.java), PineInsteadFn {
            val widgetMedia = (it.thisObject as `WidgetMedia$configureAndStartControlsAnimation$$inlined$apply$lambda$1`).`this$0`
            val floatValue = ((it.args[0] as ValueAnimator).animatedValue) as Float
            val binding = WidgetMedia.`access$getBinding$p`(widgetMedia)

            binding.b.apply {
                try {
                    translationY = if (widgetMedia.mediaSource != null) floatValue else -floatValue
                } catch (e: NoSuchFieldException) {
                    logger.error(e)
                } catch (e: IllegalAccessException) {
                    logger.error(e)
                }
            }
            if (WidgetMedia.`access$isVideo`(widgetMedia) && WidgetMedia.`access$getPlayerControlsHeight$p`(widgetMedia) > 0) {
                binding.f.apply {
                    translationY = -floatValue / (WidgetMedia.`access$getToolbarHeight$p`(widgetMedia)
                            .toFloat() / WidgetMedia.`access$getPlayerControlsHeight$p`(widgetMedia).toFloat())
                }
            }
        })
    }

    fun patchZoomLimit() {
        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("getFormattedUrl", Context::class.java, Uri::class.java), PinePatchFn {
            val res = it.result as String
            if (res.contains(".discordapp.net/")) {
                val arr = res.split("\\?").toTypedArray()
                it.result = arr[0] + if (arr[1].contains("format=")) "?format=" + arr[1].split("format=")
                    .toTypedArray()[1] else ""
            }
        })

        patcher.patch(c.f.l.b.c::class.java.getDeclaredMethod("f", Matrix::class.java, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Int::class.javaPrimitiveType), MethodReplacement.returnConstant(false))

        for (m in AnimatableValueParser::class.java.declaredMethods) {
            val params = m.parameterTypes
            if (params.size == 4 && params[0] == c.f.j.d.f::class.java && params[1] == c.f.j.d.e::class.java && params[3] == Int::class.javaPrimitiveType) {
                patcher.patch(m, MethodReplacement.returnConstant(1))
                break
            }
        }
    }
}