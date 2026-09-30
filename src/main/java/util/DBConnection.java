package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    public static Connection getConnection() throws SQLException, ClassNotFoundException {

        Properties prop= new Properties();
        try (InputStream input = DBConnection.class.getResourceAsStream("/db.properties")) {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url  = prop.getProperty("db.url"); ;
        String user = prop.getProperty("db.user");
        String pass = prop.getProperty("db.password");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, pass);
    }
}
