
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.views.Divider
import com.discord.models.domain.emoji.ModelEmojiCustom
import com.discord.models.domain.emoji.ModelEmojiUnicode
import com.discord.stores.StoreStream
import com.discord.widgets.user.WidgetUserSetCustomStatus
import com.discord.widgets.user.WidgetUserSetCustomStatusViewModel
import com.discord.widgets.user.profile.UserStatusPresenceCustomView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.reflect.TypeToken
import com.lytefast.flexinput.R
import customstatuspresets.PresetAdapter

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
class CustomStatusPresets : Plugin() {
    private val presetType = TypeToken
        .getParameterized(
            ArrayList::class.java,
            UserStatusPresenceCustomView.ViewState.WithStatus::class.javaObjectType
        ).getType()

    companion object {
        lateinit var mSettings: SettingsAPI
    }

    @Suppress("SetTextI18n")
    override fun start(context: Context) {
        mSettings = settings

        val statusExpirationId = Utils.getResId("set_custom_status_expiration", "id")
        val saveButtonId = Utils.getResId("set_custom_status_save", "id")

        patcher.after<WidgetUserSetCustomStatus>(
            "onViewBound",
            View::class.java
        ) { (_, rootView: CoordinatorLayout) ->
            val presetAdapter =
                PresetAdapter(this, settings.getObject("presets", ArrayList(), presetType))

            with(rootView.findViewById<RadioGroup>(statusExpirationId).parent as LinearLayout) {
                val ctx = this.context

                addView(Divider(ctx))
                addView(
                    TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
                        text = "Presets"
                    }
                )
                addView(
                    RecyclerView(ctx).apply {
                        adapter = presetAdapter
                        layoutManager = LinearLayoutManager(ctx)
                        layoutParams =
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                250.dp
                            )
                    }
                )
            }

            rootView.findViewById<FloatingActionButton>(saveButtonId).setOnLongClickListener {
                val formState =
                    (
                        WidgetUserSetCustomStatus
                            .`access$getViewModel$p`(
                                this
                            ).viewState as WidgetUserSetCustomStatusViewModel.ViewState.Loaded
                    ).formState

                if (formState.emoji == null &&
                    formState.text.isEmpty()
                ) {
                    return@setOnLongClickListener false
                }

                val emoji = when (formState.emoji) {
                    is ModelEmojiUnicode -> UserStatusPresenceCustomView.Emoji(
                        null,
                        (formState.emoji as ModelEmojiUnicode).surrogates,
                        false
                    )

                    is ModelEmojiCustom ->
                        StoreStream
                            .getEmojis()
                            .getCustomEmojiInternal(formState.emoji.uniqueId.toLong())
                            .let { emoji ->
                                UserStatusPresenceCustomView.Emoji(
                                    emoji.id.toString(),
                                    emoji.name,
                                    emoji.isAnimated
                                )
                            }

                    else -> null
                }

                Utils.showToast("Added Current Status")

                presetAdapter.addPreset(
                    UserStatusPresenceCustomView.ViewState.WithStatus(
                        emoji,
                        formState.text
                    )
                )
                presetAdapter.notifyItemInserted(presetAdapter.itemCount)
                true
            }

            // Hide the Wumpus image
            rootView.getChildAt(2).visibility = View.GONE
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}