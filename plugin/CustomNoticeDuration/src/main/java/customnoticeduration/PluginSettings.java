package customnoticeduration;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.aliucord.Utils;
import com.aliucord.api.SettingsAPI;
import com.aliucord.utils.DimenUtils;
import com.aliucord.widgets.BottomSheet;
import com.discord.views.CheckedSetting;
import com.lytefast.flexinput.R;

@SuppressWarnings({"MISSING_DEPENDENCY_SUPERCLASS", "SetTextI18n"})
public class PluginSettings extends BottomSheet {
    private final SettingsAPI settings;

    public PluginSettings(SettingsAPI settings) {
        this.settings = settings;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        android.content.Context ctx = requireContext();

        long noticeDuration = settings.getLong("noticeDuration", 5000);
        TextView currentTimeout = new TextView(ctx, null, 0, R.i.UiKit_TextView);
        currentTimeout.setText(noticeDuration + " ms");
        currentTimeout.setWidth(DimenUtils.dpToPx(72));

        int offset = 1000;
        boolean autoDismissNotice = settings.getBool("autoDismissNotice", true);
        
        SeekBar seekBar = new SeekBar(ctx, null, 0, R.i.UiKit_SeekBar);
        seekBar.setEnabled(autoDismissNotice);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        seekBar.setMax(9000);
        seekBar.setProgress((int) (noticeDuration - offset));
        int padding = DimenUtils.dpToPx(12);
        seekBar.setPadding(padding, 0, padding, 0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int rounded = (progress / 100) * 100;
                seekBar.setProgress(rounded);
                currentTimeout.setText((rounded + offset) + " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                settings.setLong("noticeDuration", seekBar.getProgress() + offset);
            }
        });

        // Title
        TextView title = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Label);
        title.setText("Custom Notice Duration");
        addView(title);

        // Auto-dismiss checkbox
        CheckedSetting autoDismiss = Utils.createCheckedSetting(
            ctx,
            CheckedSetting.ViewType.SWITCH,
            "Auto-Dismiss Notice",
            "Whether the notice should automatically dismiss"
        );
        autoDismiss.setIsChecked(autoDismissNotice);
        autoDismiss.setOnCheckedListener(isChecked -> {
            settings.setBool("autoDismissNotice", isChecked);
            seekBar.setEnabled(isChecked);
        });
        addView(autoDismiss);

        // Seekbar layout
        LinearLayout seekBarLayout = new LinearLayout(ctx, null, 0, R.i.UiKit_Settings_Item);
        seekBarLayout.addView(currentTimeout);
        seekBarLayout.addView(seekBar);
        addView(seekBarLayout);
    }
}
