package twitchvotesminecraft.gui;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import twitchvotesminecraft.App;

public class GUIListener implements Listener {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();
    private final App plugin;

    public GUIListener(App plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SettingsGUIHolder holder)) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getInventory())) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getSlot();
        ClickType click = event.getClick();

        switch (slot) {
            case 10 -> { // Interval Seconds (15 - 120)
                int delta = click.isShiftClick() ? 15 : 5;
                if (click.isLeftClick()) {
                    holder.setIntervalSeconds(Math.min(120, holder.getIntervalSeconds() + delta));
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
                } else if (click.isRightClick()) {
                    holder.setIntervalSeconds(Math.max(15, holder.getIntervalSeconds() - delta));
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
                }
            }
            case 11 -> { // Event Seconds (15 - 120)
                int delta = click.isShiftClick() ? 15 : 5;
                if (click.isLeftClick()) {
                    holder.setEventSeconds(Math.min(120, holder.getEventSeconds() + delta));
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
                } else if (click.isRightClick()) {
                    holder.setEventSeconds(Math.max(15, holder.getEventSeconds() - delta));
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
                }
            }
            case 12 -> { // Vote Seconds (15 - 120)
                int delta = click.isShiftClick() ? 15 : 5;
                if (click.isLeftClick()) {
                    holder.setVoteSeconds(Math.min(120, holder.getVoteSeconds() + delta));
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
                } else if (click.isRightClick()) {
                    holder.setVoteSeconds(Math.max(15, holder.getVoteSeconds() - delta));
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
                }
            }
            case 13 -> { // Max Voteable Events (2 - 5)
                if (click.isLeftClick()) {
                    holder.setMaxVoteableEvents(Math.min(5, holder.getMaxVoteableEvents() + 1));
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
                } else if (click.isRightClick()) {
                    holder.setMaxVoteableEvents(Math.max(2, holder.getMaxVoteableEvents() - 1));
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
                }
            }
            case 16 -> { // Confirm & Save -> Start Voting Session
                int eventSec = holder.getEventSeconds();
                int voteSec = holder.getVoteSeconds();
                int intervalSec = holder.getIntervalSeconds();
                int sum = eventSec + voteSec;

                if (sum > intervalSec) {
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    player.sendMessage(SERIALIZER.deserialize(
                            "§c[TwitchVotesMinecraft] Cannot save! Event Seconds (" + eventSec + "s) + Vote Seconds (" + voteSec + "s) = "
                            + sum + "s, which exceeds Interval Seconds (" + intervalSec + "s)."
                    ));
                    return;
                }

                // Ensure legacy keys are cleaned up if present
                plugin.getConfig().set("twitch.default-settings.mode", null);
                plugin.getConfig().set("twitch.default-settings.show-poll-in-minecraft", null);

                plugin.getConfig().set("twitch.default-settings.interval-seconds", intervalSec);
                plugin.getConfig().set("twitch.default-settings.event-seconds", eventSec);
                plugin.getConfig().set("twitch.default-settings.vote-seconds", voteSec);
                plugin.getConfig().set("twitch.default-settings.max-voteable-events", holder.getMaxVoteableEvents());

                plugin.saveConfig();
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.sendMessage(SERIALIZER.deserialize("§a[TwitchVotesMinecraft] Default settings successfully saved to config.yml!"));
                player.closeInventory();

                // Start the voting session for the configured Twitch channel
                plugin.startVoteSession(player, holder.getTwitchName());
                return;
            }
            case 26 -> { // Close Button
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_CHEST_CLOSE, 1.0f, 1.0f);
                player.closeInventory();
                return;
            }
        }

        SettingsGUI.refreshInventory(holder);
    }
}
