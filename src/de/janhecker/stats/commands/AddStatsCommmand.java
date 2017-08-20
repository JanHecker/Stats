package de.janhecker.stats.commands;

import de.janhecker.stats.CraftUser;
import de.janhecker.stats.Main;
import de.janhecker.stats.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddStatsCommmand implements CommandExecutor {

    private final String prefix = "§6FeelPvP §8♦ §7 ";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("FeelPvP.AddStats")) {
            sender.sendMessage(prefix + "§cKeine Rechte!");
            return true;
        }
        // setstats <Name> <--> <i>
        // bsp /addstats LuckyLemonDE onlinetime 0
        String usage = prefix + "§cBenutze /addstats <Name> <deaths/onlinetime/kills/coins> <Wert>";
        if (args.length != 3) {
            sender.sendMessage(usage);
            return true;
        }
        String name = args[0];
        Player target = Bukkit.getPlayerExact(name);
        UUID uuid;
        boolean online = target != null;
        if (online) {
            uuid = target.getUniqueId();
            name = target.getName();
        } else {
            uuid = Utils.getUuid(name);
            if (uuid == null) {
                sender.sendMessage(prefix + "§cDieser Spieler existiert nicht!");
                return true;
            }
            name = Bukkit.getOfflinePlayer(uuid).getName();
        }
        CraftUser user = Main.getInstance().getUser(uuid);
        int value;
        try {
            value = Integer.valueOf(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(usage);
            return true;
        }
        String type = args[1].toLowerCase();
        switch (type) {
            case "deaths":
                user.setDeaths(user.getDeaths() + value);
                break;
            case "onlinetime":
                user.setOnlinetime(user.getOnlinetime() + value);
                break;
            case "kills":
                user.setKills(user.getKills() + value);
                break;
            case "coins":
                user.setCoins(user.getCoins() + value);
                break;
            default:
                sender.sendMessage(usage);
                return true;
        }
        sender.sendMessage(prefix + "§aDu hast den Wert " + type.toUpperCase() + " von " + name + " auf " + value + " gesetzt!");
        if (!online) {
            Utils.async(() -> user.save());
        }
        return true;
    }

}
