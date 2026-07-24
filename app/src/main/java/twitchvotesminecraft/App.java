package twitchvotesminecraft;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import twitchvotesminecraft.auth.TwitchValidator;
import twitchvotesminecraft.command.TwitchCommand;
import twitchvotesminecraft.config.ConfigManager;
import twitchvotesminecraft.gui.GUIListener;
import twitchvotesminecraft.vote.VoteSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class App extends JavaPlugin {

    private final Map<UUID, VoteSession> activeSessions = new ConcurrentHashMap<>();

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
        stopVoteSession(player);
        VoteSession session = new VoteSession(this, player, channel);
        activeSessions.put(player.getUniqueId(), session);
        session.start();
    }

    public synchronized boolean stopVoteSession(Player player) {
        VoteSession session = activeSessions.remove(player.getUniqueId());
        if (session != null) {
            session.stop();
            return true;
        }
        return false;
    }

    public boolean hasActiveSession(Player player) {
        return activeSessions.containsKey(player.getUniqueId());
    }

    @Override
    public void onDisable() {
        for (VoteSession session : activeSessions.values()) {
            session.stop();
        }
        activeSessions.clear();
        getLogger().info("TwitchVotesMinecraft disabled.");
    }
}
