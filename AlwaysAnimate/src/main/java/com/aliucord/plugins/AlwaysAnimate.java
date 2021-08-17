package com.aliucord.plugins;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.patcher.PinePrePatchFn;
import com.aliucord.utils.ReflectUtils;
import com.discord.models.guild.Guild;
import com.discord.models.user.CoreUser;
import com.discord.utilities.icon.IconUtils;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;
import com.discord.widgets.chat.list.entries.ChatListEntry;
import com.discord.widgets.chat.list.entries.MessageEntry;
import com.discord.widgets.guilds.list.GuildListViewHolder;

public class AlwaysAnimate extends Plugin {
    public AlwaysAnimate() {
        settingsTab = new SettingsTab(PluginSettings.class, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings);
    }

    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Möth", 289556910426816513L) };
        manifest.description = "Allows making server icons and member avatars always animate.";
        manifest.version = "1.0.1";
        manifest.updateUrl = "https://raw.githubusercontent.com/litleck/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) throws NoSuchMethodException {
        if (settings.getBool("guildIcon", true)) {
            patcher.patch(GuildListViewHolder.GuildViewHolder.class.getDeclaredMethod("configureGuildIconImage", Guild.class, boolean.class), new PinePrePatchFn(callFrame -> callFrame.args[1] = true));
        }

        if (settings.getBool("authorAvatar", true)) {
            final IconUtils iconUtils = IconUtils.INSTANCE;
            patcher.patch(WidgetChatListAdapterItemMessage.class.getDeclaredMethod("onConfigure", int.class, ChatListEntry.class), new PinePatchFn(callFrame -> {
                MessageEntry messageEntry = (MessageEntry) callFrame.args[1];
                CoreUser coreUser = new CoreUser(messageEntry.getMessage().getAuthor());
                if (!iconUtils.isImageHashAnimated(coreUser.getAvatar())) return;

                try {
                    ImageView imageView = (ImageView) ReflectUtils.getField(callFrame.thisObject, "itemAvatar");
                    if (imageView != null) IconUtils.setIcon(imageView, coreUser.getAvatar());
                    // String avatar = iconUtils.getForGuildMember(messageEntry.getAuthor(), 1, true);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }));
        }
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}
