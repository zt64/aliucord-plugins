import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.utils.DimenUtils;
import com.aliucord.views.Divider;
import com.discord.databinding.WidgetGuildFolderSettingsBinding;
import com.discord.databinding.WidgetGuildsListItemFolderBinding;
import com.discord.widgets.guilds.WidgetGuildFolderSettings;
import com.discord.widgets.guilds.WidgetGuildFolderSettingsViewModel;
import com.discord.widgets.guilds.list.GuildListItem;
import com.discord.widgets.guilds.list.GuildListViewHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lytefast.flexinput.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class FolderOpacity extends Plugin {
    private final HashMap<Long, GuildListViewHolder.FolderViewHolder> folderViewHolderMap = new HashMap<>();
    private Field folderBindingField;
    private Method getBindingMethod;
    private int folderContainerId;

    @Override
    public void start(Context context) {
        try {
            folderBindingField = GuildListViewHolder.FolderViewHolder.class.getDeclaredField("binding");
            folderBindingField.setAccessible(true);
            
            getBindingMethod = WidgetGuildFolderSettings.class.getDeclaredMethod("getBinding");
            getBindingMethod.setAccessible(true);
        } catch (Exception e) {
            logger.error("Failed to get reflection members", e);
            return;
        }

        folderContainerId = Utils.getResId("guilds_item_folder_container", "id");
        int folderColorPickerId = Utils.getResId("guild_folder_settings_color", "id");
        int saveButtonId = Utils.getResId("guild_folder_settings_save", "id");
        int seekBarId = View.generateViewId();

        // Disable decoration drawing
        patcher.instead(GuildListViewHolder.FolderViewHolder.class, "shouldDrawDecoration", (callFrame) -> false);

        // Set the background color with alpha
        patcher.after(GuildListViewHolder.FolderViewHolder.class, "configure", (callFrame) -> {
            GuildListViewHolder.FolderViewHolder holder = (GuildListViewHolder.FolderViewHolder) callFrame.thisObject;
            GuildListItem.FolderItem folderItem = (GuildListItem.FolderItem) callFrame.args[0];
            
            folderViewHolderMap.put(folderItem.getFolderId(), holder);
            setAlpha(holder, settings.getInt(folderItem.getFolderId() + "opacity", 30));
        }, GuildListItem.FolderItem.class);

        // Patch folder settings
        patcher.after(WidgetGuildFolderSettings.class, "configureUI", (callFrame) -> {
            WidgetGuildFolderSettings widget = (WidgetGuildFolderSettings) callFrame.thisObject;
            
            try {
                WidgetGuildFolderSettingsBinding binding = (WidgetGuildFolderSettingsBinding) getBindingMethod.invoke(widget);
                RelativeLayout root = (RelativeLayout) binding.getRoot();
                Context ctx = widget.requireContext();

                LinearLayout linearLayout = (LinearLayout) root.findViewById(folderColorPickerId).getParent();
                if (linearLayout.findViewById(seekBarId) != null) return;

                // Get folder ID using reflection
                Field viewModelField = WidgetGuildFolderSettings.class.getDeclaredField("viewModel$delegate");
                viewModelField.setAccessible(true);
                Object viewModelDelegate = viewModelField.get(widget);
                
                Method getValueMethod = viewModelDelegate.getClass().getMethod("getValue");
                WidgetGuildFolderSettingsViewModel viewModel = (WidgetGuildFolderSettingsViewModel) getValueMethod.invoke(viewModelDelegate);
                long folderId = viewModel.getFolderId();

                int opacity = settings.getInt(folderId + "opacity", 30);
                TextView currentOpacity = new TextView(ctx, null, 0, R.i.UiKit_TextView);
                currentOpacity.setText(String.valueOf(opacity));
                currentOpacity.setWidth(DimenUtils.dpToPx(28));

                linearLayout.addView(new Divider(ctx));
                
                TextView header = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header);
                header.setText("Folder Opacity");
                linearLayout.addView(header);

                LinearLayout itemLayout = new LinearLayout(ctx, null, 0, R.i.UiKit_Settings_Item);
                itemLayout.addView(currentOpacity);

                SeekBar seekBar = new SeekBar(ctx, null, 0, R.i.UiKit_SeekBar);
                seekBar.setId(seekBarId);
                seekBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                seekBar.setMax(255);
                seekBar.setProgress(opacity);
                int padding = DimenUtils.dpToPx(12);
                seekBar.setPadding(padding, 0, padding, 0);
                
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        currentOpacity.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        root.findViewById(saveButtonId).setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

                itemLayout.addView(seekBar);
                linearLayout.addView(itemLayout);
            } catch (Exception e) {
                logger.error("Failed to configure UI", e);
            }
        }, WidgetGuildFolderSettingsViewModel.ViewState.class);

        // Patch save button
        try {
            Class<?> saveClickListener = null;
            for (Class<?> clazz : WidgetGuildFolderSettings.class.getDeclaredClasses()) {
                if (clazz.getName().contains("$configureUI$")) {
                    saveClickListener = clazz;
                    break;
                }
            }
            
            if (saveClickListener != null) {
                patcher.after(saveClickListener, "onClick", (callFrame) -> {
                    try {
                        Field thisField = callFrame.thisObject.getClass().getDeclaredField("this$0");
                        thisField.setAccessible(true);
                        WidgetGuildFolderSettings widget = (WidgetGuildFolderSettings) thisField.get(callFrame.thisObject);
                        
                        // Get viewModel
                        Field viewModelField = WidgetGuildFolderSettings.class.getDeclaredField("viewModel$delegate");
                        viewModelField.setAccessible(true);
                        Object viewModelDelegate = viewModelField.get(widget);
                        
                        Method getValueMethod = viewModelDelegate.getClass().getMethod("getValue");
                        WidgetGuildFolderSettingsViewModel viewModel = (WidgetGuildFolderSettingsViewModel) getValueMethod.invoke(viewModelDelegate);
                        long folderId = viewModel.getFolderId();
                        
                        WidgetGuildFolderSettingsBinding binding = (WidgetGuildFolderSettingsBinding) getBindingMethod.invoke(widget);
                        SeekBar seekBar = binding.getRoot().findViewById(seekBarId);

                        settings.setInt(folderId + "opacity", seekBar.getProgress());

                        GuildListViewHolder.FolderViewHolder holder = folderViewHolderMap.get(folderId);
                        if (holder != null) {
                            setAlpha(holder, seekBar.getProgress());
                        }
                    } catch (Exception e) {
                        logger.error("Failed to save opacity", e);
                    }
                }, View.class);
            }
        } catch (Exception e) {
            logger.error("Failed to patch save button", e);
        }
    }

    private void setAlpha(GuildListViewHolder.FolderViewHolder holder, int alpha) {
        try {
            WidgetGuildsListItemFolderBinding binding = (WidgetGuildsListItemFolderBinding) folderBindingField.get(holder);
            if (binding == null) return;
            
            View root = binding.getRoot();
            
            // Get color using reflection
            Field colorField = GuildListViewHolder.FolderViewHolder.class.getDeclaredField("color");
            colorField.setAccessible(true);
            Integer color = (Integer) colorField.get(holder);
            if (color == null) color = Color.WHITE;
            
            int colorWithAlpha = ColorUtils.setAlphaComponent(color, alpha);
            Drawable background = ContextCompat.getDrawable(root.getContext(), R.e.drawable_squircle_white_alpha_30);
            
            if (background != null) {
                background.setColorFilter(new PorterDuffColorFilter(colorWithAlpha, PorterDuff.Mode.SRC));
                FrameLayout container = root.findViewById(folderContainerId);
                if (container != null) {
                    container.setBackground(background);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to set alpha", e);
        }
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
