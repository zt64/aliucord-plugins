package accountswitcher.settings

import AccountSwitcher.Companion.accounts
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.provider.DocumentsContract
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Button
import com.discord.stores.StoreStream
import com.discord.utilities.rest.RestAPI
import com.lytefast.flexinput.R
import java.io.File

class PluginSettings : SettingsPage() {
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onViewBound(view: View) {
        super.onViewBound(view)

        val ctx = requireContext()
        val accountAdapter = AccountAdapter(this@PluginSettings)

        if (!::launcher.isInitialized) {
            launcher = requireActivity().registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
                if (res.resultCode == Activity.RESULT_OK) {
                    val uri = res.data?.data

                    if (uri == null) {
                        Utils.showToast("Import cancelled")
                    } else {
                        ctx.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        try {
                            ctx.contentResolver.openInputStream(uri)?.use { inputStream ->
                                val data = inputStream.bufferedReader().readText()
                                val importedCount = accounts.import(data)
                                Utils.showToast("Imported ${Utils.pluralise(importedCount, "account")} successfully")
                                @Suppress("NotifyDataSetChanged")
                                accountAdapter.notifyDataSetChanged()
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            Utils.showToast("Failed to import accounts. Is this a valid accounts json file?")
                        }
                    }
                }
            }
        }

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

            if (accounts.none { it.key == meId }) {
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

        addView(
            Button(ctx).apply {
                text = "Export"
                setBackgroundColor(
                    ResourcesCompat.getColor(
                        ctx.resources,
                        R.c.uikit_btn_bg_color_selector_secondary,
                        ctx.theme
                    )
                )
                setOnClickListener {
                    try {
                        val path = "${Constants.BASE_PATH}/accounts.json"
                        File(path).writeText(accounts.toJson())
                        Utils.showToast("Accounts exported to $path")
                    } catch (e: Throwable) {
                        Utils.showToast("Failed to export accounts: ${e.message}")
                    }
                }
            }
        )

        addView(
            Button(ctx).apply {
                text = "Import"
                setBackgroundColor(
                    ResourcesCompat.getColor(
                        ctx.resources,
                        R.c.uikit_btn_bg_color_selector_secondary,
                        ctx.theme
                    )
                )
                setOnClickListener {
                    launcher.launch(
                        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            addFlags(
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                                    Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                            )

                            putExtra(Intent.EXTRA_TITLE, "select file to import accounts from")

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                type = "application/json"
                                putExtra(DocumentsContract.EXTRA_INITIAL_URI, File(Constants.BASE_PATH).toURI())
                            } else {
                                type = "application/octet-stream"
                            }
                        }
                    )
                }
            }
        )
    }
}