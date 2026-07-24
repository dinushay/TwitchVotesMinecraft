package twitchvotesminecraft;

import org.bukkit.plugin.java.JavaPlugin;
import twitchvotesminecraft.auth.TwitchValidator;
import twitchvotesminecraft.command.TwitchCommand;
import twitchvotesminecraft.config.ConfigManager;
import twitchvotesminecraft.gui.GUIListener;

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

        // 3. Register GUI Event Listener
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        // 4. Register Command via Paper CommandMap (avoids JavaPlugin#getCommand UnsupportedOperationException)
        getServer().getCommandMap().register("twitchvotesminecraft", new TwitchCommand(this));

        getLogger().info("Twitch credentials validated successfully. TwitchVotesMinecraft enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("TwitchVotesMinecraft disabled.");
    }
}
