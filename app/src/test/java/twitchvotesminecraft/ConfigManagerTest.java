package twitchvotesminecraft;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import twitchvotesminecraft.config.ConfigManager;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {

    @Test
    void testRepairsMissingDefaultSettings() {
        YamlConfiguration config = new YamlConfiguration();
        
        // Initially empty config
        boolean repaired = ConfigManager.validateAndRepairDefaultSettings(config);

        assertTrue(repaired);
        assertEquals("chat", config.getString("twitch.default-settings.mode"));
        assertEquals(30, config.getInt("twitch.default-settings.interval-seconds"));
        assertFalse(config.getBoolean("twitch.default-settings.show-poll-in-minecraft"));
    }

    @Test
    void testRepairsInvalidDefaultSettings() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("twitch.default-settings.mode", "");
        config.set("twitch.default-settings.interval-seconds", -10);
        config.set("twitch.default-settings.show-poll-in-minecraft", "not_a_boolean");

        boolean repaired = ConfigManager.validateAndRepairDefaultSettings(config);

        assertTrue(repaired);
        assertEquals("chat", config.getString("twitch.default-settings.mode"));
        assertEquals(30, config.getInt("twitch.default-settings.interval-seconds"));
        assertFalse(config.getBoolean("twitch.default-settings.show-poll-in-minecraft"));
    }

    @Test
    void testValidDefaultSettingsNotModified() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("twitch.default-settings.mode", "poll");
        config.set("twitch.default-settings.interval-seconds", 60);
        config.set("twitch.default-settings.show-poll-in-minecraft", true);

        boolean repaired = ConfigManager.validateAndRepairDefaultSettings(config);

        assertFalse(repaired);
        assertEquals("poll", config.getString("twitch.default-settings.mode"));
        assertEquals(60, config.getInt("twitch.default-settings.interval-seconds"));
        assertTrue(config.getBoolean("twitch.default-settings.show-poll-in-minecraft"));
    }
}
