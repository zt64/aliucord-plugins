package tk.zt64.plugins.pindms.sheets

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
import tk.zt64.plugins.PinDMs
import tk.zt64.plugins.pindms.DMGroup
import tk.zt64.plugins.pindms.GroupDialog

class GroupSheet(private val group: DMGroup) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = group.name
        })

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
            text = "Rename Group"
            setOnClickListener {
                dismiss()
                GroupDialog(group.name).show(Utils.appActivity.supportFragmentManager, "EditGroup")
            }
            setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.e.ic_edit_24dp)!!.mutate().apply {
                setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
            }, null, null, null)
        })

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
            text = "Delete Group"
            setOnClickListener {
                dismiss()
                Utils.showToast("Deleted ${group.name}")

                PinDMs.groups.remove(group)
                PinDMs.saveGroups()

                PinDMs.removeGroup(group)
                StoreStream.`access$getDispatcher$p`(StoreStream.getPresences().stream).schedule {
                    StoreStream.getMessagesMostRecent().markChanged()
                }
            }
            setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(ctx, R.e.ic_delete_24dp)!!.mutate().apply {
                setTint(ColorCompat.getThemedColor(ctx, R.b.colorInfoDangerForeground))
            }, null, null, null)
        })
    }
}