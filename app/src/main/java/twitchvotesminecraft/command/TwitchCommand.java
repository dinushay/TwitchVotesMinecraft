package twitchvotesminecraft.command;

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
            sender.sendMessage(plugin.getMessageManager().getComponent("general.only-in-game"));
            return true;
        }

        // Require OP permission
        if (!player.isOp()) {
            player.sendMessage(plugin.getMessageManager().getComponent("general.must-be-op"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageManager().getComponent("general.usage", java.util.Map.of("%cmd%", commandLabel)));
            return true;
        }

        String target = args[0].trim();

        // Handle cancel subcommand
        if (target.equalsIgnoreCase("cancel")) {
            boolean stopped = plugin.stopVoteSession(player);
            if (stopped) {
                player.sendMessage(plugin.getMessageManager().getComponent("session.cancelled"));
            } else {
                player.sendMessage(plugin.getMessageManager().getComponent("session.no-active-session"));
            }
            return true;
        }

        // Validate username syntax
        if (!TWITCH_NAME_PATTERN.matcher(target).matches()) {
            player.sendMessage(plugin.getMessageManager().getComponent("general.invalid-syntax"));
            return true;
        }

        player.sendMessage(plugin.getMessageManager().getComponent("general.verifying", java.util.Map.of("%channel%", target)));

        // Asynchronously check if channel exists on Twitch via Helix API
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean exists = TwitchValidator.checkChannelExists(plugin.getConfig(), target);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) return;

                if (!exists) {
                    player.sendMessage(plugin.getMessageManager().getComponent("general.channel-not-found", java.util.Map.of("%channel%", target)));
                    return;
                }

                SettingsGUI.open(plugin, player, target);
            });
        });

        return true;
    }
}
