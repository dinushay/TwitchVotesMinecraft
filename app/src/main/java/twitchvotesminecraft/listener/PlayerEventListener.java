package twitchvotesminecraft.listener;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import twitchvotesminecraft.App;

public class PlayerEventListener implements Listener {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();
    private final App plugin;

    public PlayerEventListener(App plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (plugin.hasActiveSession(player)) {
            plugin.stopVoteSession(player);
            player.sendMessage(SERIALIZER.deserialize("§c[TwitchVotesMinecraft] You died! Active Twitch voting session has been cancelled."));
        }
    }
}
