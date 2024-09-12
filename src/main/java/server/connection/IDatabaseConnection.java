package server.connection;

import java.sql.Connection;

public interface IDatabaseConnection {
    Connection getConnection();
    void setConnection(Connection connection);
}
