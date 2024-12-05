package ru.flamexander.db.interaction.ex01_bad;

import ru.flamexander.db.interaction.exceptions.ApplicationInitializationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthenticationService {
    private Connection connection;
    private Statement statement;

    public AuthenticationService() {
    }

    public boolean register(String login, String password, String nickname) {
        try {
            statement.executeUpdate(
                    String.format("insert into users (login, password, nickname) values ('%s', '%s', '%s');",
                            login, password, nickname)
            );
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void init() {
        connect();
        initTable();
    }

    public void shutdown() {
        disconnect();
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:h2:file:./db;MODE=PostgreSQL");
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ApplicationInitializationException();
        }
    }

    private void disconnect() {
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

    private void initTable() {
        try {
            statement.executeUpdate(
                    "" +
                            "create table if not exists users (" +
                            "    id          bigserial primary key," +
                            "    login       varchar(255)," +
                            "    password    varchar(255)," +
                            "    nickname    varchar(255)" +
                            ")"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ApplicationInitializationException();
        }
    }
}
