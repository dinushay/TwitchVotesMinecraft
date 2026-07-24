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
                if (click.isLeftClick()) {
                    holder.setIntervalSeconds(Math.min(120, holder.getIntervalSeconds() + 5));
                } else if (click.isRightClick()) {
                    holder.setIntervalSeconds(Math.max(15, holder.getIntervalSeconds() - 5));
                }
            }
            case 11 -> { // Event Seconds (15 - 120)
                if (click.isLeftClick()) {
                    holder.setEventSeconds(Math.min(120, holder.getEventSeconds() + 5));
                } else if (click.isRightClick()) {
                    holder.setEventSeconds(Math.max(15, holder.getEventSeconds() - 5));
                }
            }
            case 12 -> { // Vote Seconds (15 - 120)
                if (click.isLeftClick()) {
                    holder.setVoteSeconds(Math.min(120, holder.getVoteSeconds() + 5));
                } else if (click.isRightClick()) {
                    holder.setVoteSeconds(Math.max(15, holder.getVoteSeconds() - 5));
                }
            }
            case 13 -> { // Max Voteable Events (2 - 5)
                if (click.isLeftClick()) {
                    holder.setMaxVoteableEvents(Math.min(5, holder.getMaxVoteableEvents() + 1));
                } else if (click.isRightClick()) {
                    holder.setMaxVoteableEvents(Math.max(2, holder.getMaxVoteableEvents() - 1));
                }
            }
            case 14 -> { // Show Poll in Minecraft
                holder.setShowPollInMinecraft(!holder.isShowPollInMinecraft());
            }
            case 16 -> { // Confirm & Save
                int eventSec = holder.getEventSeconds();
                int voteSec = holder.getVoteSeconds();
                int intervalSec = holder.getIntervalSeconds();
                int sum = eventSec + voteSec;

                if (sum > intervalSec) {
                    player.sendMessage(SERIALIZER.deserialize(
                            "§c[TwitchVotesMinecraft] Cannot save! Event Seconds (" + eventSec + "s) + Vote Seconds (" + voteSec + "s) = "
                            + sum + "s, which exceeds Interval Seconds (" + intervalSec + "s)."
                    ));
                    return;
                }

                // Preserve mode: "chat" in config.yml
                plugin.getConfig().set("twitch.default-settings.mode", "chat");

                plugin.getConfig().set("twitch.default-settings.interval-seconds", intervalSec);
                plugin.getConfig().set("twitch.default-settings.event-seconds", eventSec);
                plugin.getConfig().set("twitch.default-settings.vote-seconds", voteSec);
                plugin.getConfig().set("twitch.default-settings.max-voteable-events", holder.getMaxVoteableEvents());
                plugin.getConfig().set("twitch.default-settings.show-poll-in-minecraft", holder.isShowPollInMinecraft());

                plugin.saveConfig();
                player.sendMessage(SERIALIZER.deserialize("§a[TwitchVotesMinecraft] Default settings successfully saved to config.yml!"));
                player.closeInventory();
                return;
            }
        }

        SettingsGUI.refreshInventory(holder);
    }
}
