package twitchvotesminecraft.vote;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import twitchvotesminecraft.App;
import twitchvotesminecraft.event.GameEvent;
import twitchvotesminecraft.event.GameEventManager;
import twitchvotesminecraft.twitch.TwitchChatClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class VoteSession {

    private final App plugin;
    private final Player player;
    private final String channel;

    private final int intervalSeconds;
    private final int eventSeconds;
    private final int voteSeconds;
    private final int maxVoteableEvents;

    private final TwitchChatClient chatClient;
    private final Map<String, Integer> userVotes = new ConcurrentHashMap<>();

    private List<GameEvent> currentOptions = new ArrayList<>();
    private GameEvent winningEvent = null;
    private BossBar bossBar;
    private Scoreboard scoreboard;
    private Objective objective;

    private BukkitTask activeTask;
    private boolean active = true;
    private boolean isVotingPhase = false;

    public VoteSession(App plugin, Player player, String channel) {
        this.plugin = plugin;
        this.player = player;
        this.channel = channel;

        this.intervalSeconds = plugin.getConfig().getInt("twitch.default-settings.interval-seconds", 90);
        this.eventSeconds = plugin.getConfig().getInt("twitch.default-settings.event-seconds", 60);
        this.voteSeconds = plugin.getConfig().getInt("twitch.default-settings.vote-seconds", 30);
        this.maxVoteableEvents = plugin.getConfig().getInt("twitch.default-settings.max-voteable-events", 4);

        this.chatClient = new TwitchChatClient(plugin, channel, this::handleChatMessage);
    }

    public void start() {
        chatClient.connect();
        player.sendMessage(plugin.getMessageManager().getComponent("session.connected", java.util.Map.of("%channel%", channel)));
        startVotingPhase();
    }

    public void stop() {
        active = false;
        isVotingPhase = false;
        if (chatClient != null) {
            chatClient.disconnect();
        }
        if (activeTask != null) {
            activeTask.cancel();
        }
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar.setVisible(false);
        }
        if (player.isOnline()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    // Phase 1: Voting Phase (Scoreboard & BossBar active)
    private void startVotingPhase() {
        if (!active || !player.isOnline()) {
            stop();
            return;
        }

        isVotingPhase = true;
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        userVotes.clear();
        winningEvent = null;
        currentOptions = GameEventManager.getRandomEvents(maxVoteableEvents);

        // Setup Scoreboard ONLY for Voting Phase
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("twitchvote", Criteria.DUMMY, plugin.getMessageManager().getComponent("scoreboard.title"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);

        // Setup BossBar
        String title = plugin.getMessageManager().getString("bossbar.voting", java.util.Map.of("%time%", String.valueOf(voteSeconds)));
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar(title, BarColor.PURPLE, BarStyle.SOLID);
            bossBar.addPlayer(player);
        } else {
            bossBar.setColor(BarColor.PURPLE);
        }
        bossBar.setTitle(title);
        bossBar.setProgress(1.0);
        bossBar.setVisible(true);

        updateScoreboard();

        if (activeTask != null) activeTask.cancel();

        activeTask = new BukkitRunnable() {
            int remaining = voteSeconds;

            @Override
            public void run() {
                if (!active || !player.isOnline()) {
                    cancel();
                    return;
                }

                remaining--;
                if (remaining <= 3 && remaining > 0) {
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                }
                if (remaining <= 0) {
                    cancel();
                    finishVotingPhase();
                    return;
                }

                bossBar.setTitle(plugin.getMessageManager().getString("bossbar.voting", java.util.Map.of("%time%", String.valueOf(remaining))));
                bossBar.setProgress(Math.max(0.0, Math.min(1.0, (double) remaining / voteSeconds)));

                // ⚡ Bolt: Batch update the scoreboard once per second instead of per chat message
                updateScoreboard();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void finishVotingPhase() {
        if (!active || !player.isOnline()) return;

        isVotingPhase = false;

        // Hide Scoreboard immediately after voting phase ends
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        int totalVotes = userVotes.size();
        int[] counts = new int[currentOptions.size()];

        for (int opt : userVotes.values()) {
            if (opt >= 1 && opt <= currentOptions.size()) {
                counts[opt - 1]++;
            }
        }

        int max = -1;
        for (int count : counts) {
            if (count > max) max = count;
        }

        List<Integer> topIndices = new ArrayList<>();
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] == max) topIndices.add(i);
        }

        int winningIndex = topIndices.get(ThreadLocalRandom.current().nextInt(topIndices.size()));
        winningEvent = currentOptions.get(winningIndex);

        int winVotes = counts[winningIndex];
        int winPercent = (totalVotes > 0) ? (int) Math.round(((double) winVotes / totalVotes) * 100.0) : 0;
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        player.sendMessage(plugin.getMessageManager().getComponent("session.vote-ended", java.util.Map.of(
                "%event%", winningEvent.getName(),
                "%percent%", String.valueOf(winPercent)
        )));
        player.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(winningEvent.getDescription()));

        // Execute Winner Event targeted at player
        winningEvent.execute(player, plugin, eventSeconds);

        if (!winningEvent.isInstant() && eventSeconds > 0) {
            startEventActivePhase();
        } else {
            startCooldownPhase();
        }
    }

    // Phase 2: Event Duration Phase (BossBar only, no Scoreboard)
    private void startEventActivePhase() {
        if (!active || !player.isOnline()) {
            stop();
            return;
        }

        isVotingPhase = false;
        bossBar.setColor(BarColor.GREEN);
        bossBar.setTitle(plugin.getMessageManager().getString("bossbar.event-active", java.util.Map.of(
                "%event%", winningEvent.getName(),
                "%time%", String.valueOf(eventSeconds)
        )));
        bossBar.setProgress(1.0);

        if (activeTask != null) activeTask.cancel();

        activeTask = new BukkitRunnable() {
            int remaining = eventSeconds;

            @Override
            public void run() {
                if (!active || !player.isOnline()) {
                    cancel();
                    return;
                }

                remaining--;
                if (remaining <= 0) {
                    cancel();
                    startCooldownPhase();
                    return;
                }

                bossBar.setTitle(plugin.getMessageManager().getString("bossbar.event-active", java.util.Map.of(
                        "%event%", winningEvent.getName(),
                        "%time%", String.valueOf(remaining)
                )));
                bossBar.setProgress(Math.max(0.0, Math.min(1.0, (double) remaining / eventSeconds)));
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    // Phase 3: Cooldown Phase until next voting round (BossBar only, no Scoreboard)
    private void startCooldownPhase() {
        if (!active || !player.isOnline()) {
            stop();
            return;
        }

        isVotingPhase = false;
        int cooldownSeconds = intervalSeconds - (voteSeconds + (winningEvent != null && !winningEvent.isInstant() ? eventSeconds : 0));
        if (cooldownSeconds <= 0) {
            startVotingPhase();
            return;
        }

        bossBar.setColor(BarColor.BLUE);
        bossBar.setTitle(plugin.getMessageManager().getString("bossbar.next-vote", java.util.Map.of("%time%", String.valueOf(cooldownSeconds))));
        bossBar.setProgress(1.0);

        if (activeTask != null) activeTask.cancel();

        activeTask = new BukkitRunnable() {
            int remaining = cooldownSeconds;

            @Override
            public void run() {
                if (!active || !player.isOnline()) {
                    cancel();
                    return;
                }

                remaining--;
                if (remaining <= 3 && remaining > 0) {
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                }
                if (remaining <= 0) {
                    cancel();
                    startVotingPhase();
                    return;
                }

                bossBar.setTitle(plugin.getMessageManager().getString("bossbar.next-vote", java.util.Map.of("%time%", String.valueOf(remaining))));
                bossBar.setProgress(Math.max(0.0, Math.min(1.0, (double) remaining / cooldownSeconds)));
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void handleChatMessage(String username, String message) {
        if (!active || !isVotingPhase) return;

        String msg = message.trim();
        try {
            int vote = Integer.parseInt(msg);
            if (vote >= 1 && vote <= currentOptions.size()) {
                userVotes.put(username.toLowerCase(), vote);
                // ⚡ Bolt: Removed synchronous Bukkit task per-message to prevent TPS drops.
                // Scoreboard update is now batched in the 1-second voting timer task.
            }
        } catch (NumberFormatException ignored) {}
    }

    private void updateScoreboard() {
        if (!active || !isVotingPhase || objective == null || currentOptions.isEmpty()) return;

        int totalVotes = userVotes.size();
        int[] counts = new int[currentOptions.size()];

        for (int opt : userVotes.values()) {
            if (opt >= 1 && opt <= currentOptions.size()) {
                counts[opt - 1]++;
            }
        }

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        int scoreIndex = currentOptions.size() + 2;

        for (int i = 0; i < currentOptions.size(); i++) {
            GameEvent event = currentOptions.get(i);
            int votes = counts[i];
            int percent = (totalVotes > 0) ? (int) Math.round(((double) votes / totalVotes) * 100.0) : 0;

            String line = "§e" + (i + 1) + ". " + event.getName() + " §7- §b" + percent + "% §7(" + votes + ")";
            objective.getScore(line).setScore(scoreIndex--);
        }

        objective.getScore(" §7").setScore(scoreIndex--);
        objective.getScore(plugin.getMessageManager().getString("scoreboard.total-votes", java.util.Map.of("%total%", String.valueOf(totalVotes)))).setScore(scoreIndex);
    }
}
