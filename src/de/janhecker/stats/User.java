package de.janhecker.stats;

public interface User {

    int getKills();

    int getDeaths();

    int getCoins();

    long getOnlinetime();

    String getFancyOnlinetime();

    void setKills(int kills);

    void setDeaths(int deaths);

    void setCoins(int coins);

    void setOnlinetime(long time);

}
