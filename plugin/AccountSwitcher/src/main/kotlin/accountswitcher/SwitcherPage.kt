package accountswitcher

import AccountSwitcher.Companion.accounts
import accountswitcher.settings.AccountAdapter
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Button
import com.discord.stores.StoreStream
import com.lytefast.flexinput.R

class SwitcherPage : SettingsPage() {
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Account Switcher")
        setActionBarSubtitle(Utils.pluralise(accounts.size, "account"))

        val ctx = requireContext()
        // Enable once theres a way to immediately reflect changes made in settings
        // headerBar.menu
        //     .add("Settings")
        //     .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
        //     .setIcon(
        //         Utils.tintToTheme(
        //             AppCompatResources
        //                 .getDrawable(ctx, R.e.ic_settings_24dp)!!
        //                 .mutate()
        //         )
        //     )
        //     .setOnMenuItemClickListener {
        //         Utils.openPageWithProxy(ctx, PluginSettings())
        //         false
        //     }

        RecyclerView(ctx).apply {
            adapter = AccountAdapter(this@SwitcherPage, false)
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
                text = "Add accounts by opening the plugin settings and pressing the 'Add Account' button."
            }
        )

        if (StoreStream.getAuthentication().isAuthed) {
            addView(
                Button(ctx).apply {
                    text = "Log Out"
                    setBackgroundColor(
                        ResourcesCompat.getColor(
                            ctx.resources,
                            R.c.uikit_btn_bg_color_selector_red,
                            ctx.theme
                        )
                    )
                    setOnClickListener { StoreStream.getAuthentication().setAuthed(null) }
                }
            )
        }
    }
}