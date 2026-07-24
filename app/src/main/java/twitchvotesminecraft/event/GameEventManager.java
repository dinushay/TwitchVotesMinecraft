package twitchvotesminecraft.event;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import twitchvotesminecraft.App;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class GameEventManager {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final List<GameEvent> ALL_EVENTS = new ArrayList<>();

    static {
        // --- SIMPLE EVENTS ---
        
        // 1. Summon 10 Zombies
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Summon 10 Zombies"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location loc = player.getLocation();
                for (int i = 0; i < 10; i++) {
                    loc.getWorld().spawnEntity(getRandomNearbyLocation(loc, 3), EntityType.ZOMBIE);
                }
            }
        });

        // 2. Give Random Item
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Give Random Item"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Material[] materials = Material.values();
                Material mat;
                do {
                    mat = materials[ThreadLocalRandom.current().nextInt(materials.length)];
                } while (mat.isAir() || !mat.isItem());
                player.getInventory().addItem(new ItemStack(mat, 1));
            }
        });

        // 3. Shuffle Inventory
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Shuffle Inventory"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                PlayerInventory inv = player.getInventory();
                ItemStack[] contents = inv.getStorageContents();
                List<ItemStack> list = new ArrayList<>(Arrays.asList(contents));
                Collections.shuffle(list);
                inv.setStorageContents(list.toArray(new ItemStack[0]));
            }
        });

        // 4. Drop Inventory
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Drop Inventory"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack item = inv.getItem(i);
                    if (item != null && !item.getType().isAir()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                        inv.setItem(i, null);
                    }
                }
            }
        });

        // 5. Time Set Night
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Time Set Night"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getWorld().setTime(13000);
            }
        });

        // 6. Fly (Levitation)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Fly (Levitation)"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5 * 20, 1));
            }
        });

        // 7. Compensation (Full Health & Hunger)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Compensation (Heal & Feed)"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
                player.setFoodLevel(20);
                player.setSaturation(20f);
            }
        });

        // 8. 50% Health
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "50% Health"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.setHealth(Math.max(1.0, player.getHealth() / 2.0));
            }
        });

        // 9. Hunger
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Zero Hunger"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.setFoodLevel(0);
                player.setSaturation(0f);
            }
        });

        // 10. Bad Omen
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Bad Omen Effect"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, eventSeconds * 20, 0));
            }
        });

        // 11. Lightning Strike
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Lightning Strike"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getWorld().strikeLightning(player.getLocation());
            }
        });

        // 12. Rest (Freeze 5s)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Rest (Freeze 5s)"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 255));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 5 * 20, 250));
            }
        });

        // 13. Rapid Flooding
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Rapid Flooding"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location head = player.getLocation().add(0, 1, 0);
                head.getBlock().setType(Material.WATER);
            }
        });

        // 14. Fireball
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Launch Fireball"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                LargeFireball fb = player.launchProjectile(LargeFireball.class);
                fb.setYield(2.0f);
            }
        });

        // 15. Aww, Man (Creeper Sound)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Aww, Man!"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);
            }
        });

        // 16. Chicken Rain
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Chicken Rain"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location loc = player.getLocation().add(0, 5, 0);
                for (int i = 0; i < 10; i++) {
                    loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);
                }
            }
        });

        // 17. Nausea
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Nausea Effect"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, eventSeconds * 20, 0));
            }
        });

        // 18. Don't Stress (0.5 Hearts)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Don't Stress (0.5 Hearts)"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.setHealth(1.0);
            }
        });

        // 19. Friendly Creeper
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Friendly Creeper"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Creeper creeper = (Creeper) player.getWorld().spawnEntity(player.getLocation().add(1, 0, 1), EntityType.CREEPER);
                creeper.customName(SERIALIZER.deserialize("§aFriendly Creeper"));
                creeper.setCustomNameVisible(true);
            }
        });

        // 20. Scammer (Brad Pitt Merchant)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Scammer Brad Pitt"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                WanderingTrader trader = (WanderingTrader) player.getWorld().spawnEntity(player.getLocation(), EntityType.WANDERING_TRADER);
                trader.customName(SERIALIZER.deserialize("§eBrad Pitt (Scammer)"));
                trader.setCustomNameVisible(true);
            }
        });

        // 21. Glow Nearby Entities
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Glow Nearby Entities"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getNearbyEntities(20, 20, 20).forEach(entity -> {
                    if (entity instanceof LivingEntity living) {
                        living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, eventSeconds * 20, 0));
                    }
                });
            }
        });

        // --- ITEM INTEGRATION EVENTS ---

        // 22. Spooky Pumpkin
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Spooky Pumpkin"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                ItemStack helmet = player.getInventory().getHelmet();
                if (helmet != null && !helmet.getType().isAir()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), helmet);
                }
                player.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
            }
        });

        // 23. Random Enchant
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Random Enchant"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item != null && !item.getType().isAir()) {
                    Enchantment[] enchants = Enchantment.values();
                    Enchantment ench = enchants[ThreadLocalRandom.current().nextInt(enchants.length)];
                    item.addUnsafeEnchantment(ench, ThreadLocalRandom.current().nextInt(1, 4));
                }
            }
        });

        // 24. The Cake is a Lie
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "The Cake is a Lie"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.CAKE, 1));
            }
        });

        // 25. Suspicious Apple
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Suspicious Apple"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                ItemStack apple = new ItemStack(Material.APPLE, 1);
                ItemMeta meta = apple.getItemMeta();
                if (meta != null) {
                    meta.displayName(SERIALIZER.deserialize("§cSuspicious Apple"));
                    meta.lore(List.of(SERIALIZER.deserialize("§7Looks dangerously delicious...")));
                    apple.setItemMeta(meta);
                }
                player.getInventory().addItem(apple);
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10 * 20, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 0));
            }
        });

        // --- BLOCK & WORLD EVENTS ---

        // 26. The Floor is Lava
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "The Floor is Lava"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Block under = player.getLocation().subtract(0, 1, 0).getBlock();
                Material orig = under.getType();
                if (!orig.isAir()) {
                    under.setType(Material.MAGMA_BLOCK);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> under.setType(orig), eventSeconds * 20L);
                }
            }
        });

        // 27. Rapid Brush Clearing
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Rapid Brush Clearing"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location center = player.getLocation();
                int radius = 15;
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            Block block = center.clone().add(x, y, z).getBlock();
                            if (block.getType().name().contains("LEAVES")) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        });

        // --- FUN & CHAOS EVENTS ---

        // 28. MLG Water
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "MLG Water"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
                player.teleport(player.getLocation().add(0, 30, 0));
            }
        });

        // 29. Bee Movie
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Bee Movie"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location loc = player.getLocation();
                for (int i = 0; i < 10; i++) {
                    loc.getWorld().spawnEntity(getRandomNearbyLocation(loc, 3), EntityType.BEE);
                }
            }
        });

        // 30. Touch Grass
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Touch Grass"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location loc = player.getLocation();
                int highestY = loc.getWorld().getHighestBlockYAt(loc);
                player.teleport(new Location(loc.getWorld(), loc.getX(), highestY + 1, loc.getZ(), loc.getYaw(), loc.getPitch()));
            }
        });

        // 31. No U Shield
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "No U Shield"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                ItemStack shield = new ItemStack(Material.SHIELD);
                ItemMeta meta = shield.getItemMeta();
                if (meta != null) {
                    meta.displayName(SERIALIZER.deserialize("§bNo U"));
                    shield.setItemMeta(meta);
                }
                player.getInventory().addItem(shield);
            }
        });

        // 32. Gotta Go Fast
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Gotta Go Fast"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, eventSeconds * 20, 49));
            }
        });

        // 33. Snail Mode
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Snail Mode"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, eventSeconds * 20, 3));
            }
        });

        // 34. Moon Gravity
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Moon Gravity"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, eventSeconds * 20, 3));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, eventSeconds * 20, 0));
            }
        });

        // 35. Jumpscare
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Jumpscare!"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.0f);
                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 2 * 20, 2));
            }
        });

        // 36. Fake Diamonds
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Fake Diamonds"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                ItemStack fakeDiamond = new ItemStack(Material.DIRT, 5);
                ItemMeta meta = fakeDiamond.getItemMeta();
                if (meta != null) {
                    meta.displayName(SERIALIZER.deserialize("§bDiamond"));
                    fakeDiamond.setItemMeta(meta);
                }
                player.getInventory().addItem(fakeDiamond);
            }
        });

        // 37. Vegan Mode
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Vegan Mode"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack item = inv.getItem(i);
                    if (item != null && item.getType().name().contains("BEEF") || item.getType().name().contains("PORK") 
                            || item.getType().name().contains("CHICKEN") || item.getType().name().contains("MUTTON")) {
                        inv.setItem(i, null);
                    }
                }
            }
        });

        // 38. Gluten Free
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Gluten Free"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack item = inv.getItem(i);
                    if (item != null && (item.getType() == Material.BREAD || item.getType() == Material.COOKIE)) {
                        inv.setItem(i, null);
                    }
                }
            }
        });

        // 39. American Dream
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "American Dream"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
                player.getInventory().addItem(new ItemStack(Material.CROSSBOW, 1));
                player.getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 32));
            }
        });

        // 40. Social Distancing
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Social Distancing"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location loc = player.getLocation();
                player.getNearbyEntities(10, 10, 10).forEach(entity -> {
                    Vector dir = entity.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(2.5).setY(0.5);
                    entity.setVelocity(dir);
                });
            }
        });

        // 41. Stonks
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Stonks (+1 Emerald)"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.EMERALD, 1));
            }
        });

        // 42. Not Stonks
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Not Stonks (-Emeralds)"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().remove(Material.EMERALD);
            }
        });

        // 43. Build a Snowman
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Build a Snowman"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location loc = player.getLocation();
                for (int i = 0; i < 5; i++) {
                    loc.getWorld().spawnEntity(getRandomNearbyLocation(loc, 3), EntityType.SNOW_GOLEM);
                }
            }
        });

        // 44. Spider-Man
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Spider-Man"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.COBWEB, 16));
                player.getInventory().addItem(new ItemStack(Material.CROSSBOW, 1));
            }
        });

        // 45. Trapped! (Obsidian Enclosure)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Trapped! (Obsidian)"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location center = player.getLocation();
                List<Block> placed = new ArrayList<>();
                for (int x = -1; x <= 1; x++) {
                    for (int y = 0; y <= 2; y++) {
                        for (int z = -1; z <= 1; z++) {
                            if (x == 0 && (y == 0 || y == 1) && z == 0) continue;
                            Block b = center.clone().add(x, y, z).getBlock();
                            if (b.getType().isAir()) {
                                b.setType(Material.OBSIDIAN);
                                placed.add(b);
                            }
                        }
                    }
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Block b : placed) b.setType(Material.AIR);
                }, eventSeconds * 20L);
            }
        });

        // 46. Cage Match
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Cage Match"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location center = player.getLocation();
                List<Block> placed = new ArrayList<>();
                for (int x = -1; x <= 1; x++) {
                    for (int y = 0; y <= 2; y++) {
                        for (int z = -1; z <= 1; z++) {
                            if (x == 0 && (y == 0 || y == 1) && z == 0) continue;
                            Block b = center.clone().add(x, y, z).getBlock();
                            if (b.getType().isAir()) {
                                b.setType(Material.BEDROCK);
                                placed.add(b);
                            }
                        }
                    }
                }
                Zombie z = (Zombie) center.getWorld().spawnEntity(center, EntityType.ZOMBIE);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Block b : placed) b.setType(Material.AIR);
                    if (z.isValid()) z.remove();
                }, 20 * 20L);
            }
        });

        // 47. Free Dirt
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Free Dirt x64"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.DIRT, 64));
            }
        });

        // 48. It's Dangerous Here
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "It's Dangerous Here"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD, 1));
            }
        });

        // 49. 404 Not Found
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "404 Not Found"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.sendTitle("§cError 404", "§7Page Not Found", 10, 70, 20);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, eventSeconds * 20, 0));
            }
        });

        // 50. Mining Away (Haste V)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Mining Away (Haste V)"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, eventSeconds * 20, 4));
            }
        });

        // 51. Bouncy Ground
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Bouncy Ground"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Block under = player.getLocation().subtract(0, 1, 0).getBlock();
                Material orig = under.getType();
                if (!orig.isAir()) {
                    under.setType(Material.SLIME_BLOCK);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> under.setType(orig), eventSeconds * 20L);
                }
            }
        });

        // 52. No More Protection (Drop Armor)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "No More Protection"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                PlayerInventory inv = player.getInventory();
                for (ItemStack armor : inv.getArmorContents()) {
                    if (armor != null && !armor.getType().isAir()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), armor);
                    }
                }
                inv.setArmorContents(null);
            }
        });

        // 53. Iron Man
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Iron Man Armor"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                PlayerInventory inv = player.getInventory();
                inv.setHelmet(new ItemStack(Material.IRON_HELMET));
                inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                inv.setBoots(new ItemStack(Material.IRON_BOOTS));
            }
        });

        // 54. Inventory Clutter
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Inventory Clutter"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < 36; i++) {
                    if (inv.getItem(i) == null || inv.getItem(i).getType().isAir()) {
                        inv.setItem(i, new ItemStack(Material.WHEAT_SEEDS, 1));
                    }
                }
            }
        });

        // 55. Double Trouble
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Double Trouble"; }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getNearbyEntities(15, 15, 15).forEach(entity -> {
                    if (entity instanceof Monster monster) {
                        monster.getWorld().spawnEntity(monster.getLocation(), monster.getType());
                    }
                });
            }
        });

        // 56. Flashbang
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return "Flashbang"; }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location center = player.getLocation();
                player.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 3 * 20, 1));

                List<Block> blocks = new ArrayList<>();
                for (int x = -1; x <= 1; x++) {
                    for (int y = 0; y <= 2; y++) {
                        for (int z = -1; z <= 1; z++) {
                            if (x == 0 && (y == 0 || y == 1) && z == 0) continue;
                            Block b = center.clone().add(x, y, z).getBlock();
                            if (b.getType().isAir()) {
                                b.setType(Material.WHITE_CONCRETE);
                                blocks.add(b);
                            }
                        }
                    }
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Block b : blocks) b.setType(Material.AIR);
                }, 3 * 20L);
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

    private static Location getRandomNearbyLocation(Location base, int range) {
        return base.clone().add(
                ThreadLocalRandom.current().nextDouble(-range, range),
                0,
                ThreadLocalRandom.current().nextDouble(-range, range)
        );
    }
}
