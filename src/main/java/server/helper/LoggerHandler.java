package server.helper;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import server.view.ServerView;

public class LoggerHandler {
    private static LoggerHandler instance;
    private ServerView serverView;

    private LoggerHandler() {}

    public static synchronized LoggerHandler getInstance() {
        if (instance == null) {
            instance = new LoggerHandler();
        }
        return instance;
    }

    public void setServerView(ServerView serverView) {
        this.serverView = serverView;
    }

    // Phương thức ghi log với màu sắc
    private void log(String level, String message, SimpleAttributeSet style) {
        if (serverView != null) {
            serverView.logToTextPane(level, message, style);
        } else {
            System.out.println(level + " " + message);
        }
    }

    public void info(String message) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, java.awt.Color.BLUE); // Màu xanh cho INFO
        log("[INFO]", message, style);
    }

    public void warn(String message) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, java.awt.Color.ORANGE); // Màu cam cho WARNING
        log("[WARNING]", message, style);
    }

    public void error( Exception ex) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, java.awt.Color.RED); // Màu đỏ cho ERROR
        log("[ERROR]",  ex.getMessage(), style);
        ex.printStackTrace();
    }
}
