package ru.flamexander.db.interaction.lesson;

import java.sql.SQLException;

public class MockChatServer {
    public static void main(String[] args) {
        DataSource dataSource = null;
        try {
            System.out.println("Сервер чата запущен");
            dataSource = new DataSource("jdbc:sqlite:jdb.db");
            dataSource.connect();

            new DbMigrator(dataSource).migrate("dbinit.sql");

            AbstractRepository<User> usersRepository = new AbstractRepository<>(dataSource, User.class);
            usersRepository.save(new User(null, "BB", "CC", "DD"));
            System.out.println("usersRepository: " + usersRepository.findById(1L));
            System.out.println("List: " + usersRepository.findAll());
            usersRepository.update(new User(2L, "BBB", "CCC", "DDD"));
            System.out.println("List: " + usersRepository.findAll());
            usersRepository.deleteById(2L);
            System.out.println("List: " + usersRepository.findAll());

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
