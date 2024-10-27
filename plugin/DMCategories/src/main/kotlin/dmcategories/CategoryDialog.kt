package dmcategories

import DMCategories
import android.view.View
import com.aliucord.Utils
import com.aliucord.fragments.InputDialog

class CategoryDialog(private val channelId: Long?, private val name: String? = null) : InputDialog() {
    constructor(name: String) : this(null, name)

    override fun onViewBound(view: View) {
        if (name == null) {
            setTitle("Create Category")
            setDescription("Enter a name for the category")
        } else {
            setTitle("Edit Category")
        }

        setPlaceholderText("Name")

        setOnOkListener {
            val inputName = input.trim()

            if (inputName == name) return@setOnOkListener dismiss()

            if (name != null) {
                val category = DMCategories.getCategory(name)!!

                DMCategories.categories[DMCategories.categories.indexOf(category)] = DMCategory(
                    Util.getCurrentId(),
                    input,
                    category.channelIds
                )
                DMCategories.saveCategories()

                Utils.showToast("Renamed category")
            } else {
                Utils.showToast("Created category: $inputName")

                DMCategories.addCategory(inputName, arrayListOf(channelId!!))
            }

            Util.updateChannels()

            dismiss()
        }

        super.onViewBound(view)

        if (name != null) {
            inputLayout.editText?.setText(name)
            body.visibility = View.GONE
        }
    }
}