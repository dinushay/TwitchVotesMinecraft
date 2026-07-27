package twitchvotesminecraft.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import twitchvotesminecraft.App;

public class GUIListener implements Listener {

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
                int oldValue = holder.getIntervalSeconds();
                if (click == ClickType.SHIFT_LEFT) {
                    int newValue = Math.min(120, oldValue + 15);
                    holder.setIntervalSeconds(newValue);
                    playClickSound(player, oldValue, newValue, true);
                } else if (click == ClickType.SHIFT_RIGHT) {
                    int newValue = Math.max(15, oldValue - 15);
                    holder.setIntervalSeconds(newValue);
                    playClickSound(player, oldValue, newValue, false);
                } else if (click.isLeftClick()) {
                    int newValue = Math.min(120, oldValue + 5);
                    holder.setIntervalSeconds(newValue);
                    playClickSound(player, oldValue, newValue, true);
                } else if (click.isRightClick()) {
                    int newValue = Math.max(15, oldValue - 5);
                    holder.setIntervalSeconds(newValue);
                    playClickSound(player, oldValue, newValue, false);
                }
            }
            case 11 -> { // Event Seconds (15 - 120)
                int oldValue = holder.getEventSeconds();
                if (click == ClickType.SHIFT_LEFT) {
                    int newValue = Math.min(120, oldValue + 15);
                    holder.setEventSeconds(newValue);
                    playClickSound(player, oldValue, newValue, true);
                } else if (click == ClickType.SHIFT_RIGHT) {
                    int newValue = Math.max(15, oldValue - 15);
                    holder.setEventSeconds(newValue);
                    playClickSound(player, oldValue, newValue, false);
                } else if (click.isLeftClick()) {
                    int newValue = Math.min(120, oldValue + 5);
                    holder.setEventSeconds(newValue);
                    playClickSound(player, oldValue, newValue, true);
                } else if (click.isRightClick()) {
                    int newValue = Math.max(15, oldValue - 5);
                    holder.setEventSeconds(newValue);
                    playClickSound(player, oldValue, newValue, false);
                }
            }
            case 12 -> { // Vote Seconds (15 - 120)
                int oldValue = holder.getVoteSeconds();
                if (click == ClickType.SHIFT_LEFT) {
                    int newValue = Math.min(120, oldValue + 15);
                    holder.setVoteSeconds(newValue);
                    playClickSound(player, oldValue, newValue, true);
                } else if (click == ClickType.SHIFT_RIGHT) {
                    int newValue = Math.max(15, oldValue - 15);
                    holder.setVoteSeconds(newValue);
                    playClickSound(player, oldValue, newValue, false);
                } else if (click.isLeftClick()) {
                    int newValue = Math.min(120, oldValue + 5);
                    holder.setVoteSeconds(newValue);
                    playClickSound(player, oldValue, newValue, true);
                } else if (click.isRightClick()) {
                    int newValue = Math.max(15, oldValue - 5);
                    holder.setVoteSeconds(newValue);
                    playClickSound(player, oldValue, newValue, false);
                }
            }
            case 13 -> { // Max Voteable Events (2 - 5)
                int oldValue = holder.getMaxVoteableEvents();
                if (click.isLeftClick()) {
                    int newValue = Math.min(5, oldValue + 1);
                    holder.setMaxVoteableEvents(newValue);
                    playClickSound(player, oldValue, newValue, true);
                } else if (click.isRightClick()) {
                    int newValue = Math.max(2, oldValue - 1);
                    holder.setMaxVoteableEvents(newValue);
                    playClickSound(player, oldValue, newValue, false);
                }
            }
            case 16 -> { // Confirm & Save -> Start Voting Session
                int eventSec = holder.getEventSeconds();
                int voteSec = holder.getVoteSeconds();
                int intervalSec = holder.getIntervalSeconds();
                int sum = eventSec + voteSec;

                if (sum > intervalSec) {
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    player.sendMessage(plugin.getMessageManager().getComponent("gui.cannot-save-msg", java.util.Map.of(
                            "%event%", String.valueOf(eventSec),
                            "%vote%", String.valueOf(voteSec),
                            "%sum%", String.valueOf(sum),
                            "%interval%", String.valueOf(intervalSec)
                    )));
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
                player.sendMessage(plugin.getMessageManager().getComponent("gui.saved-msg"));
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

        SettingsGUI.refreshInventory(plugin, holder);
    }

    private void playClickSound(Player player, int oldValue, int newValue, boolean isIncrease) {
        if (oldValue == newValue) {
            // Boundary hit (no change) - play negative error sound
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        } else {
            // Value changed - play normal click sound with pitch change based on direction
            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, isIncrease ? 1.2f : 0.8f);
        }
    }
}
