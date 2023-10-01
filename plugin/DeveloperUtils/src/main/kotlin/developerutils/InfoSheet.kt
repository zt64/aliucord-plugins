@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.aliucord.PluginManager.logger
import com.aliucord.Utils
import com.aliucord.views.Divider
import com.aliucord.widgets.BottomSheet
import com.aliucord.widgets.LinearLayout
import com.lytefast.flexinput.R

class InfoSheet(private val tappedView: View) : BottomSheet() {
    @Suppress("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        fun addField(title: String, value: String): LinearLayout = LinearLayout(ctx).apply {
            this.addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Label).apply { text = "$title: " })
            this.addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Addition).apply { text = value })

            setOnLongClickListener {
                copyToClipboard(value)
                Utils.showToast("Copied to clipboard")
                true
            }

            linearLayout.addView(this)
        }

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "DeveloperUtils"
        })

        addField("Class", tappedView::class.java.name)

        if (tappedView.id != View.NO_ID) addField("Resource Name", tappedView.resources.getResourceEntryName(tappedView.id))

        // View specific stuff
        if (tappedView is TextView) {

        } else if (tappedView is ImageView) {
            try {
                val field = ImageView::class.java.getDeclaredField("mResource").apply { isAccessible = true }

                field[tappedView].let {
                    if (it != 0) addField("Image Resource ID", tappedView.resources.getResourceEntryName(it as Int))
                }
            } catch (e: Throwable) {
                logger.error(e)
            }
        }

        addView(Divider(ctx))

        //        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
        //            text = "Output To Log"
        //            setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
        //            setOnClickListener {
        //                Utils.showToast("Logged view")
        //            }
        //        })
    }

    private fun copyToClipboard(text: String) = Utils.setClipboard("DeveloperUtils", text)
}