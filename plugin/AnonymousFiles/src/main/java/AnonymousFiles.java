import android.content.Context;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.lytefast.flexinput.model.Attachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@AliucordPlugin
public class AnonymousFiles extends Plugin {
    private static final int LENGTH = 8;
    private final List<Character> charPool;
    private final Random random;

    public AnonymousFiles() {
        charPool = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; c++) charPool.add(c);
        for (char c = 'A'; c <= 'Z'; c++) charPool.add(c);
        for (char c = '0'; c <= '9'; c++) charPool.add(c);
        random = new Random();
    }

    @Override
    public void start(Context context) {
        patcher.after(Attachment.class, "getDisplayName", (callFrame) -> {
            String result = (String) callFrame.getResult();
            String ext = result.substring(result.lastIndexOf('.'));

            StringBuilder str = new StringBuilder();
            for (int i = 0; i < LENGTH; i++) {
                str.append(charPool.get(random.nextInt(charPool.size())));
            }

            callFrame.setResult(str.toString() + ext);
        });
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
    }
}
