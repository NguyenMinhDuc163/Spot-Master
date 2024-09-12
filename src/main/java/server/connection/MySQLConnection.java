package server.connection;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection implements IDatabaseConnection {

    private static MySQLConnection instance;
    private static final Dotenv dotenv = Dotenv.load();

    // Lấy giá trị từ file .env
    private final String jdbcHost = dotenv.get("MYSQL_DB_HOST");
    private final String jdbcPort = dotenv.get("MYSQL_DB_PORT");
    private final String jdbcDatabase = dotenv.get("MYSQL_DB_NAME");
    private final String jdbcURL = "jdbc:mysql://" + jdbcHost + ":" + jdbcPort + "/" + jdbcDatabase + "?useSSL=false";
    private final String jdbcUsername = dotenv.get("MYSQL_DB_USERNAME");
    private final String jdbcPassword = dotenv.get("MYSQL_DB_PASSWORD");
    private Connection connection;

    private MySQLConnection() {
        // Constructor private để thực hiện Singleton pattern
    }

    public static MySQLConnection getInstance() {
        if (instance == null) {
            instance = new MySQLConnection();
        }
        return instance;
    }

    @Override
    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            System.out.println("Connected to MySQL Database.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
