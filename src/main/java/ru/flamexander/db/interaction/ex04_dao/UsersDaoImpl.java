package ru.flamexander.db.interaction.ex04_dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsersDaoImpl implements UsersDao {
    private DataSource dataSource;

    public UsersDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(User user) throws SQLException {
        dataSource.getStatement().executeUpdate(
                String.format("insert into users (login, password, nickname) values ('%s', '%s', '%s');",
                        user.getLogin(), user.getPassword(), user.getNickname())
        );
    }

    @Override
    public void saveAll(List<User> users) throws SQLException {
        dataSource.getConnection().setAutoCommit(false);
        for (User u : users) {
            save(u);
        }
        dataSource.getConnection().setAutoCommit(true);
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> out = new ArrayList<>();
        try (ResultSet rs = dataSource.getStatement().executeQuery("select * from users;")) {
            while (rs.next()) {
                User user = new User(rs.getLong("id"), rs.getString("login"), rs.getString("password"), rs.getString("nickname"));
                out.add(user);
            }
        }
        return Collections.unmodifiableList(out);
    }
}
