package de.janhecker.stats;

import de.janhecker.stats.sql.SQLConnector;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CraftUser implements User {

    private final UUID uuid;
    private boolean exist;
    private int kills, deaths, coins;
    private long onlinetime, joined;

    public CraftUser(UUID uuid) {
        this.uuid = uuid;
    }

    public void join() {
        joined = System.currentTimeMillis();
    }

    public void load() {
        try {
            String command = "SELECT * FROM " + Main.getInstance().getCfg().getString("table")
                    + " WHERE uuid = '" + uuid + "';";
            ResultSet result = Main.getInstance().getSQL().executeQuery(command);
            exist = result.next();
            if (exist) {
                kills = result.getInt("kills");
                deaths = result.getInt("deaths");
                coins = result.getInt("coins");
                onlinetime = result.getLong("onlinetime");
            } else {
                kills = 0;
                onlinetime = 0;
                deaths = 0;
                coins = 0;
            }
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void quit() {
        onlinetime += System.currentTimeMillis() - joined;
    }

    public void save() {
        SQLConnector sql = Main.getInstance().getSQL();
        YamlConfiguration cfg = Main.getInstance().getCfg();
        if (exist) {
            sql.executeUpdate("UPDATE " + cfg.getString("table") + " SET kills = " + kills
                    + ", deaths = " + deaths + ", coins = " + coins + ", " + "onlinetime = " + onlinetime
                    + " WHERE uuid = '" + uuid + "';");
        } else {
            sql.executeUpdate("INSERT INTO " + cfg.getString("table") + " VALUES " +
                    "('" + uuid + "', " + kills + ", " + deaths + ", " + coins + ", " + onlinetime + ");");
//            sql.executeUpdate("INSERT INTO " + cfg.getString("table") + " ('uuid', 'kills', 'deaths', 'coins', 'onlinetime') VALUES " +
//                    "('" + uuid + "', " + kills + ", " + deaths + ", " + coins + ", " + onlinetime + ");");
        }
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public long getOnlinetime() {
        return onlinetime;
    }

    public void setOnlinetime(long onlinetime) {
        this.onlinetime = onlinetime;
        if (onlinetime == 0) {
            joined = System.currentTimeMillis();
        }
    }

    public String getFancyOnlinetime() {
        long time = onlinetime;
        if (joined != 0) {
            time += System.currentTimeMillis() - joined;
        }
        return Utils.getTimeFormat(time);
    }

    public void death() {
        deaths++;
    }

    public void gainKill() {
        kills++;
        coins += 5;
    }
}
