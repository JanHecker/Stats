package de.janhecker.stats.event;

import de.janhecker.stats.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class GameListener implements Listener {

    private final HashMap<UUID, UUID> lastDamager = new HashMap<>();

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Main.getInstance().handleJoin(player);
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CraftUser user = (CraftUser) StatsAPI.getUser(player);
        user.quit();
        Utils.async(() -> user.save());
        Main.getInstance().getUsers().remove(player.getUniqueId(), user);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void damage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof Player) {
            Player damager = Utils.getDamager(event.getDamager());
            if (damager == null) return;
            Player player = (Player) event.getEntity();
            if (!player.getUniqueId().equals(damager.getUniqueId())) {
                lastDamager.put(player.getUniqueId(), damager.getUniqueId());
            }
            if (player.getHealth() - event.getFinalDamage() <= 0) {
                death(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void event(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                    || event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE
                    || event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                return;
            }
            if (player.getHealth() - event.getFinalDamage() <= 0) {
                death(player);
            }
        }
    }

    private void death(Player player) {
        UUID uuid = player.getUniqueId();
        Player killer;
        if (lastDamager.containsKey(uuid)) {
            killer = Bukkit.getPlayer(lastDamager.get(uuid));
            lastDamager.remove(uuid);
        } else {
            killer = null;
        }
        StatsDeathEvent deathEvent = new StatsDeathEvent(player, killer);
        Bukkit.getPluginManager().callEvent(deathEvent);
        if (!deathEvent.isCancelled()) {
            ((CraftUser) StatsAPI.getUser(player)).death();
            if (killer != null) {
                ((CraftUser) StatsAPI.getUser(killer)).gainKill();
            }
        }
    }

}