package twitchvotesminecraft.vote;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();

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
    private BossBar bossBar;
    private Scoreboard scoreboard;
    private Objective objective;

    private BukkitTask timerTask;
    private BukkitTask nextRoundTask;
    private boolean active = true;

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
        player.sendMessage(SERIALIZER.deserialize("§a[TwitchVotesMinecraft] Connected to Twitch channel #" + channel + ". Starting voting system!"));
        startNewRound();
    }

    public void stop() {
        active = false;
        if (chatClient != null) {
            chatClient.disconnect();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        if (nextRoundTask != null) {
            nextRoundTask.cancel();
        }
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar.setVisible(false);
        }
        if (player.isOnline()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    private void startNewRound() {
        if (!active || !player.isOnline()) {
            stop();
            return;
        }

        userVotes.clear();
        currentOptions = GameEventManager.getRandomEvents(maxVoteableEvents);

        // Setup Scoreboard
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("twitchvote", Criteria.DUMMY, SERIALIZER.deserialize("§5§lTwitch Vote"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);

        // Setup BossBar
        String bossBarTitle = "§d§lVoting ends in: §e" + voteSeconds + "s";
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar(
                    bossBarTitle,
                    BarColor.PURPLE,
                    BarStyle.SOLID
            );
            bossBar.addPlayer(player);
        }
        bossBar.setTitle(bossBarTitle);
        bossBar.setProgress(1.0);
        bossBar.setVisible(true);

        updateScoreboard();

        // Start Countdown Task
        if (timerTask != null) {
            timerTask.cancel();
        }

        timerTask = new BukkitRunnable() {
            int remaining = voteSeconds;

            @Override
            public void run() {
                if (!active || !player.isOnline()) {
                    cancel();
                    return;
                }

                remaining--;
                if (remaining <= 0) {
                    cancel();
                    finishVoting();
                    return;
                }

                bossBar.setTitle("§d§lVoting ends in: §e" + remaining + "s");
                bossBar.setProgress(Math.max(0.0, Math.min(1.0, (double) remaining / voteSeconds)));
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void finishVoting() {
        if (!active || !player.isOnline()) {
            return;
        }

        int totalVotes = userVotes.size();
        int[] counts = new int[currentOptions.size()];

        for (int opt : userVotes.values()) {
            if (opt >= 1 && opt <= currentOptions.size()) {
                counts[opt - 1]++;
            }
        }

        // Find max votes
        int max = -1;
        for (int count : counts) {
            if (count > max) {
                max = count;
            }
        }

        // Collect all options tied for max votes
        List<Integer> topIndices = new ArrayList<>();
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] == max) {
                topIndices.add(i);
            }
        }

        // Pick winner (random tie breaker if multiple)
        int winningIndex = topIndices.get(ThreadLocalRandom.current().nextInt(topIndices.size()));
        GameEvent winningEvent = currentOptions.get(winningIndex);

        int winVotes = counts[winningIndex];
        int winPercent = (totalVotes > 0) ? (int) Math.round(((double) winVotes / totalVotes) * 100.0) : 0;

        // Broadcast Winner in Chat
        player.sendMessage(SERIALIZER.deserialize(
                "§a[TwitchVotesMinecraft] Voting ended! Winner: §e" + winningEvent.getName()
                + " §a(" + winPercent + "% votes)! Executing now..."
        ));

        // Hide displays
        if (bossBar != null) {
            bossBar.setVisible(false);
        }
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        // Execute Winner Event
        winningEvent.execute(player, plugin, eventSeconds);

        // Schedule Next Round
        nextRoundTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (active) {
                startNewRound();
            }
        }, intervalSeconds * 20L);
    }

    private void handleChatMessage(String username, String message) {
        if (!active) return;

        String msg = message.trim();
        try {
            int vote = Integer.parseInt(msg);
            if (vote >= 1 && vote <= currentOptions.size()) {
                userVotes.put(username.toLowerCase(), vote);
                Bukkit.getScheduler().runTask(plugin, this::updateScoreboard);
            }
        } catch (NumberFormatException ignored) {}
    }

    private void updateScoreboard() {
        if (!active || objective == null) return;

        int totalVotes = userVotes.size();
        int[] counts = new int[currentOptions.size()];

        for (int opt : userVotes.values()) {
            if (opt >= 1 && opt <= currentOptions.size()) {
                counts[opt - 1]++;
            }
        }

        // Unregister existing entries to prevent score duplication
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
        objective.getScore("§7Total Votes: §f" + totalVotes).setScore(scoreIndex);
    }
}
