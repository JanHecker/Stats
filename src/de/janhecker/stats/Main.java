package de.janhecker.stats;

import de.janhecker.stats.commands.AddStatsCommmand;
import de.janhecker.stats.commands.RemStatsCommand;
import de.janhecker.stats.commands.SetStatsCommmand;
import de.janhecker.stats.commands.StatsCommmand;
import de.janhecker.stats.event.GameListener;
import de.janhecker.stats.sql.SQLConnector;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main instance;
    private final HashMap<UUID, CraftUser> users = new HashMap<>();
    private SQLConnector sql;
    private YamlConfiguration cfg;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        users.values().forEach(user -> {
            user.quit();
            user.save();
        });
    }

    @Override
    public void onEnable() {
        instance = this;
        loadCfg();
        loadSQL();
        for (Player player : Bukkit.getOnlinePlayers()) {
            handleJoin(player);
        }
        getCommand("stats").setExecutor(new StatsCommmand());
        getCommand("setstats").setExecutor(new SetStatsCommmand());
        getCommand("addstats").setExecutor(new AddStatsCommmand());
        getCommand("remstats").setExecutor(new RemStatsCommand());
        getCommand("ranking").setExecutor(new RankingCommand());
        getCommand("nick").setExecutor(new NickCommand());
        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
    }

    private void loadCfg() {
        File file = new File("plugins/Stats/config.yml");
        if (file.isFile()) {
            cfg = YamlConfiguration.loadConfiguration(file);
        } else {
            cfg = new YamlConfiguration();
            cfg.options().header("Stats (Copyright 2017 - Jan Hecker)");
            cfg.set("host", "localhost");
            cfg.set("database", "skyfeel");
            cfg.set("user", "root");
            cfg.set("password", "password");
            cfg.set("port", 3306);
            cfg.set("table", "stats");
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public YamlConfiguration getCfg() {
        return cfg;
    }

    private void loadSQL() {
        sql = new SQLConnector(cfg.getString("host"), cfg.getString("database"),
                cfg.getString("user"), cfg.getString("password"), cfg.getInt("port"));
        sql.openConnection();
        sql.executeUpdate("CREATE TABLE IF NOT EXISTS " + cfg.getString("table") + " (" +
                "uuid VARCHAR(36), " +
                "kills INT, " +
                "deaths INT, " +
                "coins INT, " +
                "onlinetime VARCHAR(13), " +
                "PRIMARY KEY (uuid)" +
                ");");
//        SQLTableBuilder.name(cfg.getString("table"))
//                .add("uuid", "VARCHAR(36)")
//                .add("kills", "INT")
//                .add("deaths", "INT")
//                .add("onlinetime", "VARCHAR(13)")
//                .create(sql);
    }

    public SQLConnector getSQL() {
        return sql;
    }

    public HashMap<UUID, CraftUser> getUsers() {
        return users;
    }

    public void handleJoin(Player player) {
        CraftUser user = new CraftUser(player.getUniqueId());
        user.join();
        Utils.async(() -> user.load());
        users.put(player.getUniqueId(), user);
    }

    private HashMap<UUID, CraftUser> waiting = new HashMap<>();

    public CraftUser getUser(Player player) {
       return getUser(player.getUniqueId());
    }

    public CraftUser getUser(UUID uuid) {
        if (Main.getInstance().users.containsKey(uuid)) {
            return Main.getInstance().users.get(uuid);
        }
        CraftUser user = new CraftUser(uuid);
        new Thread(() -> {
            user.load();
            waiting.put(uuid, user);
        }).start();
        return get(uuid, 15);
    }

    private CraftUser get(UUID uuid, int i) {
        if (i == 0) {
            return null;
        }
        if (waiting.containsKey(uuid)) {
            CraftUser user = waiting.get(uuid);
            waiting.remove(uuid);
            return user;
        }
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return get(uuid, i - 1);
    }
}
