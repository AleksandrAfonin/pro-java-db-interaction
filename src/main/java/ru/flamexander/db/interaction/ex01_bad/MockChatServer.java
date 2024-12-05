package ru.flamexander.db.interaction.ex01_bad;

import ru.flamexander.db.interaction.ex01_bad.AuthenticationService;

public class MockChatServer {
    private AuthenticationService authenticationService;

    public void start() {
        try {
            authenticationService = new AuthenticationService();
            authenticationService.init();



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            authenticationService.shutdown();
        }
    }
}
