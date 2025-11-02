package dev.zt64.aliucord.plugins;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.utils.DimenUtils;
import com.lytefast.flexinput.R;

@AliucordPlugin
public class AttachmentSheetTweaks extends Plugin {
    private TextView textView;

    @Override
    public void start(Context context) {
        // Find AddContentDialogFragment class
        Class<?> addContentDialogClass = null;
        try {
            addContentDialogClass = Class.forName("b.b.a.a.a");
        } catch (ClassNotFoundException e) {
            logger.error("Failed to find AddContentDialogFragment", e);
            return;
        }

        patcher.after(addContentDialogClass, "onCreateView", (callFrame) -> {
            CoordinatorLayout root = (CoordinatorLayout) callFrame.getResult();
            if (root == null) return;

            textView = new TextView(root.getContext(), null, 0, R.i.UiKit_TextView_Medium);
            CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setAnchorId(R.f.content_pager);
            params.anchorGravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            params.setPadding(0, 0, 0, DimenUtils.dpToPx(20));
            textView.setLayoutParams(params);

            root.addView(textView);
        }, LayoutInflater.class, ViewGroup.class, Bundle.class);

        // Patch the run method
        try {
            Class<?> runnableClass = Class.forName("b.b.a.a.d");
            patcher.after(runnableClass, "run", (callFrame) -> {
                try {
                    Object thisObj = callFrame.thisObject;
                    java.lang.reflect.Field jField = thisObj.getClass().getDeclaredField("j");
                    jField.setAccessible(true);
                    Object j = jField.get(thisObj);
                    
                    java.lang.reflect.Field oField = j.getClass().getDeclaredField("o");
                    oField.setAccessible(true);
                    Object selectionAggregator = oField.get(j);
                    
                    java.lang.reflect.Method sizeMethod = selectionAggregator.getClass().getMethod("size");
                    int size = (int) sizeMethod.invoke(selectionAggregator);

                    if (textView != null) {
                        textView.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
                        textView.setText(size + " selected");
                    }
                } catch (Exception e) {
                    logger.error("Failed to update text view", e);
                }
            });
        } catch (ClassNotFoundException e) {
            logger.error("Failed to find runnable class", e);
        }
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
