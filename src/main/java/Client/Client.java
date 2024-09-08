package Client;

import Client.controller.LoginController;
import server.dao.DBConnection;
import server.dao.PlayerDAO;
import Client.view.LoginView;

public class Client {
    public static void main(String[] args) {
        PlayerDAO playerDAO = new PlayerDAO();
        LoginView loginView = new LoginView();

        LoginController loginController = new LoginController(loginView, playerDAO);
        loginView.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DBConnection.closeConnection();
        }));
    }
}
