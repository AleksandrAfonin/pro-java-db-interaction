package ru.flamexander.db.interaction.ex04_dao;

import java.sql.SQLException;

public class AuthenticationService {
    private UsersDao usersDao;

    public AuthenticationService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public boolean register(String login, String password, String nickname) {
        try {
            usersDao.save(new User(login, password, nickname));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
