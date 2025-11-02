package channelinvites;

import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aliucord.Http;
import com.aliucord.Utils;
import com.discord.api.channel.Channel;
import com.discord.app.AppFragment;
import com.discord.app.AppViewFlipper;
import com.discord.models.deserialization.gson.InboundGatewayGsonParser;
import com.discord.models.domain.ModelInvite;
import com.discord.stores.StoreStream;
import com.discord.utilities.rx.ObservableExtensionsKt;
import com.discord.widgets.servers.WidgetServerSettingsInstantInvitesActions;
import com.discord.widgets.servers.settings.invites.WidgetServerSettingsInstantInvites;
import com.google.gson.stream.JsonReader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
public class InvitesPage extends AppFragment {
    private final Channel channel;
    private final int viewFlipperId;
    private final int recyclerId;

    public InvitesPage(Channel channel) {
        super(Utils.getResId("widget_server_settings_instant_invites", "layout"));
        this.channel = channel;
        this.viewFlipperId = Utils.getResId("server_settings_instant_invites_view_flipper", "id");
        this.recyclerId = Utils.getResId("server_settings_instant_invites_recycler", "id");
    }

    @Override
    public void onViewBound(View view) {
        super.onViewBound(view);

        try {
            Field nameField = Channel.class.getDeclaredField("name");
            nameField.setAccessible(true);
            String channelName = (String) nameField.get(channel);

            setActionBarTitle("Invites");
            setActionBarSubtitle("#" + channelName);
            setActionBarDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            logger.error("Failed to set action bar", e);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        AppViewFlipper viewFlipper = view.findViewById(viewFlipperId);
        RecyclerView invitesRecycler = view.findViewById(recyclerId);

        Utils.threadPool.execute(() -> {
            try {
                Field idField = Channel.class.getDeclaredField("id");
                idField.setAccessible(true);
                long channelId = (long) idField.get(channel);

                Field guildIdField = Channel.class.getDeclaredField("guildId");
                guildIdField.setAccessible(true);
                long guildId = (long) guildIdField.get(channel);

                String json = new Http.Request("/channels/" + channelId + "/invites")
                    .execute()
                    .text();

                ModelInvite[] invites = InboundGatewayGsonParser.INSTANCE.fromJson(
                    new JsonReader(new java.io.StringReader(json)),
                    ModelInvite[].class
                );

                List<WidgetServerSettingsInstantInvites.Model.InviteItem> inviteItems = new ArrayList<>();
                for (ModelInvite modelInvite : invites) {
                    inviteItems.add(new WidgetServerSettingsInstantInvites.Model.InviteItem(modelInvite, guildId, null));
                }

                Utils.mainThread.post(() -> {
                    if (invites.length == 0) {
                        viewFlipper.setDisplayedChild(2);
                        return;
                    }

                    WidgetServerSettingsInstantInvites.Adapter adapter = new WidgetServerSettingsInstantInvites.Adapter(invitesRecycler);
                    
                    adapter.configure(
                        inviteItems,
                        modelInvite -> WidgetServerSettingsInstantInvitesActions.create(getParentFragmentManager(), modelInvite.getCode()),
                        modelInvite -> StoreStream.getInstantInvites().onInviteRemoved(modelInvite)
                    );
                    
                    invitesRecycler.setAdapter(adapter);
                    invitesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    viewFlipper.setDisplayedChild(1);
                });
            } catch (Exception e) {
                logger.error("Failed to load invites", e);
            }
        });

        try {
            Field guildIdField = Channel.class.getDeclaredField("guildId");
            guildIdField.setAccessible(true);
            long guildId = (long) guildIdField.get(channel);

            Method getMethod = WidgetServerSettingsInstantInvites.Model.Companion.class.getMethod(
                "get",
                long.class,
                com.discord.stores.StoreGuilds.class,
                com.discord.stores.StoreInstantInvites.class
            );
            getMethod.invoke(
                WidgetServerSettingsInstantInvites.Model.Companion,
                guildId,
                StoreStream.getGuilds(),
                StoreStream.getInstantInvites()
            );
        } catch (Exception e) {
            logger.error("Failed to get model", e);
        }
    }

    @Override
    public void onViewBoundOrOnResume() {
        super.onViewBoundOrOnResume();
        
        try {
            Field guildIdField = Channel.class.getDeclaredField("guildId");
            guildIdField.setAccessible(true);
            long guildId = (long) guildIdField.get(channel);

            Method getMethod = WidgetServerSettingsInstantInvites.Model.Companion.class.getMethod(
                "get",
                long.class,
                com.discord.stores.StoreGuilds.class,
                com.discord.stores.StoreInstantInvites.class
            );
            
            Object observable = getMethod.invoke(
                WidgetServerSettingsInstantInvites.Model.Companion,
                guildId,
                StoreStream.getGuilds(),
                StoreStream.getInstantInvites()
            );

            ObservableExtensionsKt.appSubscribe(
                ObservableExtensionsKt.ui((rx.Observable) observable, this, null),
                InvitesPage.class,
                null,
                null,
                null,
                null,
                null,
                model -> Utils.showToast("guh")
            );
        } catch (Exception e) {
            logger.error("Failed in onViewBoundOrOnResume", e);
        }
    }
}
