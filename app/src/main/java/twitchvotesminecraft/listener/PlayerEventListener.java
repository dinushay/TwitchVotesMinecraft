package twitchvotesminecraft.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import twitchvotesminecraft.App;

public class PlayerEventListener implements Listener {

    private final App plugin;

    public PlayerEventListener(App plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (plugin.hasActiveSession(player)) {
            plugin.stopVoteSession(player);
            player.sendMessage(plugin.getMessageManager().getComponent("session.player-died"));
        }
    }
}
