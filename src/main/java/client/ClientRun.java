package client;

import client.controller.SocketHandler;
import client.helper.AssetHelper;
import client.view.*;
import client.view.game_view.MusicPlayer;
import client.view.game_view.PlayFrame;
import client.view.game_view.SelectPanel;
import server.helper.LoggerHandler;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientRun {
    public enum SceneName {
        CONNECTSERVER,
        LOGIN,
        REGISTER,
        HOMEVIEW,
        INFOPLAYER,
        MESSAGEVIEW,
        GAMEVIEW,
        gameViewNew

    }

    // scenes
    public static ConnectServer connectServer;
    public static LoginView loginView;
    public static RegisterView registerView;
    public static HomeView homeView;
    public static GameView gameView;
    public static InfoPlayerView infoPlayerView;
    public static MessageView messageView;
    public static GameViewNew gameViewNew;

    // controller 
    public static SocketHandler socketHandler;

    public ClientRun() {
        socketHandler = new SocketHandler();
        initScene();
        openScene(SceneName.CONNECTSERVER);
    }

    public void initScene() {
        connectServer = new ConnectServer();
        loginView = new LoginView();
        registerView = new RegisterView();
        homeView = new HomeView();
        infoPlayerView = new InfoPlayerView();
        messageView = new MessageView();
        gameView = new GameView();
        gameViewNew = new GameViewNew(false, 2);
    }

    public static void openScene(SceneName sceneName) {
        if (null != sceneName) {
            switch (sceneName) {
                case CONNECTSERVER:
                    connectServer = new ConnectServer();
                    connectServer.setVisible(true);
                    break;
                case LOGIN:
                    loginView = new LoginView();
                    loginView.setVisible(true);
                    break;
                case REGISTER:
                    registerView = new RegisterView();
                    registerView.setVisible(true);
                    break;
                case HOMEVIEW:
                    homeView = new HomeView();
                    homeView.setVisible(true);
                    break;
                case INFOPLAYER:
                    infoPlayerView = new InfoPlayerView();
                    infoPlayerView.setVisible(true);
                    break;
                case MESSAGEVIEW:
                    messageView = new MessageView();
                    messageView.setVisible(true);
                    break;
                case GAMEVIEW:
                    gameViewNew = new GameViewNew(true, 3);
                    gameViewNew.showGameViewNew(true,"3");

//                    break;
                case gameViewNew:
                    System.out.println("da vao gameviewnew");

                    break;
                default:
                    break;
            }
        }
    }

    public static void closeScene(SceneName sceneName) {
        if (null != sceneName) {
            switch (sceneName) {
                case CONNECTSERVER:
                    connectServer.dispose();
                    break;
                case LOGIN:
                    loginView.dispose();
                    break;
                case REGISTER:
                    registerView.dispose();
                    break;
                case HOMEVIEW:
                    homeView.dispose();
                    break;
                case INFOPLAYER:
                    infoPlayerView.dispose();
                    break;
                case MESSAGEVIEW:
                    messageView.dispose();
                    break;
                case GAMEVIEW:
                    gameView.dispose();
                    break;
                case gameViewNew:
                    break;
                default:
                    break;
            }
        }
    }

    public static void closeAllScene() {
        if (!java.awt.GraphicsEnvironment.isHeadless()) {
            // khi build khong cho chay giao dien
            connectServer.dispose();
            loginView.dispose();
            registerView.dispose();
            homeView.dispose();
            infoPlayerView.dispose();
            messageView.dispose();
            gameView.dispose();
        } else {
            System.out.println("[WARNING]: Chạy trong chế độ headless, không khởi tạo giao diện đồ họa.");
        }


    }
    public static void updateScreen(int x, int y) {
        gameViewNew.updateOtherPlayerClick(x, y);

    }
    public static void main(String[] args) {
        new ClientRun();
    }
}
