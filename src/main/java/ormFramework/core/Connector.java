package ormFramework.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connector {

    private static Connection connection;
    private static final  String connectionPath="jdbc:mysql://localhost:3306/";

   /* private Connector() {
    }*/

    public static void createConnection(String username, String password, String dbName) throws SQLException {

        Properties properties=new Properties();
        properties.setProperty("user",username);
        properties.setProperty("password",password);
        connection= DriverManager.getConnection(connectionPath+dbName,properties);
    }

    public static Connection getConnection() {
       /* Singleton pattern

        private constructor +

       if (connection==null) {
            Connector.createConnection("root","pass", "db");
        }*/
        System.out.println("Connection is successful");
        return connection;
    }


}
