@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package developerutils

import android.view.View
import android.view.ViewGroup
import com.aliucord.fragments.SettingsPage

class InspectPage(private val rootView: ViewGroup) : SettingsPage() {
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        rootView.isEnabled = false
//        addView(rootView)
    }
}