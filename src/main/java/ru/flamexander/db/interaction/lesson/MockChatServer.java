package ru.flamexander.db.interaction.lesson;

import java.sql.SQLException;

public class MockChatServer {
    public static void main(String[] args) {
        DataSource dataSource = null;
        try {
            System.out.println("Сервер чата запущен");
            dataSource = new DataSource("jdbc:sqlite:jdb.db");
            dataSource.connect();

            UsersDao usersDao = new UsersDao(dataSource);
            usersDao.init();
            System.out.println(usersDao.getAllUsers());
//            usersDao.save(new User(null, "A", "A", "A"));
//            System.out.println(usersDao.getAllUsers());
            AbstractRepository<User> usersRepository = new AbstractRepository<>(dataSource, User.class);
            usersRepository.save(new User(null, "B", "B", "B"));
            System.out.println(usersDao.getAllUsers());

//            AuthenticationService authenticationService = new AuthenticationService(usersDao);
//            UsersStatisticService usersStatisticService = new UsersStatisticService(usersDao);
//            BonusService bonusService = new BonusService(dataSource);
//            bonusService.init();

//            authenticationService.register("A", "A", "A");
            // Основная работа сервера чата
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                dataSource.close();
            }
            System.out.println("Сервер чата завершил свою работу");
        }
    }
}
