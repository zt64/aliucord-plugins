package com.aliucord.plugins;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.aliucord.Utils;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.discord.api.channel.Channel;
import com.discord.api.user.TypingUser;
import com.discord.databinding.WidgetChannelsListItemChannelBinding;
import com.discord.stores.StoreStream;
import com.discord.stores.StoreUserTyping;
import com.discord.views.typing.TypingDots;
import com.discord.widgets.channels.list.WidgetChannelsList$binding$2;
import com.discord.widgets.channels.list.WidgetChannelsListAdapter;
import com.discord.widgets.channels.list.items.ChannelListItem;
import com.discord.widgets.channels.list.items.ChannelListItemTextChannel;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class TypingIndicators extends Plugin {
    final static int id = View.generateViewId();

    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Möth", 289556910426816513L) };
        manifest.description = "Adds typing indicators to channels.";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/litleck/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) throws NoSuchMethodException {
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(Utils.dpToPx(24), ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_END);

        patcher.patch(WidgetChannelsListAdapter.ItemChannelText.class.getDeclaredMethod("onConfigure", int.class, ChannelListItem.class), new PinePatchFn(callFrame -> {
            final ChannelListItemTextChannel textChannel = (ChannelListItemTextChannel) callFrame.args[1];
            final WidgetChannelsListAdapter.ItemChannelText itemChannelText = (WidgetChannelsListAdapter.ItemChannelText) callFrame.thisObject;

            WidgetChannelsListItemChannelBinding binding = null;
            try {
                final Field bindingField = itemChannelText.getClass().getDeclaredField("binding");
                bindingField.setAccessible(true);
                binding = (WidgetChannelsListItemChannelBinding) bindingField.get(itemChannelText);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            assert binding != null;
            if (binding.a.findViewById(id) != null) return;

            final TypingDots typingDots = new TypingDots(Utils.appActivity, null);
            typingDots.setId(id);
            typingDots.a(false);

            binding.a.addView(typingDots, lp);
        }));

        patcher.patch(StoreUserTyping.class.getDeclaredMethod("handleTypingStart", TypingUser.class), new PinePatchFn(callFrame -> {
            final TypingUser typingUser = (TypingUser) callFrame.args[0];

            if (StoreStream.getGuildSelected().getSelectedGuildId() != typingUser.b()) return;
            if (StoreStream.getUsers().getMe().getId() == typingUser.d()) return;

            final Channel channel = StoreStream.getChannels().getChannel(typingUser.a());

            WidgetChannelsList$binding$2 instance = WidgetChannelsList$binding$2.INSTANCE;
        }));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}