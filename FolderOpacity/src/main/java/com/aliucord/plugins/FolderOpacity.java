package com.aliucord.plugins;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.aliucord.Logger;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.utils.DimenUtils;
import com.aliucord.utils.ReflectUtils;
import com.aliucord.views.Divider;
import com.discord.databinding.WidgetGuildFolderSettingsBinding;
import com.discord.widgets.guilds.WidgetGuildFolderSettings;
import com.discord.widgets.guilds.WidgetGuildFolderSettings$configureUI$3;
import com.discord.widgets.guilds.WidgetGuildFolderSettingsViewModel;
import com.discord.widgets.guilds.list.FolderItemDecoration;
import com.discord.widgets.guilds.list.GuildListItem;
import com.discord.widgets.guilds.list.GuildListViewHolder;
import com.lytefast.flexinput.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import d0.z.d.m;
import top.canyie.pine.Pine;
import top.canyie.pine.callback.MethodReplacement;

public class FolderOpacity extends Plugin {
    private final Logger logger = new Logger("FolderOpacity");

    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("zt", 289556910426816513L) };
        manifest.description = "Adds an option to the guild folder settings to set the opacity";
        manifest.version = "1.0.0";
        manifest.updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) throws NoSuchMethodException {
        final int seekBarId = View.generateViewId();

        Method getBinding = WidgetGuildFolderSettings.class.getDeclaredMethod("getBinding");
        getBinding.setAccessible(true);

        patcher.patch(WidgetGuildFolderSettings.class.getDeclaredMethod("configureUI", WidgetGuildFolderSettingsViewModel.ViewState.class), new PinePatchFn(callFrame -> {
            try {
                WidgetGuildFolderSettings _this = (WidgetGuildFolderSettings) callFrame.thisObject;
                WidgetGuildFolderSettingsViewModel viewModel = WidgetGuildFolderSettings.access$getViewModel$p(_this);
                WidgetGuildFolderSettingsBinding binding = (WidgetGuildFolderSettingsBinding) getBinding.invoke(_this);
                Context ctx = _this.getContext();
                assert ctx != null;
                assert binding != null;

                LinearLayout linearLayout = (LinearLayout) binding.d.getParent();

                if (linearLayout.findViewById(seekBarId) != null) return;

                int opacity = settings.getInt(viewModel.getFolderId() + "opacity", 255);

                TextView opacityHeader = new TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Header);
                opacityHeader.setText("Folder Opacity");

                TextView currentOpacity = new TextView(ctx, null, 0, R.h.UiKit_TextView);
                currentOpacity.setText(String.valueOf(opacity));
                currentOpacity.setWidth(DimenUtils.dpToPx(28));

                SeekBar seekbar = new SeekBar(ctx, null, 0, R.h.UiKit_SeekBar);
                seekbar.setId(seekBarId);
                seekbar.setPadding(DimenUtils.dpToPx(12), 0, DimenUtils.dpToPx(12), 0);
                seekbar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                seekbar.setMax(255);
                seekbar.setProgress(opacity);
                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        currentOpacity.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        binding.f.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                });

                LinearLayout opacitySection = new LinearLayout(ctx, null, 0, R.h.UiKit_Settings_Item);
                opacitySection.addView(currentOpacity);
                opacitySection.addView(seekbar);

                linearLayout.addView(new Divider(ctx));
                linearLayout.addView(opacityHeader);
                linearLayout.addView(opacitySection);

//                binding.f.setOnClickListener(v -> {
//                    Utils.showToast(ctx, "saved");
//                    (new WidgetGuildFolderSettings$configureUI$3(_this)).onClick(v);
//                    settings.setInt(viewModel.getFolderId() + "opacity", seekbar.getProgress());
//                });
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error(e);
            }
        }));

        patcher.patch(WidgetGuildFolderSettings$configureUI$3.class.getDeclaredMethod("onClick", View.class), new PinePatchFn(callFrame -> {
            WidgetGuildFolderSettings widgetGuildFolderSettings = ((WidgetGuildFolderSettings$configureUI$3) callFrame.thisObject).this$0;
            WidgetGuildFolderSettingsViewModel viewModel = WidgetGuildFolderSettings.access$getViewModel$p(((WidgetGuildFolderSettings$configureUI$3) callFrame.thisObject).this$0);

            try {
                SeekBar seekBar = ((WidgetGuildFolderSettingsBinding) Objects.requireNonNull(getBinding.invoke(widgetGuildFolderSettings))).getRoot().findViewById(seekBarId);
                settings.setInt(viewModel.getFolderId() + "opacity", seekBar.getProgress());
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error(e);
            }
        }));

        Method _drawBackgroundForInitialFolder = FolderItemDecoration.class.getDeclaredMethod("drawBackgroundForInitialFolder", Canvas.class, RecyclerView.class);
        _drawBackgroundForInitialFolder.setAccessible(true);

        patcher.patch(FolderItemDecoration.class.getDeclaredMethod("onDraw", Canvas.class, RecyclerView.class, RecyclerView.State.class), new MethodReplacement() {
            @Override
            protected Object replaceCall(Pine.CallFrame callFrame) throws Throwable {
                FolderItemDecoration _this = (FolderItemDecoration) callFrame.thisObject;

                Drawable drawableNoChildren = (Drawable) ReflectUtils.getField(_this, "drawableNoChildren");
                Drawable drawableWithChildren = (Drawable) ReflectUtils.getField(_this, "drawableWithChildren");
                Drawable tintableDrawableNoChildren = (Drawable) ReflectUtils.getField(_this, "tintableDrawableNoChildren");
                int halfSize = (int) Objects.requireNonNull(ReflectUtils.getField(_this, "halfSize"));

                Canvas canvas = (Canvas) callFrame.args[0];
                RecyclerView recyclerView = (RecyclerView) callFrame.args[1];

                Drawable drawable;
                m.checkNotNullParameter(canvas, "c");
                m.checkNotNullParameter(recyclerView, "parent");
                if (recyclerView.getChildCount() < 1) return null;

                int childCount = recyclerView.getChildCount();
                for (int drawBackgroundForInitialFolder = (int) Objects.requireNonNull(_drawBackgroundForInitialFolder.invoke(_this, canvas, recyclerView)); drawBackgroundForInitialFolder < childCount; drawBackgroundForInitialFolder++) {
                    View childAt = recyclerView.getChildAt(drawBackgroundForInitialFolder);
                    RecyclerView.ViewHolder childViewHolder = recyclerView.getChildViewHolder(childAt);
                    if (childViewHolder instanceof GuildListViewHolder.FolderViewHolder) {
                        GuildListViewHolder.FolderViewHolder folderViewHolder = (GuildListViewHolder.FolderViewHolder) childViewHolder;
                        if (!folderViewHolder.shouldDrawDecoration()) continue;

                        m.checkNotNullExpressionValue(childAt, "view");
                        int left = (childAt.getLeft() + childAt.getRight()) / 2;
                        int top = (childAt.getTop() + childAt.getBottom()) / 2;
                        int numChildren = folderViewHolder.getNumChildren();
                        if (numChildren == 0) {
                            Integer color = folderViewHolder.getColor();
                            if (color != null) {
                                assert tintableDrawableNoChildren != null;
                                int alpha = settings.getInt(((GuildListItem.FolderItem) Objects.requireNonNull(ReflectUtils.getField(folderViewHolder, "data"))).getFolderId() + "opacity", 255);
                                color = ColorUtils.setAlphaComponent(color, alpha);
                                tintableDrawableNoChildren.setColorFilter(color, PorterDuff.Mode.SRC);

                                drawable = tintableDrawableNoChildren;
                            } else {
                                drawable = drawableNoChildren;
                            }
                            assert drawable != null;
                            drawable.setBounds(left - halfSize, top - halfSize, left + halfSize, top + halfSize);
                            drawable.draw(canvas);
                        } else {
                            int height = (childAt.getHeight() * numChildren) + halfSize + top;
                            assert drawableWithChildren != null;
                            drawableWithChildren.setBounds(left - halfSize, top - halfSize, left + halfSize, height);
                            drawableWithChildren.draw(canvas);
                        }
                    }
                    if ((childViewHolder instanceof GuildListViewHolder.GuildViewHolder) && ((GuildListViewHolder.GuildViewHolder) childViewHolder).isTargetedForFolderCreation()) {
                        m.checkNotNullExpressionValue(childAt, "view");
                        int left2 = (childAt.getLeft() + childAt.getRight()) / 2;
                        int top2 = (childAt.getTop() + childAt.getBottom()) / 2;
                        assert drawableNoChildren != null;
                        drawableNoChildren.setBounds(left2 - halfSize, top2 - halfSize, left2 + halfSize, top2 + halfSize);
                        drawableNoChildren.draw(canvas);
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void stop(Context context) { patcher.unpatchAll(); }
}