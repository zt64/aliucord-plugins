import accountswitcher.SwitcherPage
import accountswitcher.getAccounts
import accountswitcher.settings.PluginSettings
import android.content.Context
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.PatcherAPI
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.instead
import com.discord.utilities.rest.RestAPI
import com.discord.widgets.settings.WidgetSettings
import de.robv.android.xposed.XC_MethodHook
import kotlin.properties.ReadOnlyProperty

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
class AccountSwitcher : Plugin() {
    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }

    companion object {
        lateinit var mSettings: SettingsAPI
    }

    @Suppress("SetTextI18n")
    override fun start(context: Context) {
        mSettings = settings

        patcher.instead<WidgetSettings>("showLogoutDialog", Context::class.java) {
            val accounts = getAccounts().apply {
                removeIf { it.token == RestAPI.AppHeadersProvider.INSTANCE.authToken }
            }

            Utils.openPageWithProxy(
                context = Utils.appActivity,
                fragment = SwitcherPage(accounts)
            )
        }

        // Eventually add a switcher button to the login page
        //        patcher.after<WidgetAuthLanding>("onViewBound", View::class.java), Hook {
        //            val ctx = (it.thisObject as WidgetAuthLanding).requireContext()
        //            val view = it.args[0] as RelativeLayouts
        //            val v = view.getChildAt(1) as LinearLayout
        //
        //            val padding = DimenUtils.dpToPx(18)
        //            Button(ctx).apply {
        //                text = "Open Account Switcher"
        //                textSize = 16.0f
        //                setPadding(0, padding, 0, padding)
        //                setOnClickListener { Utils.openPageWithProxy(Utils.appActivity, SwitcherPage(getAccounts())) }
        //
        //                if (StoreStream.getUserSettingsSystem().theme == "light")
        //                    setBackgroundColor(ctx.resources.getColor(R.c.uikit_btn_bg_color_selector_secondary_light, null))
        //                else
        //                    setBackgroundColor(ctx.resources.getColor(R.c.uikit_btn_bg_color_selector_secondary_dark, null))
        //
        //                v.addView(this)
        //            }
        //        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}

open class Arguments {
    var rawArguments: List<Any> = emptyList()
    var argumentClasses: Array<Class<*>> = arrayOf()

    private var size: Int = 0

    fun <T> argument(clazz: Class<T>): ReadOnlyProperty<Any?, T> {
        argumentClasses += clazz

        val greg = ReadOnlyProperty<Any?, T> { _, _ ->
            @Suppress("UNCHECKED_CAST")
            rawArguments.getOrNull(argumentClasses.lastIndex) as T
        }

        return greg
    }
}

class TestArgs : Arguments() {
    val test by argument(String::class.java)
}

inline fun <reified T : Any, reified R> PatcherAPI.a(
    method: String,
    arguments: () -> R,
    crossinline body: T.(param: XC_MethodHook.MethodHookParam, args: R) -> Unit
): Runnable where R : Arguments {
    val args = arguments()

    return after<T>(method, paramTypes = args.argumentClasses) {
        args.rawArguments = it.args.asList()
        body(it, args)
    }
}