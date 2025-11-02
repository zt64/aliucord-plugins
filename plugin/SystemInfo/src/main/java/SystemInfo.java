import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateUtils;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.CommandsAPI.CommandResult;
import com.aliucord.entities.MessageEmbedBuilder;
import com.aliucord.entities.Plugin;
import com.discord.api.commands.ApplicationCommandType;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

@AliucordPlugin
public class SystemInfo extends Plugin {
    
    private static long toGB(long bytes) {
        return (long) (bytes / 1e+9);
    }
    
    private static String toFixed(double value) {
        return String.format("%.2f", value);
    }
    
    private static boolean isRooted() {
        String path = System.getenv("PATH");
        if (path == null) return false;
        
        for (String dir : path.split(":")) {
            if (new File(dir, "su").exists()) {
                return true;
            }
        }
        return false;
    }
    
    private static String getArch() {
        for (String abi : Build.SUPPORTED_ABIS) {
            switch (abi) {
                case "arm64-v8a":
                    return "aarch64";
                case "armeabi-v7a":
                    return "arm";
                case "x86_64":
                    return "x86_64";
                case "x86":
                    return "i686";
            }
        }
        
        String arch = System.getProperty("os.arch");
        if (arch != null) return arch;
        
        arch = System.getProperty("ro.product.cpu.abi");
        if (arch != null) return arch;
        
        return "Unknown Architecture";
    }

    @Override
    public void start(Context context) {
        commands.registerCommand(
            "system-info",
            "Get system information",
            Utils.createCommandOption(
                ApplicationCommandType.BOOLEAN,
                "send",
                "send result visible to everyone"
            ),
            ctx -> {
                ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                am.getMemoryInfo(memInfo);

                long totalMem = memInfo.totalMem;
                long availMem = memInfo.availMem;
                long usedMem = totalMem - availMem;
                String percentAvail = toFixed((availMem / (double) totalMem) * 100);

                Map<String, String> info = new LinkedHashMap<>();
                info.put("Brand", Build.BRAND);
                info.put("Product", Build.PRODUCT);
                info.put("Board", Build.BOARD);
                info.put("Architecture", getArch());
                info.put("Bootloader", Build.BOOTLOADER);
                info.put("Rooted", String.valueOf(isRooted()));
                info.put("OS Version", Build.VERSION.CODENAME + " " + Build.VERSION.RELEASE + " (SDK v" + Build.VERSION.SDK_INT + ")");
                info.put("Memory Usage", toGB(usedMem) + "/" + toGB(totalMem) + "GB (" + toGB(availMem) + "GB / " + percentAvail + "% free)");
                info.put("Uptime", DateUtils.formatElapsedTime(SystemClock.elapsedRealtime() / 1000));

                if (ctx.getBoolOrDefault("send", false)) {
                    StringBuilder sb = new StringBuilder("**__System Info:__**\n\n");
                    for (Map.Entry<String, String> entry : info.entrySet()) {
                        sb.append("**");
                        sb.append(entry.getKey());
                        sb.append(":** ");
                        sb.append(entry.getValue());
                        sb.append('\n');
                    }
                    return new CommandResult(sb.toString(), null, true);
                } else {
                    MessageEmbedBuilder builder = new MessageEmbedBuilder();
                    builder.setColor(0x00FFA200);
                    builder.setTitle("System Info");
                    for (Map.Entry<String, String> entry : info.entrySet()) {
                        builder.addField(entry.getKey(), entry.getValue(), true);
                    }
                    return new CommandResult(null, java.util.Collections.singletonList(builder.build()), false);
                }
            }
        );
    }

    @Override
    public void stop(Context context) {
        commands.unregisterAll();
    }
}
