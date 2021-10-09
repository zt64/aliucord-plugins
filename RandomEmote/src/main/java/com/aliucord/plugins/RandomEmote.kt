package com.aliucord.plugins

import android.content.Context
import android.os.Bundle
import android.view.View
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.utils.RxUtils.createActionSubscriber
import com.aliucord.utils.RxUtils.subscribe
import com.discord.models.domain.NonceGenerator
import com.discord.restapi.RestAPIParams
import com.discord.stores.StoreStream
import com.discord.utilities.rest.RestAPI
import com.discord.utilities.time.ClockFactory
import com.lytefast.flexinput.fragment.FlexInputFragment
import java.util.*
import java.util.stream.Collectors

@AliucordPlugin
class RandomEmote : Plugin() {
    override fun start(context: Context) {
        val rand = Random()
        val emojis = StoreStream.getEmojis().unicodeEmojisNamesMap.values.stream()
            .map { it.reactionKey }
            .collect(Collectors.toList())

        patcher.patch(FlexInputFragment::class.java.getDeclaredMethod("onViewCreated", View::class.java, Bundle::class.java), Hook {
            (it.thisObject as FlexInputFragment).j().i.setOnLongClickListener {
                val message = RestAPIParams.Message(
                        emojis[rand.nextInt(emojis.size)],
                        NonceGenerator.computeNonce(ClockFactory.get()).toString(),
                        null,
                        null,
                        emptyList(),
                        null,
                        RestAPIParams.Message.AllowedMentions(
                                emptyList(),
                                emptyList(),
                                emptyList(),
                                false
                        )
                )

                Utils.threadPool.execute {
                    RestAPI.api.sendMessage(StoreStream.getChannelsSelected().id, message).subscribe(createActionSubscriber({ }))
                }

                true
            }
        })
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}