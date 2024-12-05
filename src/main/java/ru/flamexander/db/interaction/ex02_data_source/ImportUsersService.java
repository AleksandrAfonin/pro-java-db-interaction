package ru.flamexander.db.interaction.ex02_data_source;

import java.sql.SQLException;
import java.util.List;

public class ImportUsersService {
    private class UserInfo {
        private String login;
        private String password;
        private String nickname;
    }

    private DataSource dataSource;

    public ImportUsersService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void importUsers(List<UserInfo> userInfoList) {
        try {
            dataSource.getConnection().setAutoCommit(false);
            for (UserInfo u : userInfoList) {
                dataSource.getStatement().executeUpdate(
                        String.format("insert into users (login, password, nickname) values ('%s', '%s', '%s');",
                                u.login, u.password, u.nickname)
                );
            }
            dataSource.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
