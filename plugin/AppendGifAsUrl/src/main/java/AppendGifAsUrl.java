import android.content.Context;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.discord.widgets.chat.input.AppFlexInputViewModel;
import com.discord.widgets.chat.input.gifpicker.GifAdapterItem;
import com.discord.widgets.chat.input.gifpicker.WidgetGifCategory;
import com.discord.widgets.chat.input.gifpicker.WidgetGifPickerSearch;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class AppendGifAsUrl extends Plugin {
    private AppFlexInputViewModel appFlexInputViewModel;

    @Override
    public void start(Context context) {
        // Patch constructor to capture instance
        Constructor<?>[] constructors = AppFlexInputViewModel.class.getConstructors();
        if (constructors.length > 0) {
            patcher.patch(constructors[0], (callFrame) -> {
                appFlexInputViewModel = (AppFlexInputViewModel) callFrame.thisObject;
            });
        }

        // Patch WidgetGifPickerSearch inner class
        try {
            Class<?> innerClass = null;
            for (Class<?> clazz : WidgetGifPickerSearch.class.getDeclaredClasses()) {
                if (clazz.getName().contains("$setUpGifRecycler$1")) {
                    innerClass = clazz;
                    break;
                }
            }
            
            if (innerClass != null) {
                patcher.instead(innerClass, "invoke", (callFrame) -> {
                    GifAdapterItem.GifItem gifItem = (GifAdapterItem.GifItem) callFrame.args[0];
                    
                    if (appFlexInputViewModel != null) {
                        appFlexInputViewModel.onInputTextAppended(gifItem.getGif().getTenorGifUrl() + " ");
                    }
                    
                    // Access outer class
                    Field outerField = callFrame.thisObject.getClass().getDeclaredField("this$0");
                    outerField.setAccessible(true);
                    WidgetGifPickerSearch outerInstance = (WidgetGifPickerSearch) outerField.get(callFrame.thisObject);
                    
                    // Call onGifSelected
                    Method getOnGifSelectedMethod = WidgetGifPickerSearch.class.getDeclaredMethod("access$getOnGifSelected$p", WidgetGifPickerSearch.class);
                    getOnGifSelectedMethod.setAccessible(true);
                    Object onGifSelected = getOnGifSelectedMethod.invoke(null, outerInstance);
                    
                    if (onGifSelected != null) {
                        Method invokeMethod = onGifSelected.getClass().getMethod("invoke");
                        invokeMethod.invoke(onGifSelected);
                    }
                    
                    return null;
                }, GifAdapterItem.GifItem.class);
            }
        } catch (Exception e) {
            logger.error("Failed to patch WidgetGifPickerSearch", e);
        }

        // Patch WidgetGifCategory
        patcher.instead(WidgetGifCategory.class, "selectGif", (callFrame) -> {
            GifAdapterItem.GifItem gifItem = (GifAdapterItem.GifItem) callFrame.args[0];
            
            if (appFlexInputViewModel != null) {
                appFlexInputViewModel.onInputTextAppended(gifItem.getGif().getTenorGifUrl() + " ");
            }
            
            return null;
        }, GifAdapterItem.GifItem.class);
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
