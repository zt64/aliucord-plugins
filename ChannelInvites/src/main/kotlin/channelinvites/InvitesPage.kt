package channelinvites

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.wrappers.ChannelWrapper.Companion.guildId
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.aliucord.wrappers.ChannelWrapper.Companion.name
import com.discord.api.channel.Channel
import com.discord.app.AppFragment
import com.discord.app.AppViewFlipper
import com.discord.models.deserialization.gson.InboundGatewayGsonParser
import com.discord.models.domain.ModelInvite
import com.discord.stores.StoreStream
import com.discord.utilities.rx.ObservableExtensionsKt
import com.discord.widgets.servers.WidgetServerSettingsInstantInvitesActions
import com.discord.widgets.servers.settings.invites.WidgetServerSettingsInstantInvites
import com.google.gson.stream.JsonReader

class InvitesPage(private val channel: Channel) :
    AppFragment(Utils.getResId("widget_server_settings_instant_invites", "layout")) {
    private val viewFlipperId = Utils.getResId("server_settings_instant_invites_view_flipper", "id")
    private val recyclerId = Utils.getResId("server_settings_instant_invites_recycler", "id")

    override fun onViewBound(view: View?) {
        super.onViewBound(view)

        setActionBarTitle("Invites")
        setActionBarSubtitle("#${channel.name}")
        setActionBarDisplayHomeAsUpEnabled(true)
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val viewFlipper = view.findViewById<AppViewFlipper>(viewFlipperId)
        val invitesRecycler = view.findViewById<RecyclerView>(recyclerId)

        Utils.threadPool.execute {
            val json = Http.Request.newDiscordRequest("/channels/${channel.id}/invites")
                .execute()
                .text()

            val invites = InboundGatewayGsonParser.fromJson(JsonReader(json.reader()), Array<ModelInvite>::class.java)
            val inviteItems = invites.map { modelInvite ->
                WidgetServerSettingsInstantInvites.Model.InviteItem(modelInvite, channel.guildId, null)
            }

            Utils.mainThread.post {
                if (invites.isEmpty()) {
                    viewFlipper.displayedChild = 2
                    return@post
                } else {
                    invitesRecycler.run {
                        adapter = WidgetServerSettingsInstantInvites.Adapter(this).also { adapter ->
                            val onInviteSelectedListener = { modelInvite: ModelInvite ->
                                WidgetServerSettingsInstantInvitesActions.create(parentFragmentManager, modelInvite.code)
                            }

                            val onInviteExpiredListener = { modelInvite: ModelInvite ->
                                StoreStream.getInstantInvites().onInviteRemoved(modelInvite)
                            }

                            adapter.configure(inviteItems, onInviteSelectedListener, onInviteExpiredListener)
                        }
                        layoutManager = LinearLayoutManager(context)
                    }
                    viewFlipper.displayedChild = 1
                }
            }
        }

        (WidgetServerSettingsInstantInvites.Model.Companion).get(channel.guildId, StoreStream.getGuilds(), StoreStream.getInstantInvites())
    }

    //    private fun configureUI(model: WidgetServerSettingsInstantInvites.Model) {
//        val viewFlipper = requireView().findViewById<AppViewFlipper>(viewFlipperId)
//        val invitesRecycler = requireView().findViewById<RecyclerView>(recyclerId)
//
//        val invites = model.inviteItems
//        if (invites.isEmpty()) {
//            viewFlipper.displayedChild = 2
//            return
//        } else  {
//            invitesRecycler.run {
//                adapter = WidgetServerSettingsInstantInvites.Adapter(this).also { adapter ->
//                    val onInviteSelectedListener = { modelInvite: ModelInvite ->
//                        WidgetServerSettingsInstantInvitesActions.create(parentFragmentManager, modelInvite.code)
//                    }
//
//                    val onInviteExpiredListener = { modelInvite: ModelInvite ->
//                        StoreStream.getInstantInvites().onInviteRemoved(modelInvite)
//                    }
//
//                    adapter.configure(invites, onInviteSelectedListener, onInviteExpiredListener)
//                }
//                layoutManager = LinearLayoutManager(context)
//            }
//            viewFlipper.displayedChild = 1
//        }
//    }
//
    override fun onViewBoundOrOnResume() {
        super.onViewBoundOrOnResume()
        ObservableExtensionsKt.appSubscribe(ObservableExtensionsKt.ui((WidgetServerSettingsInstantInvites.Model.Companion).get(channel.guildId, StoreStream.getGuilds(), StoreStream.getInstantInvites()), this, null), InvitesPage::class.java, null, { }, { }, { }, { }, {
            Utils.showToast("guh")
//            configureUI(it)
        })
    }
}