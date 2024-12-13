package ru.flamexander.db.interaction.lesson;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractRepository<T> {
  private DataSource dataSource;
  private PreparedStatement psInsert;
  private PreparedStatement psFindById;
  private PreparedStatement psFindAll;
  private PreparedStatement psUpdate;
  private PreparedStatement psDelete;
  private String tableName;
  private Class<T> cls;
  private List<Field> fields;

  public AbstractRepository(DataSource dataSource, Class<T> cls) {
    this.dataSource = dataSource;
    this.cls = cls;
    prepareInsert();
    prepareFindById();
    prepareFindAll();
    prepareUpdate();
    prepareDelete();
  }

  public void deleteById(Long id){
    try {
      psDelete.setLong(1, id);
      psDelete.executeUpdate();
    } catch (SQLException e) {
      throw new ORMException("���-�� ����� �� ��� ��� �������� �� id: " + id);
    }
  }

  public void update(T entity){
    try {
      int i = 0;
      for (; i < fields.size(); i++) {
        psUpdate.setObject(i + 1, fields.get(i).get(entity));
      }
      Field id = entity.getClass().getDeclaredField("id");
      id.setAccessible(true);
      psUpdate.setObject(i + 1, id.get(entity));
      psUpdate.executeUpdate();
    } catch (SQLException e) {
      throw new ORMException("������ ��� ������ � ����� ������ ��� ����������: " + entity);
    } catch (NoSuchFieldException e) {
      throw new ORMException("�� ��������� ���� ��� ����������: " + entity);
    } catch (IllegalAccessException e) {
      throw new ORMException("��� ������� � ���� ������ ��� ����������: " + entity);
    }
  }

  public List<T> findAll(){
    List<T> list = new ArrayList<>();
    try {
      ResultSet resultSet = psFindAll.executeQuery();
      while (resultSet.next()){
        list.add(createEntity(resultSet));
      }
      return list;
    } catch (SQLException e) {
      throw new ORMException("������ ��� ������ � ����� ������ ��� ������ ���� ���������: ");
    } catch (InvocationTargetException e) {
      throw new ORMException("������ �������� ������ ��� ������ ���� ���������: ");
    } catch (NoSuchMethodException e) {
      throw new ORMException("�� ��������� ����� ��� ������ ���� ���������: ");
    } catch (InstantiationException e) {
      throw new ORMException("������ ��� �������� ���������� ��������: ");
    } catch (IllegalAccessException e) {
      throw new ORMException("��� ������� � ���� ������ ��� ������ ���� ���������:");
    }
  }

  public T findById(Long id) {
    try {
      psFindById.setLong(1, id);
      ResultSet resultSet = psFindById.executeQuery();
      if (!resultSet.next()) {
        return null;
      }
      return createEntity(resultSet);
    } catch (SQLException e) {
      throw new ORMException("������ ��� ������ � ����� ������ ��� ������ �������� �� id: ");
    } catch (InvocationTargetException e) {
      throw new ORMException("������ �������� ������ ��� ������ �������� �� id: ");
    } catch (NoSuchMethodException e) {
      throw new ORMException("�� ��������� ����� ��� ������ �������� �� id: ");
    } catch (InstantiationException e) {
      throw new ORMException("������ ��� �������� ���������� ��������: ");
    } catch (IllegalAccessException e) {
      throw new ORMException("��� ������� � ���� ������ ��� ������ �������� �� id: ");
    }
  }

  public void save(T entity) {
    try {
      for (int i = 0; i < fields.size(); i++) {
        psInsert.setObject(i + 1, fields.get(i).get(entity));
      }
      psInsert.executeUpdate();
    } catch (SQLException e) {
      throw new ORMException("������ ��� ������ � ����� ������ ��� ���������� ��������:");
    } catch (IllegalAccessException e) {
      throw new ORMException("��� ������� � ���� ������ ��� ���������� ��������:");
    }
  }

  private void prepareDelete(){
    StringBuilder query = new StringBuilder("DELETE FROM ");
    if (tableName.isBlank() || tableName == null){
      throw new ORMException("�� ������� ������������������� ����������� ��� ������ " + cls.getName() +
              "\n�� ���������� �������� �������. ����� prepareDelete.");
    }
    query.append(tableName);
    query.append(" WHERE id = ?;");
    try {
      psDelete = dataSource.getConnection().prepareStatement(query.toString());
    } catch (SQLException e) {
      throw new ORMException("�� ������� ������������������� ����������� ��� ������ " + cls.getName() +
              "\n������ ��� ���������� ��������� �� �������� ��������.");
    }
  }

  private void prepareUpdate(){
    StringBuilder query = new StringBuilder("UPDATE ");
    if (tableName.isBlank() || tableName == null){
      throw new ORMException("�� ������� ������������������� ����������� ��� ������ " + cls.getName() +
              "\n�� ���������� �������� �������. ����� prepareUpdate.");
    }
    query.append(tableName).append(" SET ");
    for (Field field : fields){
      query.append(field.getName()).append(" = ?, ");
    }
    query.setLength(query.length() - 2);
    query.append(" WHERE id = ?;");
    try {
      psUpdate = dataSource.getConnection().prepareStatement(query.toString());
    } catch (SQLException e) {
      throw new ORMException("�� ������� ������������������� ����������� ��� ������ " + cls.getName() +
              "\n������ ��� ���������� ��������� �� ���������� ��������.");
    }
  }

  private void prepareFindAll(){
    StringBuilder query = new StringBuilder("SELECT * FROM ");
    if (tableName.isBlank() || tableName == null){
      throw new ORMException("�� ������� ������������������� ����������� ��� ������ " + cls.getName() +
              "\n�� ���������� �������� �������. ����� prepareFindAll.");
    }
    query.append(tableName).append(";");
    try {
      psFindAll = dataSource.getConnection().prepareStatement(query.toString());
    } catch (SQLException e) {
      throw new ORMException("�� ������� ������������������� ����������� ��� ������ " + cls.getName() +
              "\n������ ��� ���������� ��������� �� ����� ���� ���������.");
    }
  }

  private void prepareFindById() {
    StringBuilder query = new StringBuilder("SELECT * FROM ");
    if (tableName.isBlank() || tableName == null){
      throw new ORMException("�� ������� ������������������� ����������� ��� ������ " + cls.getName() +
              "\n�� ���������� �������� �������. ����� prepareFindById.");
    }
    query.append(tableName).append(" WHERE id = ?;");
    try {
      psFindById = dataSource.getConnection().prepareStatement(query.toString());
    } catch (SQLException e) {
      throw new ORMException("�� ������� ������������������� ����������� ��� ������ " + cls.getName() +
              "\n������ ��� ���������� ��������� �� ����� �������� �� id.");
    }
  }

  private void prepareInsert() {
    if (!cls.isAnnotationPresent(RepositoryTable.class)) {
      throw new ORMException("����� �� ������������ ��� �������� �����������, �� ������� ��������� @RepositoryTable");
    }
    tableName = (cls.getAnnotation(RepositoryTable.class)).title();
    StringBuilder query = new StringBuilder("insert into ");
    if (tableName.isBlank() || tableName == null){
      throw new ORMException("�� ������� ������������������� ����������� ��� ������ " + cls.getName() +
              "\n�� ���������� �������� �������. ����� prepareInsert.");
    }
    query.append(tableName).append(" (");
    fields = Arrays.stream(cls.getDeclaredFields())
            .filter(f -> f.isAnnotationPresent(RepositoryField.class))
            .filter(f -> !f.isAnnotationPresent(RepositoryIdField.class))
            .collect(Collectors.toList());
    for (Field f : fields) {
      f.setAccessible(true);
    }
    for (Field f : fields) {
      query.append(f.getName()).append(", ");
    }
    query.setLength(query.length() - 2);
    query.append(") values (");
    for (Field f : fields) {
      query.append("?, ");
    }
    query.setLength(query.length() - 2);
    query.append(");");
    try {
      psInsert = dataSource.getConnection().prepareStatement(query.toString());
    } catch (SQLException e) {
      throw new ORMException("�� ������� ������������������� ����������� ��� ������ " + cls.getName() +
              "\n������ ��� ���������� ��������� �� ���������� ��������.");
    }
  }

  private T createEntity(ResultSet resultSet) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
    T entity = cls.getConstructor().newInstance();
    List<Field> fields = Arrays.asList(cls.getDeclaredFields());
    for (Field field : fields) {
      field.setAccessible(true);
    }

    for (int i = 0; i < fields.size(); i++) {
      if (i == 0) {
        fields.get(i).set(entity, resultSet.getLong(i + 1));
      } else {
        fields.get(i).set(entity, resultSet.getString(i + 1));
      }
    }
    return entity;
  }
}
