package ru.flamexander.db.interaction.lesson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BonusService {
    private DataSource dataSource;

    public BonusService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void init() throws SQLException {
        dataSource.getStatement().executeUpdate(
                "" +
                        "create table if not exists bonuses (" +
                        "    id          bigserial primary key," +
                        "    amount      int," +
                        "    login       varchar(255)" +
                        ")"
        );
        System.out.println("Сервис бонусов успешно запущен");
    }

    public void createBonus(String login, int amount) {
        // dataSource.getStatement()...
    }
}
