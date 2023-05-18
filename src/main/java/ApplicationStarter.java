import entities.User;
import orm.Connector;
import orm.EntityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 1. Клас за настройките за вързка с базата - ще връща обект - EntityManager , който ще може да управлява базата на абстрактно ниво
 * - вид база
 * - потребителско име и парола
 * - име на базата
 * 2. Преди да се върне такъв обект, ще обиколим всички класове в проекта, за а намерим тези, които са анотирани с @Entity /наша анотация/
 * 3. Ще проверяваме дали има такава таблица и ако няма ще я създадем по предварително описани @Id и @Column(име, тип)
 * 4. Манипулация на данните чрез EntityManager - в него трябва да има
 * - функция за извличане на обект по ID– find(id, Class)
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


       // EntityScanner scanner= new EntityScanner(ApplicationStarter.class);

    //    System.out.println(String.join(",", scanner.getClasses().toString()));

        Connector.createConnection("root", password, dbName);
        Connection connection = Connector.getConnection();

        EntityManager<User> userManager = new EntityManager<User>(connection);
//        User user = new User("Test", 20, LocalDate.now());
//        User user1 = new User("Test1", 5, LocalDate.now());
//        userManager.persist(user1);
        User first = userManager.findFirst(User.class);
        System.out.println(first.getId() + " " + first.getUsername() + " " + first.getAge());

        User first1 = userManager.findFirst(User.class, "user_name='Test1'");
        System.out.println(first1.getId() + " " + first1.getUsername() + " " + first1.getAge());

        userManager.find(User.class, "age<20 AND registration_date>'2022-06-06'")
                .forEach(
                        u -> {
                            if (u != null) {
                                System.out.println(u.toString());
                            }
                        });

    }
}