package ormFramework.core;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface EntityManager {
    boolean create() throws SQLException;
    <E> boolean persist(E entity) throws SQLException, IllegalAccessException;
    <E>Iterable<E> find(Class<E> table) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
    <E>Iterable<E> find(Class<E> table,String where) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
    <E>E findFirst(Class<E> table) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
    <E>E findFirst(Class<E> table,String where) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;


}
