package twitchvotesminecraft.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import twitchvotesminecraft.App;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();
    private final App plugin;
    private FileConfiguration messageConfig;
    private File messageFile;

    public MessageManager(App plugin) {
        this.plugin = plugin;
        saveDefaultMessages();
        reloadMessages();
    }

    private void saveDefaultMessages() {
        messageFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messageFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    public void reloadMessages() {
        messageConfig = YamlConfiguration.loadConfiguration(messageFile);
        InputStream defConfigStream = plugin.getResource("messages.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            messageConfig.setDefaults(defConfig);
        }
    }

    public Component getComponent(String path, Map<String, String> placeholders) {
        String raw = messageConfig.getString(path, "&cMissing message: " + path);

        // Convert custom placeholders
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                raw = raw.replace(entry.getKey(), entry.getValue());
            }
        }

        // Replace & with § for legacy color codes
        raw = raw.replace("&", "§");

        return SERIALIZER.deserialize(raw);
    }

    public Component getComponent(String path) {
        return getComponent(path, null);
    }

    public String getString(String path, Map<String, String> placeholders) {
        String raw = messageConfig.getString(path, "&cMissing message: " + path);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                raw = raw.replace(entry.getKey(), entry.getValue());
            }
        }
        return raw.replace("&", "§");
    }

    public String getString(String path) {
        return getString(path, null);
    }
}
