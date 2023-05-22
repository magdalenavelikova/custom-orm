package ormFramework.core;

import customOrm.exceptions.ORMException;
import ormFramework.annotations.Column;
import ormFramework.annotations.Entity;
import ormFramework.annotations.Id;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityManagerImpl implements EntityManager {
    private final Connection connection;
    private final EntityScanner entityScanner;

    public EntityManagerImpl(Connection connection, EntityScanner entityScanner) {
        this.connection = connection;
        this.entityScanner = entityScanner;

    }

    @Override
    public boolean create() throws SQLException {
        List<Class<?>> classes = entityScanner.getClasses();
        for (Class<?> classInfo : classes) {
            Entity entityInfo = classInfo.getAnnotation(Entity.class);
            String tableName = entityInfo.name();
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s%n(", tableName);
            String pkDefinitions = "";
            for (Field field : classInfo.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Id.class)) {
                    continue;
                }
                String columnInfo = getColumnInfo(field);
                if (columnInfo.startsWith("id")) {
                    sql += columnInfo + ", \n";
                    pkDefinitions = "\n CONSTRAINT " + tableName + "_pk PRIMARY KEY (id));";
                } else {
                    sql += columnInfo + ", ";
                }
            }
            sql += pkDefinitions + "\n";

            connection.createStatement().execute(sql);
        }
        return false;
    }


    @Override
    public <E> boolean persist(E entity) throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Field idField = getIdField(entity);
        idField.setAccessible(true);
        long id = (long) idField.get(entity);
        String tableName = this.getTableName(entity.getClass());
        String fieldList = this.getFieldList(entity);
        String valueList = this.getValueList(entity);
        String sql = "";
        if (id == 0) {
            sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, fieldList, valueList);

        } else {
            String where = String.format("id=%s", id);
            Object o = this.findFirst(entity.getClass(), where);
            String valueListFromTable = getValueList(o);
            StringBuilder setValues = new StringBuilder();
            setValues.append("SET ");

            if (!valueList.equals(valueListFromTable)) {
                String[] columnName = fieldList.split(", ");
                String[] oValues = valueList.split(", ");
                String[] tableValues = valueListFromTable.split(", ");
                for (int i = 0; i < tableValues.length; i++) {
                    if (!oValues[i].equals(tableValues[i])) {
                        setValues.append(columnName[i] + "=" + oValues[i] + ", ");
                    }
                }
                setValues.replace(setValues.length() - 2, setValues.length() - 1, "");
                sql = String.format("UPDATE %s %s WHERE id=%d;", tableName, setValues, id);
            }
        }

        if (sql.equals("")) {
            return false;
        }
        return this.connection.prepareStatement(sql).execute();
    }

    @Override
    public <E> boolean delete(E entity) throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Field idField = getIdField(entity);
        idField.setAccessible(true);
        long id = (long) idField.get(entity);
        String tableName = this.getTableName(entity.getClass());
        String sql = "";

            sql = String.format("DELETE FROM %s WHERE id=%s;", tableName, id);

        return this.connection.prepareStatement(sql).execute();
    }


    @Override
    public <E> Iterable<E> find(Class<E> table) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        return find(table, "");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Iterable<E> find(Class<E> table, String where) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        String tableName = this.getTableName(table);
        String sql = String.format("SELECT * FROM %s %s;", tableName, where.equals("") ? "" : "WHERE " + where);
        ResultSet resultSet = this.connection.prepareStatement(sql).executeQuery();
        if (!resultSet.next()) {
            return null;
        }
        List<E> result = new ArrayList<>();
        E entity = createEntity(table, resultSet);
        while (entity != null) {
            result.add(entity);
            entity = createEntity(table, resultSet);
        }
        return result;
    }

    @Override
    public <E> E findFirst(Class<E> table) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return findFirst(table, "");

    }

    @Override
    public <E> E findFirst(Class<E> table, String where) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String tableName = this.getTableName(table);
        String sql = String.format("SELECT * FROM %s %s LIMIT 1", tableName, where.equals("") ? "" : "WHERE " + where);
        ResultSet resultSet = this.connection.prepareStatement(sql).executeQuery();

        return this.createEntity(table, resultSet);


    }

    private <E> E createEntity(Class<E> table, ResultSet resultSet) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        if (!resultSet.next()) {
            return null;
        }
        E entity = table.getDeclaredConstructor().newInstance();
        for (Field declaredField : table.getDeclaredFields()) {
            if (!declaredField.isAnnotationPresent(Column.class) && !declaredField.isAnnotationPresent(Id.class)) {
                continue;
            }
            String fieldName = getColumnName(declaredField);
            String value = resultSet.getString(fieldName);

            entity = this.fillData(entity, declaredField, value);
        }
        return entity;
    }

    private String getColumnInfo(Field declaredField) {

        Column columnAnnotation = declaredField.getAnnotation(Column.class);
        String fieldName = columnAnnotation == null ? declaredField.getName() : columnAnnotation.name();
        String fieldDefinition = columnAnnotation == null ? "INT AUTO_INCREMENT" : columnAnnotation.columnDefinition();
        return fieldName + " " + fieldDefinition;
    }

    private String getColumnName(Field declaredField) {

        Column columnAnnotation = declaredField.getAnnotation(Column.class);
        return columnAnnotation == null ? declaredField.getName() : columnAnnotation.name();


    }

    private <E> E fillData(E entity, Field declaredField, String value) throws IllegalAccessException {
        declaredField.setAccessible(true);

        if (declaredField.getType() == long.class || declaredField.getType() == Long.class) {
            declaredField.setLong(entity, Long.parseLong(value));
        } else if (declaredField.getType() == LocalDate.class) {
            declaredField.set(entity, LocalDate.parse(value));
        } else if (declaredField.getType() == int.class || declaredField.getType() == Integer.class) {
            declaredField.setInt(entity, Integer.parseInt(value));
        } else if (declaredField.getType() == String.class) {
            declaredField.set(entity, value);
        } else {
            throw new ORMException("Unsupported type " + declaredField.getType());
        }
        return entity;
    }

    private String getTableName(Class<?> clazz) {
        Entity annotation = clazz.getAnnotation(Entity.class);
        if (annotation == null) {
            throw new ORMException("Provided Class does not have Entity annotation");
        }
        return annotation.name();
    }

    private <E> String getFieldList(E entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.getAnnotation(Column.class) != null)
                .map(f -> f.getAnnotation(Column.class).name())
                .collect(Collectors.joining(", "));

    }

    private <E> Field getIdField(E entity) {
        return Arrays.stream(entity
                        .getClass()
                        .getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("This entity doesn't have id"));


    }

    private <E> String getValueList(E entity) throws IllegalAccessException {
        List<Field> fields = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.getAnnotation(Column.class) != null).toList();

        List<String> list = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            list.add("\"" + field.get(entity).toString() + "\"");
        }
        return String.join(", ", list);
    }
}
