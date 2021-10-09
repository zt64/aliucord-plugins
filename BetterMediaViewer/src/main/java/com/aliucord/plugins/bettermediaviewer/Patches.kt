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
import androidx.appcompat.widget.Toolbar
import com.airbnb.lottie.parser.AnimatableValueParser
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.PatcherAPI
import com.aliucord.api.SettingsAPI
import com.aliucord.patcher.Hook
import com.aliucord.patcher.InsteadHook
import com.aliucord.utils.RxUtils
import com.discord.utilities.rx.ObservableExtensionsKt
import com.discord.widgets.media.WidgetMedia
import com.discord.widgets.media.`WidgetMedia$configureAndStartControlsAnimation$$inlined$apply$lambda$1`
import com.discord.widgets.media.`WidgetMedia$showControls$1`
import com.discord.widgets.media.`WidgetMedia$showControls$2`
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.material.appbar.AppBarLayout
import de.robv.android.xposed.XC_MethodReplacement
import java.util.concurrent.TimeUnit

class Patches(private val patcher: PatcherAPI) {
    private var settingsAPI: SettingsAPI = PluginManager.plugins["BetterMediaViewer"]?.settings!!

    fun patchMenu() {
        val downloadManager = Utils.appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val shareItemId = Utils.getResId("menu_media_share", "id")
        val browserItemId = Utils.getResId("menu_media_browser", "id")
        val appBarLayoutId = Utils.getResId("action_bar_toolbar_layout", "id")

        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("onViewBoundOrOnResume"), Hook {
            val ctx = Utils.appContext
            val widgetMedia = it.thisObject as WidgetMedia
            val menu = (widgetMedia.binding.root.findViewById<AppBarLayout>(appBarLayoutId).getChildAt(0) as Toolbar).menu

            with(menu, {
//                findItem(Utils.getResId("menu_media_download", "id")).setOnMenuItemClickListener {
//                    val uri = Uri.parse(settingsAPI?.getString("downloadDir", ctx.getExternalFilesDir("Downloads")
//                        .toString()))
//
//                    val inputStream = ctx.contentResolver.openInputStream(uri)
//
//                    downloadManager.enqueue(downloadManager.Request(Uri.parse("http://speedtest.ftp.otenet.gr/files/test10Mb.db"))
//                            .setDestinationUri(uri)
//                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE))
//                    false
//                }

                findItem(shareItemId)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                findItem(browserItemId)?.isVisible = settingsAPI.getBool("showOpenInBrowser", true)
            })
        })
    }

    fun patchControls() {
        val playerControlViewId = Utils.getResId("media_player_control_view", "id")
        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("showControls"), InsteadHook {
            with(it.thisObject as WidgetMedia) {
                if (settingsAPI.getBool("autoHideControls", true)) {
                    binding.root.findViewById<PlayerControlView>(playerControlViewId).h()
                    val timer = RxUtils.timer(settingsAPI.getInt("controlsTimeout", 3000).toLong(), TimeUnit.MILLISECONDS)
                    ObservableExtensionsKt.`appSubscribe$default`(ObservableExtensionsKt.`ui$default`(timer, this, null, 2, null), WidgetMedia::class.java, null, `WidgetMedia$showControls$1`(this), null, null, null, `WidgetMedia$showControls$2`(this), 58, null)
                }

                val controlsAnimationAction2 = WidgetMedia.ControlsAnimationAction.SHOW
                if (controlsAnimationAction != controlsAnimationAction2) {
                    controlsAnimationAction = controlsAnimationAction2
                    controlsAnimator?.cancel()

                    with(ValueAnimator.ofFloat(getToolbarTranslationY(), 0.0f)) {
                        configureAndStartControlsAnimation(this)
                        controlsAnimator = this
                    }
                }
            }
        })
    }

    @Suppress("DEPRECATION")
    fun patchImmersiveMode() {
        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("onViewBoundOrOnResume"), Hook {
            val root = (it.thisObject as WidgetMedia).binding.root
            root.systemUiVisibility = when (settingsAPI.getInt("immersiveModeType", 0)) {
                0 -> View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
                1 -> View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
                2 -> View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
                else -> return@Hook
            }
        })
    }

    fun patchBackButton() {
        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("onViewBoundOrOnResume"), Hook {
            (it.thisObject as WidgetMedia).setActionBarDisplayHomeAsUpEnabled(false)
        })
    }

    fun patchToolbar() {
        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("onViewBoundOrOnResume"), Hook {
            with(it.thisObject as WidgetMedia) {
                if (mediaSource != null) return@Hook
                (binding.b.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.BOTTOM
            }
        })

        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("getToolbarTranslationY"), Hook {
            with(it.thisObject as WidgetMedia) {
                if (mediaSource == null) it.result = -binding.b.translationY
            }
        })

        patcher.patch(`WidgetMedia$configureAndStartControlsAnimation$$inlined$apply$lambda$1`::class.java.getDeclaredMethod("onAnimationUpdate", ValueAnimator::class.java), InsteadHook {
            val widgetMedia = (it.thisObject as `WidgetMedia$configureAndStartControlsAnimation$$inlined$apply$lambda$1`).`this$0`
            val floatValue = ((it.args[0] as ValueAnimator).animatedValue) as Float
            val binding = widgetMedia.binding

            binding.b.translationY = if (widgetMedia.mediaSource != null) floatValue else -floatValue

            if (widgetMedia.isVideo() && widgetMedia.playerControlsHeight > 0) {
                binding.f.translationY = -floatValue / (widgetMedia.toolbarHeight.toFloat() / widgetMedia.playerControlsHeight.toFloat())
            }
        })
    }

    fun patchZoomLimit() {
        patcher.patch(WidgetMedia::class.java.getDeclaredMethod("getFormattedUrl", Context::class.java, Uri::class.java), Hook {
            val res = it.result as String
            if (res.contains(".discordapp.net/")) {
                val arr = res.split("\\?").toTypedArray()

                it.result = arr[0] + if (arr[1].contains("format=")) "?format=" + arr[1].split("format=").toTypedArray()[1] else ""
            }
        })

        patcher.patch(c.f.l.b.c::class.java.getDeclaredMethod("f", Matrix::class.java, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Int::class.javaPrimitiveType), XC_MethodReplacement.returnConstant(false))

        for (m in AnimatableValueParser::class.java.declaredMethods) {
            val params = m.parameterTypes
            if (params.size == 4 && params[0] == c.f.j.d.f::class.java && params[1] == c.f.j.d.e::class.java && params[3] == Int::class.javaPrimitiveType) {
                patcher.patch(m, XC_MethodReplacement.returnConstant(1))
                break
            }
        }
    }
}