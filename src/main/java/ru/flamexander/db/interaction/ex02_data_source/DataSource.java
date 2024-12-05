package ru.flamexander.db.interaction.ex02_data_source;

import ru.flamexander.db.interaction.exceptions.ApplicationInitializationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSource {
    private Connection connection;
    private Statement statement;

    private String url;

    public DataSource(String url) {
        this.url = url;
    }

    public Connection getConnection() { // можем легко подменить на коннект из пула клннектов
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ApplicationInitializationException();
        }
    }

    public void disconnect() {
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
