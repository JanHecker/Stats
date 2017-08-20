package de.janhecker.stats;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class RankingCommand implements CommandExecutor {

    private final String prefix = "§6FeelPvP §8♦ §7 ";

    public RankingCommand() {
        Main main = Main.getInstance();
//        ResultSet set = main.getSQL().executeQuery("SELECT uuid, username FROM tabelle ORDER BY kills DESC LIMIT 10");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(prefix + "§cBenutze /ranking (coins/onlinetime/kills/deaths)");
            return true;
        }
        List<String> list = new ArrayList<>();
        list.add("coins");
        list.add("onlinetime");
        list.add("deaths");
        list.add("kills");
        String s = args[0].toLowerCase();
        if (!list.contains(s)) {
            sender.sendMessage(prefix + "§cBenutze /ranking (coins/onlinetime/kills/deaths)");
        }
        sendRanking(sender, s);
        return true;
    }

    private void sendRanking(CommandSender sender, String type) {
        sender.sendMessage("§cNoch in Wartung!");
    }

}
