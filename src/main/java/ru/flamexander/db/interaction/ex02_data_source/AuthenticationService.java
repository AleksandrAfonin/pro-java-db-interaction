package ru.flamexander.db.interaction.ex02_data_source;

import ru.flamexander.db.interaction.exceptions.ApplicationInitializationException;

import java.sql.*;

public class AuthenticationService {
    private DataSource dataSource;

    public AuthenticationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean register(String login, String password, String nickname) {
        try {
            dataSource.getStatement().executeUpdate(
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
        initTable();
    }

    private void initTable() {
        try {
            dataSource.getStatement().executeUpdate(
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
