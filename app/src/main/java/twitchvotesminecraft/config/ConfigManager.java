package twitchvotesminecraft.config;

import org.bukkit.configuration.file.FileConfiguration;
import twitchvotesminecraft.App;

import java.util.ArrayList;
import java.util.List;

public final class ConfigManager {

    private ConfigManager() {}

    /**
     * Validates and repairs default settings on the provided configuration object.
     * @return true if any setting was repaired/re-created, false otherwise.
     */
    public static boolean validateAndRepairDefaultSettings(FileConfiguration config) {
        boolean changed = false;

        // 1. Check twitch.default-settings.mode
        String modePath = "twitch.default-settings.mode";
        if (!config.isString(modePath) || config.getString(modePath, "").trim().isEmpty()) {
            config.set(modePath, "chat");
            changed = true;
        }

        // 2. Check twitch.default-settings.interval-seconds
        String intervalPath = "twitch.default-settings.interval-seconds";
        if (!config.isInt(intervalPath) || config.getInt(intervalPath) <= 0) {
            config.set(intervalPath, 30);
            changed = true;
        }

        // 3. Check twitch.default-settings.show-poll-in-minecraft
        String showPollPath = "twitch.default-settings.show-poll-in-minecraft";
        if (!config.isBoolean(showPollPath)) {
            config.set(showPollPath, false);
            changed = true;
        }

        return changed;
    }

    /**
     * Validates default settings in config.yml for the plugin.
     * Re-creates/repairs any option under twitch.default-settings if missing or invalid.
     */
    public static void validateAndRepairDefaultSettings(App plugin) {
        FileConfiguration config = plugin.getConfig();
        boolean changed = validateAndRepairDefaultSettings(config);

        if (changed) {
            plugin.saveConfig();
            plugin.getLogger().warning("Some default settings were missing or invalid and have been recreated.");
        }
    }
}
