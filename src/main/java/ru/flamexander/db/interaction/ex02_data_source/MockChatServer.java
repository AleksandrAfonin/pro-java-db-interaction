package ru.flamexander.db.interaction.ex02_data_source;

public class MockChatServer {
    private DataSource dataSource;
    private AuthenticationService authenticationService;
    private BonusService bonusService;

    public void start() {
        try {
            dataSource = new DataSource("jdbc:h2:file:./db;MODE=PostgreSQL");
            dataSource.connect();

            authenticationService = new AuthenticationService(dataSource);
            authenticationService.init();

            bonusService = new BonusService(dataSource);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataSource.disconnect();
        }
    }
}
