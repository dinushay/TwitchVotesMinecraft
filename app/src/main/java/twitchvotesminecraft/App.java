package twitchvotesminecraft;

import org.bukkit.plugin.java.JavaPlugin;

public final class App extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("TwitchVotesMinecraft enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("TwitchVotesMinecraft disabled.");
    }
}
