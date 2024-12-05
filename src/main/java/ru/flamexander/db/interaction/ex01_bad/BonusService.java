package ru.flamexander.db.interaction.ex01_bad;

import ru.flamexander.db.interaction.exceptions.ApplicationInitializationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BonusService {
    private Connection connection;
    private Statement statement;

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
}
