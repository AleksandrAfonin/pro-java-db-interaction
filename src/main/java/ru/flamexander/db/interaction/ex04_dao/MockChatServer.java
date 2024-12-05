package ru.flamexander.db.interaction.ex04_dao;

public class MockChatServer {
    private DataSource dataSource;
    private UsersDao usersDao;
    private AuthenticationService authenticationService;

    public void start() {
        try {
            dataSource = new DataSource("jdbc:h2:file:./db;MODE=PostgreSQL");
            dataSource.connect();

            usersDao = new UsersDaoImpl(dataSource);

            authenticationService = new AuthenticationService(usersDao);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataSource.disconnect();
        }
    }
}
