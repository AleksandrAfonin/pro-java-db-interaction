package ru.flamexander.db.interaction.lesson;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UsersDao {
    private DataSource dataSource;

    public UsersDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void init() throws SQLException {
        dataSource.getStatement().executeUpdate(
                "" +
                        "create table if not exists users (" +
                        "    id          bigserial primary key," +
                        "    login       varchar(255)," +
                        "    password    varchar(255)," +
                        "    nickname    varchar(255)" +
                        ")"
        );
    }

    public Optional<User> getUserByLoginAndPassword(String login, String password) {
        try (ResultSet rs = dataSource.getStatement().executeQuery("select * from users where login = '" + login + "' AND password = '" + password + "'")) {
            return Optional.of(new User(rs.getLong("id"), rs.getString("login"), rs.getString("password"), rs.getString("nickname")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> getUserById(Long id) {
        try (ResultSet rs = dataSource.getStatement().executeQuery("select * from users where id = " + id)) {
            if (rs.next() != false) {
                return Optional.of(new User(rs.getLong("id"), rs.getString("login"), rs.getString("password"), rs.getString("nickname")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        try (ResultSet rs = dataSource.getStatement().executeQuery("select * from users")) {
            while (rs.next() != false) {
                result.add(new User(rs.getLong("id"), rs.getString("login"), rs.getString("password"), rs.getString("nickname")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableList(result);
    }

    public void save(User user) throws SQLException {
        dataSource.getStatement().executeUpdate(String.format("insert into users (login, password, nickname) values ('%s', '%s', '%s');", user.getLogin(), user.getPassword(), user.getNickname()));
    }

    public void saveAll(List<User> users) throws SQLException {
        dataSource.getConnection().setAutoCommit(false);
        for (User u : users) {
            dataSource.getStatement().executeUpdate(String.format("insert into users (login, password, nickname) values ('%s', '%s', '%s');", u.getLogin(), u.getPassword(), u.getNickname()));
        }
        dataSource.getConnection().setAutoCommit(true);
    }
}
