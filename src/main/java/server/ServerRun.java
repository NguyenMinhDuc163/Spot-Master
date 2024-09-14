package server;
import server.helper.LoggerHandler;
import server.service.Client;

import server.service.ClientManager;
import server.service.RoomManager;
import server.view.ServerView;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRun {

    public static volatile ClientManager clientManager;
    public static volatile RoomManager roomManager;
    public static boolean isShutDown = false;
    public static ServerSocket ss;

    public ServerRun() {

        try {
            int port = 2000;

            ss = new ServerSocket(port);
            System.out.println("Created Server at port " + port + ".");
            LoggerHandler.getInstance().info("Created Server at port " + port + ".");
            // init managers
            clientManager = new ClientManager();
            roomManager = new RoomManager();

            // create threadpool
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    10, // corePoolSize
                    100, // maximumPoolSize
                    10, // thread timeout
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(8) // queueCapacity
            );

            // server main loop - listen to client's connection
            while (!isShutDown) {
                try {
                    // socket object to receive incoming client requests
                    Socket s = ss.accept();
                     System.out.println("+ New Client connected: " + s);
                    LoggerHandler.getInstance().info("+ New Client connected: " + s);

                    // create new client runnable object
                    Client c = new Client(s);
                    clientManager.add(c);
                    System.out.println("Count of client online: " + clientManager.getSize());
                    LoggerHandler.getInstance().info("Count of client online: " + clientManager.getSize());

                    // execute client runnable
                    executor.execute(c);

                } catch (IOException ex) {
                    ex.printStackTrace();
                    LoggerHandler.getInstance().error(ex);
                    // Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    isShutDown = true;
                }
            }

            System.out.println("shutingdown executor...");
            LoggerHandler.getInstance().info("shutingdown executor...");

            executor.shutdownNow();

        } catch (IOException ex) {
            ex.printStackTrace();
            LoggerHandler.getInstance().error(ex);
        }
    }

    public static void main(String[] args) {
        // khi build khong cho chay giao dien
        if (!GraphicsEnvironment.isHeadless()) {
            ServerView serverView = new ServerView();
            serverView.setVisible(true);
            serverView.setLocationRelativeTo(null);
            LoggerHandler.getInstance().warn("Giao diện log đã khởi động. Đang chờ nhận log...\n");

        } else {
            LoggerHandler.getInstance().warn("Chạy trong chế độ headless, không khởi tạo giao diện đồ họa.");
            System.out.println("Chạy trong chế độ headless, không khởi tạo giao diện đồ họa.");
        }
        new ServerRun();
    }
}
