package twitchvotesminecraft;

import org.bukkit.plugin.java.JavaPlugin;
import twitchvotesminecraft.auth.TwitchValidator;
import twitchvotesminecraft.config.ConfigManager;

public final class App extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        // 1. Validate and repair default settings in config.yml
        ConfigManager.validateAndRepairDefaultSettings(this);

        // 2. Validate Twitch credentials (client-id, access-token, refresh-token)
        TwitchValidator.ValidationResult result = TwitchValidator.validateCredentials(getConfig());
        if (!result.isValid()) {
            getLogger().severe("=================================================");
            getLogger().severe("TwitchVotesMinecraft failed to start!");
            getLogger().severe("Reason: " + result.message());
            getLogger().severe("Please configure valid Twitch credentials in config.yml.");
            getLogger().severe("=================================================");

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Twitch credentials validated successfully. TwitchVotesMinecraft enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("TwitchVotesMinecraft disabled.");
    }
}
