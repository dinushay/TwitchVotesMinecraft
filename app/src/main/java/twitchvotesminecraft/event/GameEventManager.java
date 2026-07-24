package twitchvotesminecraft.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import twitchvotesminecraft.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class GameEventManager {

    private static final List<GameEvent> ALL_EVENTS = new ArrayList<>();

    static {
        // 1. Spawn 5 Zombies (Instant)
        ALL_EVENTS.add(new GameEvent() {
            @Override
            public String getName() { return "Spawn 5 Zombies"; }
            @Override
            public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location loc = player.getLocation();
                for (int i = 0; i < 5; i++) {
                    loc.getWorld().spawnEntity(loc.clone().add(
                            ThreadLocalRandom.current().nextDouble(-3, 3),
                            0,
                            ThreadLocalRandom.current().nextDouble(-3, 3)
                    ), EntityType.ZOMBIE);
                }
            }
        });

        // 2. Speed Boost II (Duration)
        ALL_EVENTS.add(new GameEvent() {
            @Override
            public String getName() { return "Speed Boost II"; }
            @Override
            public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, eventSeconds * 20, 1));
            }
        });

        // 3. Strike Lightning (Instant)
        ALL_EVENTS.add(new GameEvent() {
            @Override
            public String getName() { return "Strike Lightning"; }
            @Override
            public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location loc = player.getLocation().add(
                        ThreadLocalRandom.current().nextDouble(-4, 4),
                        0,
                        ThreadLocalRandom.current().nextDouble(-4, 4)
                );
                loc.getWorld().strikeLightning(loc);
            }
        });

        // 4. Spawn Creeper (Instant)
        ALL_EVENTS.add(new GameEvent() {
            @Override
            public String getName() { return "Spawn Creeper"; }
            @Override
            public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location loc = player.getLocation().add(
                        ThreadLocalRandom.current().nextDouble(-2, 2),
                        0,
                        ThreadLocalRandom.current().nextDouble(-2, 2)
                );
                loc.getWorld().spawnEntity(loc, EntityType.CREEPER);
            }
        });

        // 5. Jump Boost III (Duration)
        ALL_EVENTS.add(new GameEvent() {
            @Override
            public String getName() { return "Jump Boost III"; }
            @Override
            public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, eventSeconds * 20, 2));
            }
        });

        // 6. Drop Anvil (Instant)
        ALL_EVENTS.add(new GameEvent() {
            @Override
            public String getName() { return "Drop Anvil"; }
            @Override
            public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location loc = player.getLocation().add(0, 3, 0);
                loc.getBlock().setType(Material.ANVIL);
            }
        });

        // 7. Give Diamond (Instant)
        ALL_EVENTS.add(new GameEvent() {
            @Override
            public String getName() { return "Give Diamond"; }
            @Override
            public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
            }
        });

        // 8. Random Teleport (Instant)
        ALL_EVENTS.add(new GameEvent() {
            @Override
            public String getName() { return "Random Teleport"; }
            @Override
            public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location loc = player.getLocation().add(
                        ThreadLocalRandom.current().nextDouble(-10, 10),
                        0,
                        ThreadLocalRandom.current().nextDouble(-10, 10)
                );
                player.teleport(loc);
            }
        });

        // 9. Blindness Effect (Duration)
        ALL_EVENTS.add(new GameEvent() {
            @Override
            public String getName() { return "Blindness Effect"; }
            @Override
            public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, eventSeconds * 20, 0));
            }
        });

        // 10. Heal & Feed (Instant)
        ALL_EVENTS.add(new GameEvent() {
            @Override
            public String getName() { return "Heal & Feed"; }
            @Override
            public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.setHealth(player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());
                player.setFoodLevel(20);
            }
        });
    }

    private GameEventManager() {}

    /**
     * Pick distinct random events from the event registry.
     * @param count Number of events to pick (e.g. max-voteable-events).
     * @return List of random distinct GameEvent instances.
     */
    public static List<GameEvent> getRandomEvents(int count) {
        List<GameEvent> copy = new ArrayList<>(ALL_EVENTS);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(count, copy.size()));
    }
}
