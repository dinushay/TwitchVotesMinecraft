package twitchvotesminecraft.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SettingsGUIHolder implements InventoryHolder {
    private final String twitchName;
    private int intervalSeconds;
    private int eventSeconds;
    private int voteSeconds;
    private int maxVoteableEvents;
    private Inventory inventory;

    public SettingsGUIHolder(String twitchName, int intervalSeconds, int eventSeconds, int voteSeconds, int maxVoteableEvents) {
        this.twitchName = twitchName;
        this.intervalSeconds = intervalSeconds;
        this.eventSeconds = eventSeconds;
        this.voteSeconds = voteSeconds;
        this.maxVoteableEvents = maxVoteableEvents;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public String getTwitchName() {
        return twitchName;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public int getEventSeconds() {
        return eventSeconds;
    }

    public void setEventSeconds(int eventSeconds) {
        this.eventSeconds = eventSeconds;
    }

    public int getVoteSeconds() {
        return voteSeconds;
    }

    public void setVoteSeconds(int voteSeconds) {
        this.voteSeconds = voteSeconds;
    }

    public int getMaxVoteableEvents() {
        return maxVoteableEvents;
    }

    public void setMaxVoteableEvents(int maxVoteableEvents) {
        this.maxVoteableEvents = maxVoteableEvents;
    }
}
