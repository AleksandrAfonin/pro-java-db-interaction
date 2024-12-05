package ru.flamexander.db.interaction.ex04_dao;

import java.sql.SQLException;
import java.util.List;

public interface UsersDao {
    void save(User user) throws SQLException;
    List<User> findAll() throws SQLException;
    void saveAll(List<User> users) throws SQLException;
}
