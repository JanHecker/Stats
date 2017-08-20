package de.janhecker.stats.commands;

import de.janhecker.stats.CraftUser;
import de.janhecker.stats.Main;
import de.janhecker.stats.StatsAPI;
import de.janhecker.stats.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatsCommmand implements CommandExecutor {

    private final String prefix = "§6FeelPvP §8♦ §7 ";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            showStats(sender, ((Player) sender).getUniqueId());
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(prefix + "§cBenutze /stats 'Name'");
            return true;
        }
        Utils.async(() -> {
            UUID uuid = Utils.getUuid(args[0]);
            if (uuid == null) {
                sender.sendMessage(prefix + "§cDieser Spieler existiert nicht!");
                return;
            }
            showStats(sender, uuid);
        });
        return true;
    }

    private void showStats(CommandSender showSender, UUID uuid) {
        CraftUser user = (CraftUser) StatsAPI.getUser(uuid);
        if (user == null) {
            showSender.sendMessage(prefix + "§cDieser Spieler war noch nie auf dem Server!");
            return;
        }
        String borderMessage;
        if (showSender instanceof Player && ((Player) showSender).getUniqueId().equals(uuid)) {
            borderMessage = "§8§m§l======[ §r§eStats §8§m§l]======";
        } else {
            borderMessage = "§8§m§l======[ §r§eStats (" + Bukkit.getOfflinePlayer(uuid).getName() + ") §8§m§l]======";
        }
        showSender.sendMessage(borderMessage);
        showSender.sendMessage(prefix + "§7Kills: §a" + user.getKills());
        showSender.sendMessage(prefix + "§7Deaths: §a" + user.getDeaths());
        showSender.sendMessage(prefix + "§7Coins: §a" + user.getCoins());
        showSender.sendMessage(prefix + "§7Spielzeit: §a" + user.getFancyOnlinetime());
        showSender.sendMessage(borderMessage);
    }
}
