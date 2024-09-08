package server.dao;


import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Dotenv dotenv = Dotenv.load();
    private static final String URL = "jdbc:mysql://localhost:3306/spot_master_db";
    private static final String USER = dotenv.get("DB_USERNAME");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    private static Connection connection = null;

    // Phương thức để lấy kết nối
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Kết nối thành công tới cơ sở dữ liệu!");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Kết nối không thành công!");
            }
        }
        return connection;
    }

    // Phương thức để đóng kết nối khi không sử dụng nữa
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Đã đóng kết nối tới cơ sở dữ liệu.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
