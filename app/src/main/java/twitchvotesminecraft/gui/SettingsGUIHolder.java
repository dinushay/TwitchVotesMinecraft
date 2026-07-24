package twitchvotesminecraft.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SettingsGUIHolder implements InventoryHolder {
    private final String twitchName;
    private String mode;
    private int intervalSeconds;
    private int eventSeconds;
    private int voteSeconds;
    private int maxVoteableEvents;
    private boolean showPollInMinecraft;
    private Inventory inventory;

    public SettingsGUIHolder(String twitchName, String mode, int intervalSeconds, int eventSeconds, int voteSeconds, int maxVoteableEvents, boolean showPollInMinecraft) {
        this.twitchName = twitchName;
        this.mode = mode;
        this.intervalSeconds = intervalSeconds;
        this.eventSeconds = eventSeconds;
        this.voteSeconds = voteSeconds;
        this.maxVoteableEvents = maxVoteableEvents;
        this.showPollInMinecraft = showPollInMinecraft;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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

    public boolean isShowPollInMinecraft() {
        return showPollInMinecraft;
    }

    public void setShowPollInMinecraft(boolean showPollInMinecraft) {
        this.showPollInMinecraft = showPollInMinecraft;
    }
}
