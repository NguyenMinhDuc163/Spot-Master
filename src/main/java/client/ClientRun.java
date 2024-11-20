package client;

import client.controller.SocketHandler;
import client.helper.AssetHelper;
import client.view.*;
import client.view.game_view.MusicPlayer;
import client.view.game_view.PlayFrame;
import client.view.game_view.SelectPanel;
import server.helper.LoggerHandler;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ClientRun {
    public enum SceneName {
        CONNECTSERVER,
        LOGIN,
        REGISTER,
        HOMEVIEW,
        INFOPLAYER,
        MESSAGEVIEW,
        GAMEVIEW,
        gameViewNew,
        LEADERBOARDVIEW

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
    public static LeaderboardView leaderboardView;

    // controller LeaderboardView
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
        gameViewNew = new GameViewNew(false, "1"); // intialize gameViewNew
        leaderboardView = new LeaderboardView();
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
//                case GAMEVIEW:
//                    gameViewNew = new GameViewNew(true, 3);
//                    gameViewNew.showGameViewNew(true,"3");

//                    break;
                case gameViewNew:
                    System.out.println("da vao gameviewnew");

                    break;
                case LEADERBOARDVIEW:
                    leaderboardView = new LeaderboardView();
                    leaderboardView.setVisible(true);
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
                case LEADERBOARDVIEW:
                    leaderboardView.dispose();
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
            leaderboardView.dispose();
        } else {
            System.out.println("[WARNING]: Chạy trong chế độ headless, không khởi tạo giao diện đồ họa.");
        }


    }
    public static void updateScreen(int x, int y) {
        gameViewNew.updateOtherPlayerClick(x, y);

    }

    public static boolean judge(double[] XY,double[] userXY, ArrayList<Double> user_correct) {
        Point p0 = new Point((int)userXY[0], (int)userXY[1]);

        boolean isCorrect = false;
        int i;
        for (i = 0; i < XY.length; i += 2) {
            Point p = new Point((int)XY[i], (int)XY[i + 1]);
            if (p.distance(p0) <= 13) {
                isCorrect = true;
                break;
            }
        }

        boolean exist = false;
        for (int j = 0; j < user_correct.size(); j += 2) {
            Point p = new Point((int)(double)(user_correct.get(j)), (int)(double)(user_correct.get(j + 1)));
            if (p.distance(p0) <= 13) {
                exist = true;
                break;
            }
        }

        if (isCorrect && !exist) {
            user_correct.add(XY[i]);
            user_correct.add(XY[i + 1]);
            return true;
        }
        return false;
    }
    public static void main(String[] args) {
        new ClientRun();
    }
}
