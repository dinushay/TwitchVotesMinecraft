package twitchvotesminecraft.command;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import twitchvotesminecraft.App;
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
            "/twitchvotesminecraft <twitch-name>",
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

        if (args.length < 1) {
            player.sendMessage(SERIALIZER.deserialize("§cUsage: /" + commandLabel + " <twitch-name>"));
            return true;
        }

        String twitchName = args[0].trim();
        if (!TWITCH_NAME_PATTERN.matcher(twitchName).matches()) {
            player.sendMessage(SERIALIZER.deserialize("§c[TwitchVotesMinecraft] Invalid Twitch username! Usernames must be 4-25 alphanumeric characters or underscores."));
            return true;
        }

        SettingsGUI.open(plugin, player, twitchName);
        return true;
    }
}
