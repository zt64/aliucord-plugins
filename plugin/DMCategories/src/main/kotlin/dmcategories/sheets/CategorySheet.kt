package dmcategories.sheets

import DMCategories
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.widgets.BottomSheet
import com.discord.utilities.color.ColorCompat
import com.lytefast.flexinput.R
import dmcategories.CategoryDialog
import dmcategories.DMCategory
import dmcategories.Util

class CategorySheet(private val category: DMCategory) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        fun textView(text: String, @StyleRes style: Int) = TextView(ctx, null, 0, style).apply {
            this.text = text
        }

        fun addAction(
            text: String,
            drawable: Int,
            tint: Int,
            onClick: () -> Unit
        ) {
            addView(textView(text, R.i.UiKit_Settings_Item_Icon).apply {
                setOnClickListener {
                    dismiss()
                    onClick()
                }
                setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(ctx, drawable)!!
                        .mutate()
                        .apply {
                            setTint(ColorCompat.getThemedColor(ctx, tint))
                        }, null, null, null
                )
            })
        }

        addView(textView(category.name, R.i.UiKit_Settings_Item_Header))


        addAction("Rename Category", R.e.ic_edit_24dp, R.b.colorInteractiveNormal) {
            CategoryDialog(category.name).show(parentFragmentManager, "EditCategory")
        }

        addAction("Delete Category", R.e.ic_delete_24dp, R.b.colorInfoDangerForeground) {
            Utils.showToast("Removed category: ${category.name}")

            DMCategories.removeCategory(category)

            Util.updateChannels()
        }
    }
}