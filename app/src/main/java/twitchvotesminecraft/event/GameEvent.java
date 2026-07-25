package twitchvotesminecraft.event;

import org.bukkit.entity.Player;
import twitchvotesminecraft.App;

public interface GameEvent {
    /**
     * @return Human-readable name of the event shown in chat and scoreboard.
     */
    String getName();

    /**
     * @return Brief description of the event's effect.
     */
    String getDescription();

    /**
     * @return true if the event executes instantly, false if it runs for a duration (eventSeconds).
     */
    boolean isInstant();

    /**
     * Executes the event logic on the target player.
     * @param player Target Minecraft player.
     * @param plugin Plugin instance for scheduling tasks if duration-based.
     * @param eventSeconds Duration in seconds for non-instant events.
     */
    void execute(Player player, App plugin, int eventSeconds);
}
