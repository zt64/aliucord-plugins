package com.aliucord.plugins.bettermediaviewer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.plugins.BetterMediaViewer
import com.aliucord.utils.ReflectUtils
import com.aliucord.views.Divider
import com.aliucord.widgets.BottomSheet
import com.discord.utilities.color.ColorCompat
import com.discord.utilities.uri.UriHandler
import com.discord.widgets.media.WidgetMedia
import com.google.android.material.snackbar.Snackbar
import com.lytefast.flexinput.R

class MediaSheet(private val widgetMedia: WidgetMedia) : BottomSheet() {
    private val logger = BetterMediaViewer.logger

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        addView(createTextView(ctx, "Copy URL", R.e.ic_link_white_24dp) {
            val parse = Uri.parse(widgetMedia.mostRecentIntent.getStringExtra("INTENT_MEDIA_URL"))
            Snackbar.make(widgetMedia.requireView(), "Copied to clipboard", Snackbar.LENGTH_SHORT).setAction("Dismiss") {
                dismiss()
            }.show()
            Utils.setClipboard("Media link", parse.toString()).also { dismiss() }
        })
        if (widgetMedia.isVideo()) addView(createTextView(ctx, "Google image search", R.e.ic_search_white_24dp) {
            val parse = Uri.parse(widgetMedia.mostRecentIntent.getStringExtra("INTENT_MEDIA_URL"))
            UriHandler.`handleOrUntrusted$default`(it.context, "https://www.google.com/searchbyimage?site=search&sa=X&image_url=$parse", null, 4, null)
        })
        addView(Divider(ctx))
        addView(createTextView(ctx, "BetterMediaViewer Settings", R.e.ic_settings_24dp) {
            with(PluginManager.plugins["BetterMediaViewer"]!!) {
                try {
                    Utils.openPageWithProxy(view.context, ReflectUtils.invokeConstructorWithArgs(settingsTab.page, settings))
                } catch (e: Throwable) {
                    logger.error(e)
                }
            }
        })
    }

    private fun createTextView(ctx: Context, buttonText: String, resID: Int, onClickListener: View.OnClickListener): TextView {
        val icon = ContextCompat.getDrawable(ctx, resID)?.mutate()!!.apply {
            setTint(ColorCompat.getThemedColor(requireContext(), R.b.colorInteractiveNormal))
        }

        return TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
            text = text
            setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
            setOnClickListener(onClickListener)
        }
    }
}