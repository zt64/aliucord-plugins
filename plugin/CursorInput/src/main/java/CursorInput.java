import android.content.Context;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.widgets.chat.input.AppFlexInputViewModel;
import com.lytefast.flexinput.R;
import com.lytefast.flexinput.widget.FlexEditText;

@AliucordPlugin
public class CursorInput extends Plugin {
    private FlexEditText flexEditText;

    @Override
    public void start(Context context) {
        // Patch FlexInputFragment inner class to capture FlexEditText
        try {
            Class<?> innerClass = null;
            for (Class<?> clazz : com.lytefast.flexinput.fragment.FlexInputFragment.class.getDeclaredClasses()) {
                if (clazz.getName().contains("$c")) {
                    innerClass = clazz;
                    break;
                }
            }
            
            if (innerClass != null) {
                patcher.after(innerClass, "invoke", (callFrame) -> {
                    Object result = callFrame.getResult();
                    try {
                        // Access root field to get FlexEditText
                        java.lang.reflect.Field rootField = result.getClass().getField("root");
                        Object root = rootField.get(result);
                        flexEditText = ((android.view.View) root).findViewById(R.f.text_input);
                    } catch (Exception e) {
                        logger.error("Failed to get FlexEditText", e);
                    }
                }, Object.class);
            }
        } catch (Exception e) {
            logger.error("Failed to patch FlexInputFragment", e);
        }

        patcher.instead(AppFlexInputViewModel.class, "onInputTextAppended", (callFrame) -> {
            AppFlexInputViewModel viewModel = (AppFlexInputViewModel) callFrame.thisObject;
            String str = (String) callFrame.args[0];
            
            try {
                java.lang.reflect.Method requireViewStateMethod = AppFlexInputViewModel.class.getDeclaredMethod("requireViewState");
                requireViewStateMethod.setAccessible(true);
                Object viewState = requireViewStateMethod.invoke(viewModel);
                
                java.lang.reflect.Field aField = viewState.getClass().getField("a");
                String baseString = (String) aField.get(viewState);
                
                if (flexEditText == null) {
                    viewModel.onInputTextChanged(baseString + str, null);
                } else {
                    int selectionEnd = flexEditText.getSelectionEnd();
                    String trimmed = selectionEnd != baseString.length() ? str.trim() : str;
                    
                    StringBuilder newText = new StringBuilder(baseString);
                    newText.insert(selectionEnd, trimmed);
                    
                    viewModel.onInputTextChanged(newText.toString(), null);
                    flexEditText.setSelection(selectionEnd + trimmed.length());
                }
            } catch (Exception e) {
                logger.error("Failed to handle input text append", e);
            }
            
            return null;
        }, String.class);
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
