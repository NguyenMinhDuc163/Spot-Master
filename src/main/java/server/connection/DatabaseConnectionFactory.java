package server.connection;

public class DatabaseConnectionFactory {

    public static IDatabaseConnection getDatabaseConnection(String dbType) {
        return switch (dbType.toLowerCase()) {
            case "mysql" -> MySQLConnection.getInstance();
            case "postgresql" -> PostgreSQLConnection.getInstance();
            default -> throw new IllegalArgumentException("Unknown database type: " + dbType);
        };
    }
}
