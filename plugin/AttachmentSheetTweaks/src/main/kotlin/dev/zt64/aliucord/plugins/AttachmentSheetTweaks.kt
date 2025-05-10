package dev.zt64.aliucord.plugins

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R
import com.lytefast.flexinput.fragment.FlexInputFragment
import b.b.a.a.a as AddContentDialogFragment

@AliucordPlugin
class AttachmentSheetTweaks : Plugin() {
    override fun start(context: Context) {
        lateinit var textView: TextView

        patcher.after<AddContentDialogFragment>(
            "onCreateView",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Bundle::class.java
        ) {
            val root = it.result!! as CoordinatorLayout

            textView = TextView(root.context, null, 0, R.i.UiKit_TextView_Medium).apply {
                layoutParams = CoordinatorLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    anchorId = R.f.content_pager
                    anchorGravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                    setPadding(0, 0, 0, 20.dp)
                }
            }

            root.addView(textView)
        }

        patcher.after<b.b.a.a.d>("run") {
            val selectionAggregator = j.o

            textView.visibility = if (selectionAggregator.size > 0) View.VISIBLE else View.GONE
            textView.text = "${selectionAggregator.size} selected"
        }

        val m = FlexInputFragment::class.java
            .getDeclaredMethod("g", FlexInputFragment::class.java, Int::class.java)

        // patcher.patch(
        //     m,
        //     InsteadHook {
        //         val fragment = it.args[0] as FlexInputFragment
        //
        //         val transaction = fragment.getChildFragmentManager().beginTransaction()
        //
        //         val sheet = B()
        //
        //         sheet.show(transaction, "Add content")
        //     }
        // )
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
class B : BottomSheet() {
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        addView(
            Utils
                .createCheckedSetting(
                    context = ctx,
                    type = CheckedSetting.ViewType.SWITCH,
                    text = "Reverse",
                    subtext = "Whether the counter goes in reverse, counting down how many chars remain"
                ).apply {
                    isChecked = false
                }
        )
    }
}