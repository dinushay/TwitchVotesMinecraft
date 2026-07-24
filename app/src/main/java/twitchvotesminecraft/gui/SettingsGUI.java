package twitchvotesminecraft.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import twitchvotesminecraft.App;

import java.util.ArrayList;
import java.util.List;

public final class SettingsGUI {

    private SettingsGUI() {}

    public static void open(App plugin, Player player, String twitchName) {
        int intervalSeconds = plugin.getConfig().getInt("twitch.default-settings.interval-seconds", 90);
        int eventSeconds = plugin.getConfig().getInt("twitch.default-settings.event-seconds", 60);
        int voteSeconds = plugin.getConfig().getInt("twitch.default-settings.vote-seconds", 30);
        int maxVoteableEvents = plugin.getConfig().getInt("twitch.default-settings.max-voteable-events", 4);

        SettingsGUIHolder holder = new SettingsGUIHolder(twitchName, intervalSeconds, eventSeconds, voteSeconds, maxVoteableEvents);

        Component title = plugin.getMessageManager().getComponent("gui.title", java.util.Map.of("%channel%", twitchName));
        Inventory inv = Bukkit.createInventory(holder, 27, title);
        holder.setInventory(inv);

        refreshInventory(plugin, holder);
        player.openInventory(inv);
    }

    public static void refreshInventory(App plugin, SettingsGUIHolder holder) {
        Inventory inv = holder.getInventory();

        boolean isValid = holder.getEventSeconds() + holder.getVoteSeconds() <= holder.getIntervalSeconds();
        Material bgMaterial = isValid ? Material.GRAY_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        ItemStack bg = createItem(bgMaterial, " ", null);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, bg);
        }

        // Slot 10: Interval Seconds
        ItemStack intervalItem = createItem(
                Material.CLOCK,
                plugin.getMessageManager().getComponent("gui.interval-seconds", java.util.Map.of("%val%", String.valueOf(holder.getIntervalSeconds()))),
                List.of(
                    plugin.getMessageManager().getComponent("gui.click-modify"),
                    plugin.getMessageManager().getComponent("gui.range-time")
                )
        );
        inv.setItem(10, intervalItem);

        // Slot 11: Event Seconds
        ItemStack eventItem = createItem(
                Material.COMPARATOR,
                plugin.getMessageManager().getComponent("gui.event-seconds", java.util.Map.of("%val%", String.valueOf(holder.getEventSeconds()))),
                List.of(
                    plugin.getMessageManager().getComponent("gui.click-modify"),
                    plugin.getMessageManager().getComponent("gui.range-time")
                )
        );
        inv.setItem(11, eventItem);

        // Slot 12: Vote Seconds
        ItemStack voteItem = createItem(
                Material.REPEATER,
                plugin.getMessageManager().getComponent("gui.vote-seconds", java.util.Map.of("%val%", String.valueOf(holder.getVoteSeconds()))),
                List.of(
                    plugin.getMessageManager().getComponent("gui.click-modify"),
                    plugin.getMessageManager().getComponent("gui.range-time")
                )
        );
        inv.setItem(12, voteItem);

        // Slot 13: Max Voteable Events
        ItemStack maxEventsItem = createItem(
                Material.DIAMOND,
                plugin.getMessageManager().getComponent("gui.max-events", java.util.Map.of("%val%", String.valueOf(holder.getMaxVoteableEvents()))),
                List.of(
                    plugin.getMessageManager().getComponent("gui.click-modify-small"),
                    plugin.getMessageManager().getComponent("gui.range-count")
                )
        );
        inv.setItem(13, maxEventsItem);

        // Slot 16: Confirm Button
        ItemStack confirmItem;
        if (isValid) {
            confirmItem = createItem(
                    Material.LIME_CONCRETE,
                    plugin.getMessageManager().getComponent("gui.confirm"),
                    List.of(plugin.getMessageManager().getComponent("gui.confirm-lore"))
            );
        } else {
            confirmItem = createItem(
                    Material.RED_CONCRETE,
                    plugin.getMessageManager().getComponent("gui.cannot-save"),
                    List.of(
                        plugin.getMessageManager().getComponent("gui.cannot-save-lore1"),
                        plugin.getMessageManager().getComponent("gui.cannot-save-lore2")
                    )
            );
        }
        inv.setItem(16, confirmItem);

        // Slot 26: Close Button
        ItemStack closeItem = createItem(
                Material.BARRIER,
                plugin.getMessageManager().getComponent("gui.close"),
                List.of(plugin.getMessageManager().getComponent("gui.close-lore"))
        );
        inv.setItem(26, closeItem);
    }

    private static ItemStack createItem(Material material, String name, List<String> lore) {
        // Kept for backward compatibility with empty space item
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name));
            if (lore != null) {
                List<Component> loreComponents = new ArrayList<>();
                for (String l : lore) {
                    loreComponents.add(Component.text(l));
                }
                meta.lore(loreComponents);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createItem(Material material, Component name, List<Component> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(name);
            if (lore != null) {
                meta.lore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
