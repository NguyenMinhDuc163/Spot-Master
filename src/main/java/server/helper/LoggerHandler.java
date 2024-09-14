package server.helper;

import server.view.ServerView;

public class LoggerHandler {
    private static LoggerHandler instance;
    private ServerView serverView;

    // Private constructor để ngăn chặn việc tạo instance bên ngoài
    private LoggerHandler() {}

    // Trả về instance duy nhất của LoggerHandler
    public static synchronized LoggerHandler getInstance() {
        if (instance == null) {
            instance = new LoggerHandler();
        }
        return instance;
    }

    // Đặt ServerView để LoggerHandler có thể ghi log vào JTextArea
    public void setServerView(ServerView serverView) {
        this.serverView = serverView;
    }

    // Phương thức ghi log
    public void log(String message) {
        if (serverView != null) {
            serverView.logToTextArea(message);
        } else {
            System.out.println("ServerView chưa được thiết lập: " + message);
            LoggerHandler.getInstance().log("[ERROR] " + "ServerView chưa được thiết lập: " + message);
        }
    }
}
