package twitchvotesminecraft.command;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import twitchvotesminecraft.App;
import twitchvotesminecraft.auth.TwitchValidator;
import twitchvotesminecraft.gui.SettingsGUI;

import java.util.List;
import java.util.regex.Pattern;

public class TwitchCommand extends Command {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final Pattern TWITCH_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,25}$");

    private final App plugin;

    public TwitchCommand(App plugin) {
        super(
            "twitchvotesminecraft",
            "Configures TwitchVotesMinecraft default settings for a given Twitch channel.",
            "/twitchvotesminecraft <twitch-name | cancel>",
            List.of("tvm")
        );
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(SERIALIZER.deserialize("§cOnly in-game players can execute this command."));
            return true;
        }

        // Require OP permission
        if (!player.isOp()) {
            player.sendMessage(SERIALIZER.deserialize("§c[TwitchVotesMinecraft] You must be a server Operator (OP) to use this command."));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(SERIALIZER.deserialize("§cUsage: /" + commandLabel + " <twitch-name | cancel>"));
            return true;
        }

        String target = args[0].trim();

        // Handle cancel subcommand
        if (target.equalsIgnoreCase("cancel")) {
            boolean stopped = plugin.stopVoteSession(player);
            if (stopped) {
                player.sendMessage(SERIALIZER.deserialize("§a[TwitchVotesMinecraft] Active Twitch voting session cancelled."));
            } else {
                player.sendMessage(SERIALIZER.deserialize("§c[TwitchVotesMinecraft] You do not have an active voting session to cancel."));
            }
            return true;
        }

        // Validate username syntax
        if (!TWITCH_NAME_PATTERN.matcher(target).matches()) {
            player.sendMessage(SERIALIZER.deserialize("§c[TwitchVotesMinecraft] Invalid Twitch username syntax! Usernames must be 4-25 alphanumeric characters or underscores."));
            return true;
        }

        player.sendMessage(SERIALIZER.deserialize("§e[TwitchVotesMinecraft] Verifying Twitch channel '" + target + "'..."));

        // Asynchronously check if channel exists on Twitch via Helix API
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean exists = TwitchValidator.checkChannelExists(plugin.getConfig(), target);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) return;

                if (!exists) {
                    player.sendMessage(SERIALIZER.deserialize("§c[TwitchVotesMinecraft] Twitch channel '" + target + "' does not exist!"));
                    return;
                }

                SettingsGUI.open(plugin, player, target);
            });
        });

        return true;
    }
}
