package ru.flamexander.db.interaction.ex00_simple_intro;

import java.sql.*;

public class Application {
    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {
        try {
            connect();
            createTable();
            insert();
            selectAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public static void connect() throws SQLException {
        // jdbc:h2:file:./db;MODE=PostgreSQL
        // jdbc:sqlite:jdb.db
        connection = DriverManager.getConnection("jdbc:sqlite:jdb.db");
        statement = connection.createStatement();
    }

    public static void createTable() throws SQLException {
        statement.executeUpdate(
                "" +
                        "create table if not exists users (" +
                        "    id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "    username    VARCHAR(255)" +
                        ")"
        );
    }

    public static void insert() throws SQLException {
        for (int i = 1; i < 5; i++) {
            statement.executeUpdate("insert into users (username) values ('user" + i + "');");
        }
    }

    public static void selectAll() {
        try (ResultSet rs = statement.executeQuery("select * from users;")) {
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
