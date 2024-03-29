import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.aliucord.patcher.instead
import com.discord.widgets.chat.input.AppFlexInputViewModel
import com.discord.widgets.chat.input.gifpicker.*

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
class AppendGifAsUrl : Plugin() {
    override fun start(context: Context) {
        lateinit var appFlexInputViewModel: AppFlexInputViewModel

        patcher.patch(AppFlexInputViewModel::class.java.constructors.first()) {
            appFlexInputViewModel = it.thisObject as AppFlexInputViewModel
        }

        patcher.instead<`WidgetGifPickerSearch$setUpGifRecycler$1`>(
            "invoke",
            GifAdapterItem.GifItem::class.java
        ) { (_, gifItem: GifAdapterItem.GifItem) ->
            appFlexInputViewModel.onInputTextAppended("${gifItem.gif.tenorGifUrl} ")
            WidgetGifPickerSearch.`access$getOnGifSelected$p`(`this$0`).invoke()

            return@instead null
        }

        patcher.instead<WidgetGifCategory>(
            "selectGif",
            GifAdapterItem.GifItem::class.java
        ) { (_, gifItem: GifAdapterItem.GifItem) ->
            appFlexInputViewModel.onInputTextAppended("${gifItem.gif.tenorGifUrl} ")

            return@instead null
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}