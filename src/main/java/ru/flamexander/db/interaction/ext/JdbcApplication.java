package ru.flamexander.db.interaction.ext;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <P>Пример простого взаимодействия с реляционной базой данных через JDBC. Для упрощения материала в качестве СУБД
 * взята SQLite в файловом режиме работы
 */
public class JdbcApplication {
    /**
     * <P>Тестовый класс Student
     */
    private static class Student {
        private Long id;
        private String name;
        private int score;
        private LocalDateTime createdAt;

        public Student(String name, int score) {
            this.name = name;
            this.score = score;
            this.createdAt = LocalDateTime.now();
        }

        public Student(Long id, String name, int score) {
            this.id = id;
            this.name = name;
            this.score = score;
            this.createdAt = LocalDateTime.now();
        }

        public Student(Long id, String name, int score, LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.score = score;
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return String.format("Студент [id: %d, имя: %s, балл: %d, дата создания: %s]", id, name, score, createdAt);
        }
    }

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement psInsert;

    public static void main(String[] args) {
        try {
            connect();
            dropTable();
            createTable();
            prepareStatements();
            //insertOperationCreateNewStudent(new Student("Bob", 100));
            insertOperationCreateNewStudent2(new Student("bob", 1));
            System.out.println(selectOperationFindAllStudents());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    /**
     * Пример rollback
     * <P><B>Следует обратить внимание:</B> при создании <code>SavePoint</code> автоматически будет выполнен setAutoCommit(false)
     *
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void rollback() throws SQLException {
        connection.setAutoCommit(true);
        statement.executeUpdate(String.format("insert into students (name, score, created_at) values ('%s', %d, '%s');", "Bob1", 10, LocalDateTime.now()));
        Savepoint savepoint = connection.setSavepoint();
        statement.executeUpdate(String.format("insert into students (name, score, created_at) values ('%s', %d, '%s');", "Bob2", 20, LocalDateTime.now()));
        connection.rollback(savepoint);
        statement.executeUpdate(String.format("insert into students (name, score, created_at) values ('%s', %d, '%s');", "Bob3", 30, LocalDateTime.now()));
        connection.commit();
    }

    /**
     * Пример использования batch + PreparedStatement
     * <B>Следует обратить внимание:</B> даже в таком случае требуется вручную управлять транзакцией, иначе
     * запросы из пачки будут выполняться каждый в своей транзакции
     *
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void batchExecutionDemoWithPreparedStatement() {
        try (PreparedStatement localPreparedStatementInsert = connection.prepareStatement("insert into students (name, score, created_at) VALUES (?, ?, ?)")) {
            connection.setAutoCommit(false);
            for (int i = 1; i <= 10000; i++) {
                localPreparedStatementInsert.setString(1, "Bob" + i);
                localPreparedStatementInsert.setInt(2, i * 10 % 100);
                localPreparedStatementInsert.setObject(3, LocalDateTime.now());
                localPreparedStatementInsert.addBatch();
            }
            int[] result = localPreparedStatementInsert.executeBatch();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Пример использования batch
     * <P><B>Следует обратить внимание:</B> даже в таком случае требуется вручную управлять транзакцией, иначе
     * запросы из пачки будут выполняться каждый в своей транзакции
     *
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void batchExecutionDemo() {
        try {
            connection.setAutoCommit(false);
            for (int i = 1; i <= 1000; i++) {
                statement.addBatch(String.format("insert into students (name, score, created_at) values ('%s', %d, '%s')", "Bob" + i, i * 10 % 100, LocalDateTime.now()));
                // statement.cancel();
            }
            int[] result = statement.executeBatch();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Пример использования транзакций. Отключая setAutoCommit(false) можно сравнить время выполнения
     *
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void transactionsDemo() throws SQLException {
        long time = System.currentTimeMillis();
        connection.setAutoCommit(false);
        for (int i = 1; i <= 10000; i++) {
            psInsert.setString(1, "Bob" + i);
            psInsert.setInt(2, i * 10 % 100);
            psInsert.setObject(3, LocalDateTime.now());
            psInsert.executeUpdate();
        }
        connection.setAutoCommit(true);
        System.out.printf("Затраченное время: %d мс.\n", System.currentTimeMillis() - time);
    }

    /**
     * Подготовка всех PreparedStatement'ов
     *
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    public static void prepareStatements() throws SQLException {
        psInsert = connection.prepareStatement("insert into students (name, score, created_at) values (?, ?, ?) returning (score);");
    }

    /**
     * Пример заполнения таблицы students через <code>PreparedStatement</code> в рамках одной транзакции
     *
     * @param count количество добавляемых студентов
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void addStudentsByPreparedStatement(int count) throws SQLException {
        for (int i = 1; i <= count; i++) {
            psInsert.setString(1, "Bob" + i);
            psInsert.setInt(2, i * 10 % 100);
            psInsert.setObject(3, LocalDateTime.now());
            psInsert.executeUpdate();
        }
    }

    /**
     * Работа с хранимыми процедурами (пример только кода, в используемой БД нет хранимых процедур)
     *
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    public static void callableStatementExample() throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call insertPerson(?, ?, ?)}");
        callableStatement.setInt(1, 10);
        callableStatement.setInt(2, 32);
        callableStatement.setString(3, "Bob");
        callableStatement.executeUpdate();
    }

    /**
     * Пример заполнения таблицы students через <code>Statement</code> в рамках одной транзакции
     * <P><B>Следует обратить внимание:</B> для подобных действий гораздо лучше подходит PreparedStatement
     *
     * @param count количество добавляемых студентов
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void fillTableByStatement(int count) throws SQLException {
        long time = System.currentTimeMillis();
        connection.setAutoCommit(false);
        LocalDateTime now = LocalDateTime.now();
        for (int i = 1; i <= count; i++) {
            statement.executeUpdate(String.format("insert into students (name, score, created_at) values ('%s', %d, '%s');", "Bob" + i, 10 + i * 10 % 100, now));
        }
        connection.setAutoCommit(true);
        System.out.printf("Время выполнения: %d мс.\n", System.currentTimeMillis() - time);
    }

    /**
     * Создание таблицы students
     *
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void createTable() throws SQLException {
        statement.executeUpdate(
                "" +
                        "create table if not exists students (" +
                        "    id          integer primary key autoincrement," +
                        "    name        varchar(255)," +
                        "    score       integer," +
                        "    created_at  timestamp" +
                        ")");
    }

    /**
     * <B>DROP</B> таблицы students
     *
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void dropTable() throws SQLException {
        statement.executeUpdate("drop table if exists students;");
    }

    /**
     * Получение списка студентов
     * <p>
     * <B>Следует обратить внимание:</B> В примере видно что в случае JDBC индексация столбцов начинается с 1, и существует возможность
     * обращаться к столбцам по имени
     *
     * @return неизменяемый список студентов
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static List<Student> selectOperationFindAllStudents() throws SQLException {
        try (ResultSet rs = statement.executeQuery("select * from students;")) {
            List<Student> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Student(rs.getLong(1), rs.getString("name"), rs.getInt(3), rs.getTimestamp(4).toLocalDateTime()));
            }
            return Collections.unmodifiableList(out);
        }
    }

    /**
     * Очистка таблицы students
     *
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void clearTable() throws SQLException {
        int affectedRowsCount = statement.executeUpdate("delete from students;");
        System.out.printf("Удалено строк: %d\n", affectedRowsCount);
    }

    /**
     * Выполнение <B>DELETE</B> операции для удаления студента по имени
     * <p>
     * <B>Следует обратить внимание:</B> <code>Statement</code> в данном случае используется только в качестве примера. Для параметризованных
     * запросов необходимо использовать <code>PreparedStatement</code>, который будет работать быстрее и безопаснее
     *
     * @param name имя удаляемого студента
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void deleteStudentByName(String name) throws SQLException {
        int affectedRowsCount = statement.executeUpdate(String.format("delete students where name = '%s';", name));
        System.out.printf("Удалено строк: %d, по имени: %s\n", affectedRowsCount, name);
    }

    /**
     * Выполнение <B>UPDATE</B> для изменения балла студента по его имени
     * <p>
     * <B>Следует обратить внимание:</B> <code>Statement</code> в данном случае используется только в качестве примера. Для параметризованных
     * запросов необходимо использовать <code>PreparedStatement</code>, который будет работать быстрее и безопаснее
     *
     * @param name  имя студента
     * @param score новое значение балла
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void updateOperationChangeStudentScoreByName(String name, int score) throws SQLException {
        int affectedRowsCount = statement.executeUpdate(String.format("update students set score = %d where name = '%s';", score, name));
        System.out.printf("Балл студента %s стал равен %d (изменено строк: %d)\n", name, score, affectedRowsCount);
    }

    /**
     * Выполнение <B>INSERT</B> для создания нового студента в базе
     * <p>
     * <B>Следует обратить внимание:</B> <code>Statement</code> в данном случае используется только в качестве примера. Для параметризованных
     * запросов необходимо использовать <code>PreparedStatement</code>, который будет работать быстрее и безопаснее
     *
     * @param student новый студент
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    private static void insertOperationCreateNewStudent(Student student) throws SQLException {
        int affectedRowsCount = statement.executeUpdate(String.format("insert into students (name, score, created_at) values ('%s', %d, '%s');", student.name, student.score, student.createdAt));
        System.out.printf("В БД сохранен новый студент %s (добавлено строк: %d)\n", student, affectedRowsCount);
    }

    private static void insertOperationCreateNewStudent2(Student student) throws SQLException {

        psInsert.setString(1, "Bob");
        psInsert.setInt(2, 1);
        psInsert.setObject(3, LocalDateTime.now());

        ResultSet rs = psInsert.executeQuery();

        while (rs.next()) {
            System.out.println(rs.getString(1) + " " + rs.getString(2));
        }
    }

    /**
     * Открытие соединения с БД
     * <p>
     * <B>Следует обратить внимание:</B> При использовании старых версий драйверов требовалось вручную выполнять
     * загрузку класса драйвера через, сейчас это делать необязательно
     * Class.forName()
     *
     * @throws SQLException <code>SQLException</code> пробрасывается просто наверх (допустимо в учебном примере)
     */
    public static void connect() throws SQLException {
        // Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
        statement = connection.createStatement();
        // connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        // DatabaseMetaData dbMetaData = connection.getMetaData();
    }

    /**
     * Закрытие соединения с БД
     * <p>
     * <B>Следует обратить внимание:</B> как орагнизована защита от NullPointerException и порядок закрытия ресурсов
     */
    public static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (psInsert != null) {
                psInsert.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}