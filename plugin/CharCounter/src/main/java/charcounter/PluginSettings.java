package charcounter;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import com.aliucord.Utils;
import com.aliucord.api.SettingsAPI;
import com.aliucord.utils.DimenUtils;
import com.aliucord.views.TextInput;
import com.aliucord.widgets.BottomSheet;
import com.discord.views.CheckedSetting;
import com.lytefast.flexinput.R;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
public class PluginSettings extends BottomSheet {
    private final SettingsAPI settings;

    public PluginSettings(SettingsAPI settings) {
        this.settings = settings;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        android.content.Context ctx = requireContext();

        // Reverse checkbox
        CheckedSetting reverseCheck = Utils.createCheckedSetting(
            ctx,
            CheckedSetting.ViewType.SWITCH,
            "Reverse",
            "Whether the counter goes in reverse, counting down how many chars remain"
        );
        reverseCheck.setIsChecked(settings.getBool("reverse", false));
        reverseCheck.setOnCheckedListener(isChecked -> settings.setBool("reverse", isChecked));
        addView(reverseCheck);

        // Threshold input
        TextInput textInput = new TextInput(ctx);
        textInput.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        textInput.getEditText().setText(String.valueOf(settings.getInt("threshold", 1)));
        textInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(s.toString());
                    settings.setInt("threshold", value);
                } catch (NumberFormatException ignored) {}
            }
        });
        textInput.setHint("Threshold");
        int padding = DimenUtils.dpToPx(8);
        textInput.setPadding(padding, padding, padding, padding);
        addView(textInput);

        // Description
        TextView description = new TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Addition);
        description.setText("Minimum number of characters for the counter to appear. Set to zero for it to always be visible");
        addView(description);
    }
}
