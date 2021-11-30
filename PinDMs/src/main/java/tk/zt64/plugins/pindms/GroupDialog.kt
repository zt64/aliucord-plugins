package tk.zt64.plugins.pindms

import android.view.View
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog
import com.discord.stores.StoreStream
import tk.zt64.plugins.PinDMs

class GroupDialog(private val channelId: Long?, private val name: String? = null): InputDialog() {
    constructor(name: String) : this(null, name)

    override fun onViewBound(view: View) {
        if (name == null) {
            setTitle("Create Group")
            setDescription("Enter a name for the group")
        } else setTitle("Edit Group")

        setPlaceholderText("Name")

        setOnOkListener {
            val inputName = input.trim()

            if (inputName == name) return@setOnOkListener dismiss()

            if (name != null) {
                val group = PinDMs.getGroup(name)!!

                PinDMs.groups[PinDMs.groups.indexOf(group)] = DMGroup(input, group.channelIds)
                PinDMs.saveGroups()

                Utils.showToast("Renamed group")
            } else {
                Utils.showToast("Created group: $inputName")

                PinDMs.addGroup(inputName, arrayListOf(channelId!!))
            }

            StoreStream.`access$getDispatcher$p`(StoreStream.getPresences().stream).schedule {
                StoreStream.getChannels().markChanged()
            }

            dismiss()
        }

        super.onViewBound(view)

        if (name != null) {
            inputLayout.editText?.setText(name)
            body.visibility = View.GONE
        }
    }
}