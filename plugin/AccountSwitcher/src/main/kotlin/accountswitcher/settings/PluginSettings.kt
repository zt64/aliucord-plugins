@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS", "MISSING_DEPENDENCY_SUPERCLASS_WARNING")

package accountswitcher.settings

import AccountSwitcher.Companion.accounts
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Button
import com.discord.stores.StoreStream
import com.discord.utilities.rest.RestAPI
import com.lytefast.flexinput.R

class PluginSettings : SettingsPage() {
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        val ctx = requireContext()
        val accountAdapter = AccountAdapter(this@PluginSettings)

        setActionBarTitle("Account Switcher")
        setActionBarSubtitle("Settings")

        RecyclerView(ctx).apply {
            adapter = accountAdapter
            layoutManager = LinearLayoutManager(ctx)
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                weight = 1f
            }

            addItemDecoration(
                DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL).apply {
                    setDrawable(
                        ShapeDrawable(RectShape()).apply {
                            intrinsicHeight = DimenUtils.defaultPadding
                            setTint(Color.TRANSPARENT)
                        }
                    )
                }
            )

            linearLayout.addView(this)
        }

        linearLayout.addView(
            TextView(ctx, null, 0, R.i.UiKit_Settings_Item_SubText).apply {
                text = "Access the switcher by pressing the logout button on the main settings page."
            }
        )

        addView(
            Button(ctx).apply {
                text = "Add Account"
                setOnClickListener {
                    AccountDialog(accountAdapter).show(parentFragmentManager, "Add Account")
                }
            }
        )

        if (StoreStream.getAuthentication().isAuthed) {
            val meId = StoreStream.getUsers().me.id

            if (accounts.any { it.key == meId }) return

            addView(
                Button(ctx).apply {
                    text = "Add Current Account"
                    setOnClickListener {
                        try {
                            if (accountAdapter.addAccount(RestAPI.AppHeadersProvider.INSTANCE.authToken, meId) == null) {
                                Utils.showToast("Added current account")
                            } else {
                                Utils.showToast("Failed to add current account")
                            }
                        } catch (e: Throwable) {
                            Utils.showToast("Failed to add current account: ${e.message}")
                        }

                        linearLayout.removeView(this)
                    }
                }
            )
        }
    }
}