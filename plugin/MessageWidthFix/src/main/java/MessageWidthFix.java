import android.content.Context;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemEmbed;
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemSticker;
import com.discord.widgets.chat.list.adapter.WidgetChatListItem;
import com.discord.widgets.chat.list.entries.ChatListEntry;
import com.discord.widgets.chat.list.entries.StickerEntry;
import com.google.android.material.card.MaterialCardView;
import com.lytefast.flexinput.R;

@AliucordPlugin
public class MessageWidthFix extends Plugin {
    @Override
    public void start(Context context) {
        int containerCardViewId = Utils.getResId("chat_list_item_embed_container_card", "id");

        patcher.after(WidgetChatListItem.class, "onConfigure", (callFrame) -> {
            WidgetChatListItem item = (WidgetChatListItem) callFrame.thisObject;
            ChatListEntry chatListEntry = (ChatListEntry) callFrame.args[1];

            item.itemView.getResources().getDimension(R.d.chat_cell_horizontal_spacing_padding);

            if (item instanceof WidgetChatListAdapterItemEmbed) {
                ConstraintLayout layout = (ConstraintLayout) item.itemView;
                
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) layout.getLayoutParams();
                layoutParams.width = RecyclerView.LayoutParams.MATCH_PARENT;

                MaterialCardView cardView = layout.findViewById(containerCardViewId);
                ConstraintLayout.LayoutParams cardParams = (ConstraintLayout.LayoutParams) cardView.getLayoutParams();
                cardParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                cardParams.horizontalBias = 0.0f;
                cardParams.constrainedWidth = true;
            } else if (item instanceof WidgetChatListAdapterItemSticker) {
                FrameLayout layout = (FrameLayout) item.itemView;
                layout.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;

                // Discord devs too lazy to do it themselves
                layout.setOnLongClickListener(v -> {
                    StickerEntry stickerEntry = (StickerEntry) chatListEntry;
                    ((WidgetChatListAdapterItemSticker) item).adapter.eventHandler.onMessageLongClicked(
                        stickerEntry.getMessage(),
                        "",
                        stickerEntry.getMessage().hasThread()
                    );
                    return true;
                });
            }
        }, int.class, ChatListEntry.class);
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
