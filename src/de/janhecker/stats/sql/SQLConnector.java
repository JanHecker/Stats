package de.janhecker.stats.sql;

import java.sql.*;

public class SQLConnector {

    private Connection connection;
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

    public SQLConnector(String host, String database, String username, String password, int port) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public void openConnection() {
        if (isConnected()) return;
        try {
            Class.forName("com.mysql.jdbc.Driver");
//            String connectionString = "jdbc:mysql://localhost/" + dbName + "?user="
//                    + dbUserName + "&password=" + dbPassword + "&useUnicode=true&characterEncoding=UTF-8";
//            System.out.println("pw " + password);
//            System.out.println("name " + username);
//            System.out.println("host " + host);
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/"
                    + this.database, this.username, this.password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    private Statement getStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalError("Statement can't be created");
        }
    }

    public void executeUpdate(String commmand) {
        try {
            getStatement().executeUpdate(commmand);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalError("ExecuteUpdate could not be done");
        }
    }

    public ResultSet executeQuery(String command) {
        try {
            return getStatement().executeQuery(command);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalError("ExecuteQuery could not be done");
        }
    }

}
