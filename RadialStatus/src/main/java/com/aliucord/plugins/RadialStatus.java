package com.aliucord.plugins;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.discord.databinding.WidgetChannelMembersListItemUserBinding;
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListAdapter;
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListViewHolderMember;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;
import com.discord.widgets.chat.list.entries.ChatListEntry;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class RadialStatus extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Möth", 289556910426816513L) };
        manifest.description = "Displays the user status as a radial circle around the avatar.";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/litleck/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) throws NoSuchMethodException {
        patcher.patch(ChannelMembersListAdapter.class.getDeclaredMethod("onCreateViewHolder", ViewGroup.class, int.class), new PinePatchFn(callFrame -> {
            ChannelMembersListViewHolderMember channelMembersListViewHolderMember = (ChannelMembersListViewHolderMember) callFrame.getResult();
            WidgetChannelMembersListItemUserBinding binding = null;

            try {
                Field field = ChannelMembersListViewHolderMember.class.getDeclaredField("binding");
                field.setAccessible(true);
                binding = (WidgetChannelMembersListItemUserBinding) field.get(channelMembersListViewHolderMember);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            if (binding == null) return;

            ConstraintLayout root = (ConstraintLayout) binding.getRoot();
            SimpleDraweeView simpleDraweeView = binding.b;
            simpleDraweeView.setScaleX(3);
        }));

        patcher.patch(WidgetChatListAdapterItemMessage.class.getDeclaredMethod("onConfigure", int.class, ChatListEntry.class), new PinePatchFn(callFrame -> {
            WidgetChatListAdapterItemMessage widgetChatListAdapterItemMessage = ((WidgetChatListAdapterItemMessage) callFrame.thisObject);

            Log.v("","");
        }));
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
