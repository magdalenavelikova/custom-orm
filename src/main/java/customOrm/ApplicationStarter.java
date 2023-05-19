package customOrm;

import customOrm.entities.User;
import ormFramework.core.Connector;
import ormFramework.core.EntityManagerImpl;
import ormFramework.core.EntityScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * 1. Клас, нойто ще обиколи всички класове в проекта, за oа намери тези, които са анотирани с @Entity /наша анотация/ - EntityScanner
 * 3. Ще проверява дали има такава таблица и ако няма ще я създаде по предварително описани @Id и @Column(име, тип)
 * 4. Манипулация на данните чрез EntityManager - в него трябва да има
 * - функция за извличане на обект – find( Class)
 * - функция за извличане на обект с where– find( Class, where)
 * - функция за записване или редакция на обект по ID– persist(Object)
 * - функция за изтриване на обект по ID– delete(id, Class)
 **/

public class ApplicationStarter {
    private static BufferedReader reader;

    public static void main(String[] args) throws IOException, SQLException, IllegalAccessException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter password:");
        String password = reader.readLine();

        System.out.println("Enter DataBase Name:");
        String dbName = reader.readLine();
      //  String dbName = "orm_test";

        EntityScanner scanner = new EntityScanner(ApplicationStarter.class);

        Connector.createConnection("root", password, dbName);
        Connection connection = Connector.getConnection();

        EntityManagerImpl entityManager = new EntityManagerImpl(connection, scanner);
        entityManager.create();

        User user = new User("Test", 20, LocalDate.now());
        User user1 = new User("Test1", 5, LocalDate.now());
        entityManager.persist(user);
        entityManager.persist(user1);

        User first = entityManager.findFirst(User.class);
        System.out.println(first.getId() + " " + first.getUsername() + " " + first.getAge());

        User first1 = entityManager.findFirst(User.class, "user_name='Test1'");
        System.out.println(first1.getId() + " " + first1.getUsername() + " " + first1.getAge());

        entityManager.find(User.class, "age<20 AND registration_date>'2022-06-06'")
                .forEach(
                        u -> {
                            if (u != null) {
                                System.out.println(u);
                            }
                        });

    }
}