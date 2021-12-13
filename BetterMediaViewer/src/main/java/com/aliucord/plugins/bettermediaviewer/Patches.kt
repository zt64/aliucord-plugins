package com.aliucord.plugins.bettermediaviewer

import android.animation.ValueAnimator
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.PatcherAPI
import com.aliucord.api.SettingsAPI
import com.aliucord.patcher.InsteadHook
import com.aliucord.patcher.after
import com.aliucord.patcher.before
import com.aliucord.patcher.instead
import com.aliucord.utils.RxUtils
import com.discord.utilities.rx.ObservableExtensionsKt
import com.discord.widgets.media.WidgetMedia
import com.discord.widgets.media.`WidgetMedia$configureAndStartControlsAnimation$$inlined$apply$lambda$1`
import com.discord.widgets.media.`WidgetMedia$showControls$1`
import com.discord.widgets.media.`WidgetMedia$showControls$2`
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.material.appbar.AppBarLayout
import com.lytefast.flexinput.R
import java.util.concurrent.TimeUnit

object Patches {
    private var settings: SettingsAPI = PluginManager.plugins["BetterMediaViewer"]?.settings!!

    fun PatcherAPI.patchWidget() {
        val downloadManager = Utils.appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadItemId = Utils.getResId("menu_media_download", "id")
        val browserItemId = Utils.getResId("menu_media_browser", "id")

        after<WidgetMedia>("onViewBoundOrOnResume") {
            val toolbar = Utils.appActivity.findViewById<Toolbar>(R.f.action_bar_toolbar)

            with(toolbar.menu) {
                findItem(downloadItemId).setOnMenuItemClickListener {
                    val uri = if (isVideo()) mediaSource?.j else Uri.parse(mostRecentIntent.getStringExtra("INTENT_IMAGE_URL"))
                    val title = mostRecentIntent.getStringExtra("INTENT_TITLE")

                    val request = DownloadManager.Request(uri)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setTitle(title)
                        .setDestinationUri(Uri.withAppendedPath(Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), title))

                    Utils.threadPool.execute {
                        val reference: Long = downloadManager.enqueue(request)

                        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
                            override fun onReceive(context: Context, intent: Intent) {
                                if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == reference) {
                                    Utils.showToast("Finished downloading $title")
                                    Utils.appActivity.unregisterReceiver(this)
                                }
                            }
                        }

                        Utils.appActivity.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                    }

                    true
                }

                findItem(browserItemId)?.isVisible = settings.getBool("showOpenInBrowser", true)
            }

            // immersive mode
            if (settings.getBool("immersiveModeState", false))
                WindowInsetsControllerCompat(Utils.appActivity.window, binding.root).hide(
                        when (settings.getInt("immersiveModeType", 0)) {
                            0 -> WindowInsetsCompat.Type.statusBars()
                            1 -> WindowInsetsCompat.Type.navigationBars()
                            2 -> WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.statusBars()
                            else -> return@after
                        }
                )

            // hide back button
            if (settings.getBool("hideBackButton", false)) setActionBarDisplayHomeAsUpEnabled(false)
            if (settings.getBool("bottomToolbar", false) && !isVideo())
                (binding.root.findViewById<AppBarLayout>(R.f.action_bar_toolbar_layout).layoutParams as FrameLayout.LayoutParams).gravity = Gravity.BOTTOM
        }
    }

    fun PatcherAPI.patchControls() {
        val playerControlViewId = Utils.getResId("media_player_control_view", "id")

        patch(WidgetMedia::class.java.getDeclaredMethod("showControls"), InsteadHook {
            with(it.thisObject as WidgetMedia) {
                if (settings.getBool("autoHideControls", true)) {
                    binding.root.findViewById<PlayerControlView>(playerControlViewId).i()
                    controlsVisibilitySubscription?.unsubscribe()

                    val timer = RxUtils.timer(settings.getInt("controlsTimeout", 3000)
                        .toLong(), TimeUnit.MILLISECONDS)
                    ObservableExtensionsKt.appSubscribe(ObservableExtensionsKt.ui(timer, this, null), WidgetMedia::class.java, null, `WidgetMedia$showControls$1`(this), null, { }, { }, `WidgetMedia$showControls$2`(this))
                }

                if (controlsAnimationAction == WidgetMedia.ControlsAnimationAction.SHOW) return@with
                controlsAnimationAction = WidgetMedia.ControlsAnimationAction.SHOW
                controlsAnimator?.cancel()

                controlsAnimator = ValueAnimator.ofFloat(getToolbarTranslationY(), 0.0f)
                    .also { animator ->
                        configureAndStartControlsAnimation(animator)
                    }
            }
        })
    }

    fun PatcherAPI.patchToolbar() {
        val actionBarId = Utils.getResId("action_bar_toolbar_layout", "id")
        val mediaPlayerViewId = Utils.getResId("media_player_control_view", "id")

        after<WidgetMedia>("getToolbarTranslationY") {
            if (mediaSource == null) it.result = -binding.root.findViewById<AppBarLayout>(actionBarId).translationY
        }

        before<`WidgetMedia$configureAndStartControlsAnimation$$inlined$apply$lambda$1`>("onAnimationUpdate", ValueAnimator::class.java) {
            val widgetMedia = `this$0`
            val floatValue = ((it.args[0] as ValueAnimator).animatedValue) as Float
            val root = widgetMedia.binding.root

            root.findViewById<AppBarLayout>(actionBarId).translationY = if (widgetMedia.mediaSource != null) floatValue else -floatValue

            if (widgetMedia.isVideo() && widgetMedia.playerControlsHeight > 0) root.findViewById<PlayerControlView>(mediaPlayerViewId).translationY =
                    -floatValue / (widgetMedia.toolbarHeight.toFloat() / widgetMedia.playerControlsHeight.toFloat())
        }
    }

    fun PatcherAPI.patchZoomLimit() {
        patch(WidgetMedia::class.java.getDeclaredMethod("getFormattedUrl", Context::class.java, Uri::class.java)) {
            val res = it.result as String
            if (res.contains(".discordapp.net/")) {
                val arr = res.split("\\?").toTypedArray()

                it.result = "$arr[0]${
                    if ("format=" in arr[1]) "?format=${
                        arr[1].split("format=")
                            .toTypedArray()[1]
                    }" else ""
                }"
            }
        }


        instead<c.f.l.b.c>("f", Matrix::class.java, Float::class.javaPrimitiveType!!, Float::class.javaPrimitiveType!!, Int::class.javaPrimitiveType!!) { false }

        for (m in c.c.a.a0.d::class.java.declaredMethods) {
            val params = m.parameterTypes
            if (params.size == 4 && params[0] == c.f.j.d.f::class.java && params[1] == c.f.j.d.e::class.java && params[3] == Int::class.javaPrimitiveType) {
                patch(m, InsteadHook.returnConstant(1))
                break
            }
        }
    }
}