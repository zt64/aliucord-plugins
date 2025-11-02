import android.content.Context;
import android.view.View;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class AvatarMention extends Plugin {
    @Override
    public void start(Context context) {
        patcher.instead(
            WidgetChatListAdapterItemMessage.class.getDeclaredClasses()[0], // The inner class for onClick
            "onClick",
            (callFrame) -> {
                Object thisObject = callFrame.thisObject;
                try {
                    // Access the outer class instance (this$0)
                    java.lang.reflect.Field outerField = thisObject.getClass().getDeclaredField("this$0");
                    outerField.setAccessible(true);
                    WidgetChatListAdapterItemMessage outerInstance = (WidgetChatListAdapterItemMessage) outerField.get(thisObject);
                    
                    // Access the message field
                    java.lang.reflect.Field messageField = thisObject.getClass().getDeclaredField("$message");
                    messageField.setAccessible(true);
                    Object message = messageField.get(thisObject);
                    
                    // Access adapter using the accessor method
                    java.lang.reflect.Method getAdapterMethod = WidgetChatListAdapterItemMessage.class.getDeclaredMethod("access$getAdapter$p", WidgetChatListAdapterItemMessage.class);
                    getAdapterMethod.setAccessible(true);
                    Object adapter = getAdapterMethod.invoke(null, outerInstance);
                    
                    // Call the event handler
                    java.lang.reflect.Field eventHandlerField = adapter.getClass().getField("eventHandler");
                    Object eventHandler = eventHandlerField.get(adapter);
                    
                    java.lang.reflect.Field dataField = adapter.getClass().getField("data");
                    Object data = dataField.get(adapter);
                    java.lang.reflect.Field guildIdField = data.getClass().getField("guildId");
                    long guildId = (long) guildIdField.get(data);
                    
                    java.lang.reflect.Method onMessageAuthorNameClickedMethod = eventHandler.getClass().getMethod("onMessageAuthorNameClicked", message.getClass(), long.class);
                    onMessageAuthorNameClickedMethod.invoke(eventHandler, message, guildId);
                } catch (Exception e) {
                    logger.error("Failed to handle avatar click", e);
                }
                return null;
            },
            View.class
        );
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
