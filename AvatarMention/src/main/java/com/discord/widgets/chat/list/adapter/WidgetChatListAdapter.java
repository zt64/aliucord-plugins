package com.discord.widgets.chat.list.adapter;

import com.discord.models.message.Message;

public class WidgetChatListAdapter {
    public interface Data {
        long getGuildId();
    }
    public final Data getData() { return getData(); }
    public final EventHandler getEventHandler() { return getEventHandler(); }

    public interface EventHandler {
        void onMessageAuthorNameClicked(Message message, long j);    }
}
