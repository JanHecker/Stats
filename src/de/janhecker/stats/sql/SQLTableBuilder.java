package de.janhecker.stats.sql;

import java.util.HashMap;

public class SQLTableBuilder {

    private final String name;
    private final HashMap<String, String> values = new HashMap<>();

    private SQLTableBuilder(String table) {
        this.name = table;
    }

    public static SQLTableBuilder name(String table) {
        return new SQLTableBuilder(table);
    }

    public SQLTableBuilder add(String name, String type) {
        values.put(name, type);
        return this;
    }

    public void create(SQLConnector sql) {
        StringBuilder values = new StringBuilder();
        this.values.forEach((name, type) -> {
            values.append(name);
            values.append(" ");
            values.append(type);
            values.append(", ");
        });
        String string = values.toString();
        if (string.length() > 1) {
            string = string.substring(0, string.length() - 2);
        }
        sql.executeUpdate("CREATE TABLE IF NOT EXISTS " + name + " (" + string + ");");
    }

}
