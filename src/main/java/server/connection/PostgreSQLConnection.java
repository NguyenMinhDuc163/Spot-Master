package server.connection;

import io.github.cdimascio.dotenv.Dotenv;
import server.helper.LoggerHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConnection implements IDatabaseConnection {

    private static PostgreSQLConnection instance;
    private static Dotenv dotenv = Dotenv.load();
    private String jdbcURL = "jdbc:postgresql://"
            + dotenv.get("POSTGRES_DB_HOST") + ":"
            + dotenv.get("POSTGRES_DB_PORT") + "/"
            + dotenv.get("POSTGRES_DB_NAME");

    private String jdbcUsername = dotenv.get("POSTGRES_DB_USERNAME");
    private String jdbcPassword = dotenv.get("POSTGRES_DB_PASSWORD");
    private Connection connection;

    private PostgreSQLConnection() {
        // Constructor private để thực hiện Singleton pattern
    }

    public static PostgreSQLConnection getInstance() {
        if (instance == null) {
            instance = new PostgreSQLConnection();
        }
        return instance;
    }

    @Override
    public Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            System.out.println("Connected to PostgreSQL Database.");
            LoggerHandler.getInstance().log("[INFO] " + "Connected to PostgreSQL Database.");

        } catch (SQLException | ClassNotFoundException e) {
            LoggerHandler.getInstance().log("[ERROR] " + e);
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
