package twitchvotesminecraft.event;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
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


    private static final List<GameEvent> ALL_EVENTS = new ArrayList<>();

    static {
        // --- SIMPLE EVENTS ---
        
        // 1. Summon 10 Zombies
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_1"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_1"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_2"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_2"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_3"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_3"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_4"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_4"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_5"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_5"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getWorld().setTime(13000);
            }
        });

        // 6. Fly (Levitation)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_6"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_6"); }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5 * 20, 1));
            }
        });

        // 7. Compensation (Full Health & Hunger)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_7"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_7"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_8"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_8"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.setHealth(Math.max(1.0, player.getHealth() / 2.0));
            }
        });

        // 9. Hunger
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_9"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_9"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.setFoodLevel(0);
                player.setSaturation(0f);
            }
        });

        // 10. Bad Omen
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_10"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_10"); }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, eventSeconds * 20, 0));
            }
        });

        // 11. Lightning Strike
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_11"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_11"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getWorld().strikeLightning(player.getLocation());
            }
        });

        // 12. Rest (Freeze 5s)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_12"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_12"); }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 255));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 5 * 20, 250));
            }
        });

        // 13. Rapid Flooding
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_13"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_13"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location head = player.getLocation().add(0, 1, 0);
                head.getBlock().setType(Material.WATER);
            }
        });

        // 14. Fireball
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_14"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_14"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                LargeFireball fb = player.launchProjectile(LargeFireball.class);
                fb.setYield(2.0f);
            }
        });

        // 15. Aww, Man (Creeper Sound)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_15"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_15"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);
            }
        });

        // 16. Chicken Rain
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_16"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_16"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_17"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_17"); }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, eventSeconds * 20, 0));
            }
        });

        // 18. Don't Stress (0.5 Hearts)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_18"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_18"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.setHealth(1.0);
            }
        });

        // 19. Friendly Creeper
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_19"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_19"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Creeper creeper = (Creeper) player.getWorld().spawnEntity(player.getLocation().add(1, 0, 1), EntityType.CREEPER);
                creeper.customName(twitchvotesminecraft.App.getInstance().getMessageManager().getComponent("event_entities.friendly_creeper"));
                creeper.setCustomNameVisible(true);
            }
        });

        // 20. Scammer (Brad Pitt Merchant)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_20"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_20"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                WanderingTrader trader = (WanderingTrader) player.getWorld().spawnEntity(player.getLocation(), EntityType.WANDERING_TRADER);
                trader.customName(twitchvotesminecraft.App.getInstance().getMessageManager().getComponent("event_entities.brad_pitt_scammer"));
                trader.setCustomNameVisible(true);
            }
        });

        // 21. Glow Nearby Entities
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_21"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_21"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_22"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_22"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_23"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_23"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_24"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_24"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.CAKE, 1));
            }
        });

        // 25. Suspicious Apple
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_25"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_25"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                ItemStack apple = new ItemStack(Material.APPLE, 1);
                ItemMeta meta = apple.getItemMeta();
                if (meta != null) {
                    meta.displayName(twitchvotesminecraft.App.getInstance().getMessageManager().getComponent("event_entities.suspicious_apple"));
                    meta.lore(List.of(twitchvotesminecraft.App.getInstance().getMessageManager().getComponent("event_entities.suspicious_apple_lore")));
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_26"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_26"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_27"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_27"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                Location center = player.getLocation();
                org.bukkit.World world = center.getWorld();
                int cx = center.getBlockX();
                int cy = center.getBlockY();
                int cz = center.getBlockZ();
                int radius = 15;
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            Block block = world.getBlockAt(cx + x, cy + y, cz + z);
                            if (Tag.LEAVES.isTagged(block.getType())) {
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_28"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_28"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
                player.teleport(player.getLocation().add(0, 30, 0));
            }
        });

        // 29. Bee Movie
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_29"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_29"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_30"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_30"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_31"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_31"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                ItemStack shield = new ItemStack(Material.SHIELD);
                ItemMeta meta = shield.getItemMeta();
                if (meta != null) {
                    meta.displayName(twitchvotesminecraft.App.getInstance().getMessageManager().getComponent("event_entities.no_u"));
                    shield.setItemMeta(meta);
                }
                player.getInventory().addItem(shield);
            }
        });

        // 32. Gotta Go Fast
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_32"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_32"); }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, eventSeconds * 20, 49));
            }
        });

        // 33. Snail Mode
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_33"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_33"); }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, eventSeconds * 20, 3));
            }
        });

        // 34. Moon Gravity
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_34"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_34"); }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, eventSeconds * 20, 3));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, eventSeconds * 20, 0));
            }
        });

        // 35. Jumpscare
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_35"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_35"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.0f);
                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 2 * 20, 2));
            }
        });

        // 36. Fake Diamonds
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_36"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_36"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                ItemStack fakeDiamond = new ItemStack(Material.DIRT, 5);
                ItemMeta meta = fakeDiamond.getItemMeta();
                if (meta != null) {
                    meta.displayName(twitchvotesminecraft.App.getInstance().getMessageManager().getComponent("event_entities.diamond"));
                    fakeDiamond.setItemMeta(meta);
                }
                player.getInventory().addItem(fakeDiamond);
            }
        });

        // 37. Vegan Mode
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_37"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_37"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_38"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_38"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_39"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_39"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_40"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_40"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_41"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_41"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.EMERALD, 1));
            }
        });

        // 42. Not Stonks
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_42"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_42"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().remove(Material.EMERALD);
            }
        });

        // 43. Build a Snowman
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_43"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_43"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_44"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_44"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.COBWEB, 16));
                player.getInventory().addItem(new ItemStack(Material.CROSSBOW, 1));
            }
        });

        // 45. Trapped! (Obsidian Enclosure)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_45"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_45"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_46"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_46"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_47"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_47"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.DIRT, 64));
            }
        });

        // 48. It's Dangerous Here
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_48"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_48"); }
            @Override public boolean isInstant() { return true; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD, 1));
            }
        });

        // 49. 404 Not Found
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_49"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_49"); }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.sendTitle("§cError 404", "§7Page Not Found", 10, 70, 20);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, eventSeconds * 20, 0));
            }
        });

        // 50. Mining Away (Haste V)
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_50"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_50"); }
            @Override public boolean isInstant() { return false; }
            @Override
            public void execute(Player player, App plugin, int eventSeconds) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, eventSeconds * 20, 4));
            }
        });

        // 51. Bouncy Ground
        ALL_EVENTS.add(new GameEvent() {
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_51"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_51"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_52"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_52"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_53"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_53"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_54"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_54"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_55"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_55"); }
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
            @Override public String getName() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("events.event_56"); }
            @Override public String getDescription() { return twitchvotesminecraft.App.getInstance().getMessageManager().getString("event_descriptions.event_56"); }
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
