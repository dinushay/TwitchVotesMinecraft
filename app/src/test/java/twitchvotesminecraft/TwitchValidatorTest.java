package twitchvotesminecraft;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import twitchvotesminecraft.auth.TwitchValidator;

import static org.junit.jupiter.api.Assertions.*;

class TwitchValidatorTest {

    @Test
    void testEmptyCredentialsFailValidation() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("twitch.client-id", "");
        config.set("twitch.access-token", "");
        config.set("twitch.refresh-token", "");

        TwitchValidator.ValidationResult result = TwitchValidator.validateCredentials(config);

        assertFalse(result.isValid());
        assertTrue(result.message().contains("client-id"));
    }

    @Test
    void testPlaceholderCredentialsFailValidation() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("twitch.client-id", "YOUR_CLIENT_ID");
        config.set("twitch.access-token", "valid_access_token_123");
        config.set("twitch.refresh-token", "valid_refresh_token_456");

        TwitchValidator.ValidationResult result = TwitchValidator.validateCredentials(config);

        assertFalse(result.isValid());
        assertTrue(result.message().contains("client-id"));
    }

    @Test
    void testMissingRefreshTokenFailsValidation() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("twitch.client-id", "some_client_id");
        config.set("twitch.access-token", "some_access_token");
        config.set("twitch.refresh-token", "");

        TwitchValidator.ValidationResult result = TwitchValidator.validateCredentials(config);

        assertFalse(result.isValid());
        assertTrue(result.message().contains("refresh-token"));
    }
}
