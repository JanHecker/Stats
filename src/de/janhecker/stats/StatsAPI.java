package de.janhecker.stats;

import org.bukkit.entity.Player;

import java.util.UUID;

public class StatsAPI {

    public static User getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public static User getUser(UUID uuid) {
        return Main.getInstance().getUser(uuid);
    }
}
