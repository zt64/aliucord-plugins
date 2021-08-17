package com.aliucord.plugins;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.aliucord.entities.Plugin;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapter;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage$onConfigure$5;

import top.canyie.pine.Pine;
import top.canyie.pine.callback.MethodReplacement;

public class AvatarMention extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{new Manifest.Author("zt", 289556910426816513L)};
        manifest.description = "Tapping a message avatar will mention the user.";
        manifest.version = "1.0.1";
        manifest.updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) throws Throwable {
        patcher.patch(WidgetChatListAdapterItemMessage$onConfigure$5.class.getDeclaredMethod("onClick", View.class), new MethodReplacement() {
            @Override
            protected Object replaceCall(Pine.CallFrame callFrame) {
                WidgetChatListAdapterItemMessage$onConfigure$5 thisObject = (WidgetChatListAdapterItemMessage$onConfigure$5) callFrame.thisObject;
                WidgetChatListAdapter adapter = WidgetChatListAdapterItemMessage.access$getAdapter$p(thisObject.this$0);

                adapter.getEventHandler().onMessageAuthorNameClicked(thisObject.$message, adapter.getData().getGuildId());
                return null;
            }
        });
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
