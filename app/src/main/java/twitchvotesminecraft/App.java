package twitchvotesminecraft;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import twitchvotesminecraft.auth.TwitchValidator;
import twitchvotesminecraft.command.TwitchCommand;
import twitchvotesminecraft.config.ConfigManager;
import twitchvotesminecraft.gui.GUIListener;
import twitchvotesminecraft.vote.VoteSession;

public final class App extends JavaPlugin {

    private VoteSession activeSession;

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

        // 4. Register Command via Paper CommandMap
        getServer().getCommandMap().register("twitchvotesminecraft", new TwitchCommand(this));

        getLogger().info("Twitch credentials validated successfully. TwitchVotesMinecraft enabled.");
    }

    public synchronized void startVoteSession(Player player, String channel) {
        if (activeSession != null) {
            activeSession.stop();
        }
        activeSession = new VoteSession(this, player, channel);
        activeSession.start();
    }

    @Override
    public void onDisable() {
        if (activeSession != null) {
            activeSession.stop();
            activeSession = null;
        }
        getLogger().info("TwitchVotesMinecraft disabled.");
    }
}
