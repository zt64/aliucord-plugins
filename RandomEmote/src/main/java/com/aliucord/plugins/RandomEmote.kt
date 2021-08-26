package com.aliucord.plugins

import android.content.Context
import android.os.Bundle
import android.view.View
import com.aliucord.Utils
import com.aliucord.entities.Plugin
import com.aliucord.entities.Plugin.Manifest.Author
import com.aliucord.patcher.PinePatchFn
import com.aliucord.utils.RxUtils.createActionSubscriber
import com.aliucord.utils.RxUtils.subscribe
import com.discord.models.domain.emoji.ModelEmojiUnicode
import com.discord.restapi.RestAPIParams
import com.discord.stores.StoreStream
import com.discord.utilities.rest.RestAPI
import com.lytefast.flexinput.fragment.FlexInputFragment
import java.util.*
import java.util.stream.Collectors

class RandomEmote : Plugin() {
    override fun getManifest(): Manifest {
        return Manifest().apply {
            authors = arrayOf(Author("zt", 289556910426816513L))
            description = "Long press on the emoji button will send a random emote."
            version = "1.0.0"
            updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json"
        }
    }

    override fun start(context: Context) {
        val rand = Random()
        val emojis = StoreStream.getEmojis().unicodeEmojisNamesMap.values.stream()
            .map { obj: ModelEmojiUnicode -> obj.reactionKey }
            .collect(Collectors.toList())

        patcher.patch(FlexInputFragment::class.java.getDeclaredMethod("onViewCreated", View::class.java, Bundle::class.java), PinePatchFn {
            (it.thisObject as FlexInputFragment).j().i.setOnLongClickListener {
                val message = RestAPIParams.Message(emojis[rand.nextInt(emojis.size)], null, null, null, null, null, null)
                RestAPI.api.sendMessage(StoreStream.getChannelsSelected().id, message).subscribe(createActionSubscriber(onNext = {
                    Utils.showToast(context, "send message")
                }))
                true
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}