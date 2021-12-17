package tk.zt64.plugins.dmcategories.sheets

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.widgets.BottomSheet
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.lytefast.flexinput.R
import tk.zt64.plugins.DMCategories
import tk.zt64.plugins.dmcategories.DMCategory
import tk.zt64.plugins.dmcategories.CategoryDialog
import tk.zt64.plugins.dmcategories.Util

class CategorySheet(private val category: DMCategory) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = category.name
        })

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
            text = "Rename Category"
            setOnClickListener {
                dismiss()
                CategoryDialog(category.name).show(parentFragmentManager, "EditCategory")
            }
            setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.e.ic_edit_24dp)!!.mutate().apply {
                setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
            }, null, null, null)
        })

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
            text = "Delete Category"
            setOnClickListener {
                dismiss()
                Utils.showToast("Removed category: ${category.name}")

                DMCategories.removeCategory(category)

                Util.updateChannels()
            }
            setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.e.ic_delete_24dp)!!.mutate().apply {
                setTint(ColorCompat.getThemedColor(ctx, R.b.colorInfoDangerForeground))
            }, null, null, null)
        })
    }
}