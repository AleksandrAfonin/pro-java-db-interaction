package ru.flamexander.db.interaction.lesson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbMigrator {
  private final DataSource dataSource;

  public DbMigrator(DataSource dataSource) {
    this.dataSource = dataSource;
    Statement statement = dataSource.getStatement();
    try {
      statement.executeUpdate("create table if not exists migration_history " +
              "(id INTEGER PRIMARY KEY AUTOINCREMENT, script varchar(255));");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void migrate(String fileName) {
    Statement statement = dataSource.getStatement();
    try {
      ResultSet resultSet = statement.executeQuery("SELECT script FROM migration_history WHERE script = '" + fileName + "';");
      if (!resultSet.next()){
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
          String command = bufferedReader.readLine();
          while (command != null) {
            try {
              statement.executeUpdate(command);
              command = bufferedReader.readLine();
            } catch (SQLException e) {
              e.printStackTrace();
            }
          }
          statement.executeUpdate("INSERT INTO migration_history (script) VALUES ('" + fileName + "');");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
