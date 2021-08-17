package com.aliucord.plugins;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.discord.utilities.color.ColorCompat;
import com.discord.widgets.settings.WidgetSettings;
import com.lytefast.flexinput.R;

import java.util.Objects;

public class RestartButton extends Plugin {
    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{new Manifest.Author("zt", 289556910426816513L)};
        manifest.description = "Adds a button to restart Aliucord to the settings page";
        manifest.version = "1.0.3";
        manifest.updateUrl = "https://raw.githubusercontent.com/zt64/aliucord-plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) throws NoSuchMethodException {
        final Drawable icon = Objects.requireNonNull(ContextCompat.getDrawable(context, com.yalantis.ucrop.R.c.ucrop_rotate)).mutate();

        patcher.patch(WidgetSettings.class.getDeclaredMethod("onTabSelected"), new PinePatchFn(callFrame -> {
            final WidgetSettings _this = (WidgetSettings) callFrame.thisObject;

            icon.setTint(ColorCompat.getThemedColor(_this.requireContext(), R.b.colorInteractiveNormal));

            _this.requireAppActivity().t.getMenu().add("Restart")
                    .setIcon(icon)
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    .setOnMenuItemClickListener(item -> {
                        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                        context.startActivity(Intent.makeRestartActivityTask(intent.getComponent()));
                        Runtime.getRuntime().exit(0);
                        return false;
                    });
        }));
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}