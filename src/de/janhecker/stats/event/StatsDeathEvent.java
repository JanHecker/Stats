package de.janhecker.stats.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StatsDeathEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Player killer;
    private boolean cancelled;

    public StatsDeathEvent(Player player, Player killer) {
        this.player = player;
        this.killer = killer;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getKiller() {
        return killer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
