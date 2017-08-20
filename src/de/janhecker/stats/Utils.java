package de.janhecker.stats;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.spigotmc.SpigotConfig;

import java.text.DecimalFormat;
import java.util.UUID;

public class Utils {

    public static void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), runnable);
    }

    public static UUID getUuid(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            return player.getUniqueId();
        }
        if (MinecraftServer.getServer().getOnlineMode() || SpigotConfig.bungee) {
            GameProfile profile = MinecraftServer.getServer().getUserCache().getProfile(name);
            if (profile != null) {
                return profile.getId();
            }
        }
        return null;
    }

    static String getTimeFormat(long ms) {
        if (ms < 1500) {
            return new DecimalFormat("0.00").format((double) ms / 1000) + "ms";
        }
        int SEC = 1000;
        int MIN = SEC * 60;
        int HOUR = MIN * 60;
        int DAY = HOUR * 24;

        int days = 0;
        while (ms > DAY) {
            days++;
            ms -= DAY;
        }
        int hour = 0;
        while (ms > HOUR) {
            hour++;
            ms -= HOUR;
        }
        int mins = 0;
        while (ms > MIN) {
            mins++;
            ms -= MIN;
        }
        int sec = 0;
        while (ms > SEC) {
            sec++;
            ms -= SEC;
        }
        if (days != 0) {
            return days + " Tage, " + hour + " Stunden und " + mins + " Minuten";
        }
        if (hour != 0) {
            return hour + " Stunden, " + mins + " Minuten und " + sec + " Sekunden";
        }
        if (mins != 0) {
            return mins + " Minuten und " + sec + " Sekunden";
        }
        return sec + " Sekunden";
    }

    public static Player getDamager(Entity entity) {
        if (entity instanceof Player) {
            return ((Player) entity);
        }
        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player) {
                return ((Player) shooter);
            }
        }
        return null;
    }
}
