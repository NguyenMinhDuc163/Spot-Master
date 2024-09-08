package Client.controller;

import server.dao.PlayerDAO;
import Client.view.LoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {

    public LoginController(LoginView loginView, PlayerDAO playerDAO) {

        // Thêm listener cho nút đăng nhập
        loginView.addLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = loginView.getUsername();
                String password = loginView.getPassword();

                // Kiểm tra xác thực
                if (playerDAO.authenticate(username, password)) {
                    loginView.setMessage("Login successful!");
                } else {
                    loginView.setMessage("Invalid username or password.");
                }
            }
        });
    }
}
