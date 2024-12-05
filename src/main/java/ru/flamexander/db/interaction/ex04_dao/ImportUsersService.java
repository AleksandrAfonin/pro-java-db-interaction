package ru.flamexander.db.interaction.ex04_dao;

import java.sql.SQLException;
import java.util.List;

public class ImportUsersService {
    private UsersDao usersDao;

    public ImportUsersService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public void importUsers(List<User> users) {
        try {
            usersDao.saveAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
