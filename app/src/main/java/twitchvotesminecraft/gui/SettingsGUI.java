package twitchvotesminecraft.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();

    private SettingsGUI() {}

    public static void open(App plugin, Player player, String twitchName) {
        int intervalSeconds = plugin.getConfig().getInt("twitch.default-settings.interval-seconds", 90);
        int eventSeconds = plugin.getConfig().getInt("twitch.default-settings.event-seconds", 60);
        int voteSeconds = plugin.getConfig().getInt("twitch.default-settings.vote-seconds", 30);
        int maxVoteableEvents = plugin.getConfig().getInt("twitch.default-settings.max-voteable-events", 4);

        SettingsGUIHolder holder = new SettingsGUIHolder(twitchName, intervalSeconds, eventSeconds, voteSeconds, maxVoteableEvents);

        Component title = SERIALIZER.deserialize("§8Twitch Settings - §5" + twitchName);
        Inventory inv = Bukkit.createInventory(holder, 27, title);
        holder.setInventory(inv);

        refreshInventory(holder);
        player.openInventory(inv);
    }

    public static void refreshInventory(SettingsGUIHolder holder) {
        Inventory inv = holder.getInventory();

        ItemStack bg = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, bg);
        }

        // Slot 10: Interval Seconds
        ItemStack intervalItem = createItem(
                Material.CLOCK,
                "§eInterval Seconds: §b" + holder.getIntervalSeconds() + "s",
                List.of("§7Left-Click: §a+5s §7| Right-Click: §c-5s", "§7Range: 15s - 120s")
        );
        inv.setItem(10, intervalItem);

        // Slot 11: Event Seconds
        ItemStack eventItem = createItem(
                Material.COMPARATOR,
                "§eEvent Seconds: §b" + holder.getEventSeconds() + "s",
                List.of("§7Left-Click: §a+5s §7| Right-Click: §c-5s", "§7Range: 15s - 120s")
        );
        inv.setItem(11, eventItem);

        // Slot 12: Vote Seconds
        ItemStack voteItem = createItem(
                Material.REPEATER,
                "§eVote Seconds: §b" + holder.getVoteSeconds() + "s",
                List.of("§7Left-Click: §a+5s §7| Right-Click: §c-5s", "§7Range: 15s - 120s")
        );
        inv.setItem(12, voteItem);

        // Slot 13: Max Voteable Events
        ItemStack maxEventsItem = createItem(
                Material.DIAMOND,
                "§eMax Voteable Events: §b" + holder.getMaxVoteableEvents(),
                List.of("§7Left-Click: §a+1 §7| Right-Click: §c-1", "§7Range: 2 - 5")
        );
        inv.setItem(13, maxEventsItem);

        // Slot 16: Confirm Button
        ItemStack confirmItem = createItem(
                Material.LIME_CONCRETE,
                "§a§lConfirm & Save",
                List.of("§7Click to save default settings to config.yml.")
        );
        inv.setItem(16, confirmItem);
    }

    private static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(SERIALIZER.deserialize(name));
            if (lore != null) {
                List<Component> loreComponents = new ArrayList<>();
                for (String l : lore) {
                    loreComponents.add(SERIALIZER.deserialize(l));
                }
                meta.lore(loreComponents);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
