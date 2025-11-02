import android.content.Context;
import android.util.Base64;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.CommandsAPI.CommandResult;
import com.aliucord.entities.Plugin;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.stores.StoreStream;
import com.discord.utilities.rest.RestAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings("MISSING_DEPENDENCY_SUPERCLASS")
@AliucordPlugin
public class Token extends Plugin {
    @Override
    public void start(Context context) {
        List<Object> options = Arrays.asList(
            Utils.createCommandOption(
                ApplicationCommandType.BOOLEAN,
                "send",
                "Send visible to everyone"
            )
        );

        commands.registerCommand("token", "Tells you your token", options, ctx -> {
            if (ctx.getBoolOrDefault("send", false)) {
                return new CommandResult(genFakeToken(), null, true);
            } else {
                try {
                    return new CommandResult(
                        "```\n" + RestAPI.AppHeadersProvider.INSTANCE.getAuthToken() + "```",
                        null,
                        false
                    );
                } catch (Exception e) {
                    logger.error(e);
                    return new CommandResult("Uh oh, failed to get token", null, false);
                }
            }
        });
    }

    // imagine if this somehow generates the actual token that'd be pretty funny dont u think
    private String genFakeToken() {
        byte[] id = String.valueOf(StoreStream.getUsers().getMe().getId()).getBytes();
        StringBuilder sb = new StringBuilder(Base64.encodeToString(id, Base64.DEFAULT).replace("\n", ""))
            .append('.');

        List<Character> chars = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) chars.add(c);
        for (char c = 'a'; c <= 'z'; c++) chars.add(c);
        for (char c = '0'; c <= '9'; c++) chars.add(c);
        chars.add('_');
        chars.add('-');

        Random random = new Random();
        for (int i = 1; i <= 7 + 28; i++) {
            if (i == 8) {
                sb.append('.');
            } else {
                sb.append(chars.get(random.nextInt(chars.size())));
            }
        }

        return sb.toString();
    }

    @Override
    public void stop(Context context) {
        commands.unregisterAll();
    }
}
