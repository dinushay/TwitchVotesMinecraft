package twitchvotesminecraft.config;

import org.bukkit.configuration.file.FileConfiguration;
import twitchvotesminecraft.App;

public final class ConfigManager {

    private ConfigManager() {}

    /**
     * Validates and repairs default settings on the provided configuration object.
     * @return true if any setting was repaired/re-created, false otherwise.
     */
    public static boolean validateAndRepairDefaultSettings(FileConfiguration config) {
        boolean changed = false;

        // 1. Remove legacy mode setting if present
        String modePath = "twitch.default-settings.mode";
        if (config.contains(modePath)) {
            config.set(modePath, null);
            changed = true;
        }

        // 2. Check twitch.default-settings.interval-seconds (15 - 120)
        String intervalPath = "twitch.default-settings.interval-seconds";
        if (!config.isInt(intervalPath)) {
            config.set(intervalPath, 90);
            changed = true;
        } else {
            int val = config.getInt(intervalPath);
            if (val < 15 || val > 120) {
                config.set(intervalPath, 90);
                changed = true;
            }
        }

        // 3. Check twitch.default-settings.event-seconds (15 - 120)
        String eventSecondsPath = "twitch.default-settings.event-seconds";
        if (!config.isInt(eventSecondsPath)) {
            config.set(eventSecondsPath, 60);
            changed = true;
        } else {
            int val = config.getInt(eventSecondsPath);
            if (val < 15 || val > 120) {
                config.set(eventSecondsPath, 60);
                changed = true;
            }
        }

        // 4. Check twitch.default-settings.vote-seconds (15 - 120)
        String voteSecondsPath = "twitch.default-settings.vote-seconds";
        if (!config.isInt(voteSecondsPath)) {
            config.set(voteSecondsPath, 30);
            changed = true;
        } else {
            int val = config.getInt(voteSecondsPath);
            if (val < 15 || val > 120) {
                config.set(voteSecondsPath, 30);
                changed = true;
            }
        }

        // 5. Enforce constraint: (event-seconds + vote-seconds) <= interval-seconds
        int intervalVal = config.getInt(intervalPath);
        int eventVal = config.getInt(eventSecondsPath);
        int voteVal = config.getInt(voteSecondsPath);

        if (eventVal + voteVal > intervalVal) {
            int requiredInterval = eventVal + voteVal;
            if (requiredInterval <= 120) {
                config.set(intervalPath, requiredInterval);
            } else {
                config.set(intervalPath, 90);
                config.set(eventSecondsPath, 60);
                config.set(voteSecondsPath, 30);
            }
            changed = true;
        }

        // 6. Check twitch.default-settings.max-voteable-events (2 - 5)
        String maxEventsPath = "twitch.default-settings.max-voteable-events";
        if (!config.isInt(maxEventsPath)) {
            config.set(maxEventsPath, 4);
            changed = true;
        } else {
            int val = config.getInt(maxEventsPath);
            if (val < 2 || val > 5) {
                config.set(maxEventsPath, 4);
                changed = true;
            }
        }

        // 7. Check twitch.default-settings.show-poll-in-minecraft (boolean)
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
