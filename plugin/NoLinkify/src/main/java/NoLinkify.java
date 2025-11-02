import android.content.Context;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;

@AliucordPlugin
public class NoLinkify extends Plugin {
    @Override
    public void start(Context context) {
        patcher.instead(WidgetChatListAdapterItemMessage.class, "shouldLinkify", (callFrame) -> false, String.class);
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
