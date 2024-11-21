package client.controller;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import client.ClientRun;
import client.view.GameViewNew;
import server.helper.LoggerHandler;

public class SocketHandler {
     
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;

    String loginUser = null; // lưu tài khoản đăng nhập hiện tại
    String roomIdPresent = null; // lưu room hiện tại
    float score = 0;
    public static ArrayList<String> imageName = new ArrayList<>();
    Thread listener = null;

    public String connect(String addr, int port) {
        try {
            // getting ip 
            InetAddress ip = InetAddress.getByName(addr);

            // establish the connection with server port 
            s = new Socket();
            s.connect(new InetSocketAddress(ip, port), 4000);
            System.out.println("Connected to " + ip + ":" + port + ", localport:" + s.getLocalPort());
            // obtaining input and output streams
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // close old listener
            if (listener != null && listener.isAlive()) {
                listener.interrupt();
            }

            // listen to server
            listener = new Thread(this::listen);
            listener.start();

            // connect success
            return "success";

        } catch (IOException e) {
            e.printStackTrace();
            // connect failed
            return "failed;" + e.getMessage();
        }
    }

    private void listen() {
        boolean running = true;

        while (running) {
            try {
                // receive the data from server
                String received = dis.readUTF();

                System.out.println("RECEIVED: " + received);
                String type = received.split(";")[0];

                switch (type) {
                    case "LOGIN":
                        onReceiveLogin(received);
                        break;
                    case "REGISTER":
                        onReceiveRegister(received);
                        break;
                    case "GET_LIST_ONLINE":
                        onReceiveGetListOnline(received);
                        break;
                    case "LOGOUT":
                        onReceiveLogout(received);
                        break;
                    case "INVITE_TO_CHAT":
                        onReceiveInviteToChat(received);
                        break;
                    case "GET_INFO_USER":
                        onReceiveGetInfoUser(received);
                        break;
                    case "ACCEPT_MESSAGE":
                        onReceiveAcceptMessage(received);
                        break;
                    case "NOT_ACCEPT_MESSAGE":
                        onReceiveNotAcceptMessage(received);
                        break;
                    case "LEAVE_TO_CHAT":
                        onReceiveLeaveChat(received);
                        break;
                    case "CHAT_MESSAGE":
                        onReceiveChatMessage(received);
                        break;
                    case "INVITE_TO_PLAY":
                        onReceiveInviteToPlay(received);
                        break;
                    case "ACCEPT_PLAY":
                        onReceiveAcceptPlay(received);
                        break;
                    case "NOT_ACCEPT_PLAY":
                        onReceiveNotAcceptPlay(received);
                        break;
                    case "LEAVE_TO_GAME":
                        onReceiveLeaveGame(received);
                        break;
                    case "CHECK_STATUS_USER":
                        onReceiveCheckStatusUser(received);
                        break;
                    case "START_GAME":
                        onReceiveStartGame(received);
                        break; 
                    case "RESULT_GAME":
                        onReceiveResultGame(received);
                        break;
                    case "ASK_PLAY_AGAIN":
                        onReceiveAskPlayAgain(received);
                        break;
                    case "LOCATION":
                        onReceiveLocation(received);
                        break;
                    case "SENDPOINT":
                        onReceivePoint(received);
                        break;
                    case "ENDGAME":
                        onReceiveEndGame(received);
                        break;
                    case "LEADERBOARD":
                        onReceiveLeaderboard(received);
                        break;
                    case "SEND_IMAGE_START":
                        onReceiveImage();
                        break;
                    case "EXIT":
                        running = false;
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
                running = false;
            }
        }

        try {
            // closing resources
            s.close();
            dis.close();
            dos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        // alert if connect interup
        JOptionPane.showMessageDialog(null, "Mất kết nối tới server", "Lỗi", JOptionPane.ERROR_MESSAGE);
        ClientRun.closeAllScene();
        ClientRun.openScene(ClientRun.SceneName.CONNECTSERVER);
    }



    private void onReceiveImage() {
        try {
            String saveDirectory = "src/main/java/client/image/q/";
            File dir = new File(saveDirectory);
            if (!dir.exists()) dir.mkdirs();

            // Nhận số lượng ảnh
            int imageCount = dis.readInt();
            System.out.println("Số lượng ảnh cần nhận: " + imageCount);

            for (int i = 0; i < imageCount; i++) {
                // Nhận key và kiểm tra
                String partKey = dis.readUTF();
                if (!"IMAGE_PART".equals(partKey)) {
                    System.err.println("Lỗi: Key không khớp, mong đợi IMAGE_PART nhưng nhận được: " + partKey);
                    break;
                }

                // Nhận tên và kích thước ảnh
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();
                System.out.println("Nhận ảnh: " + fileName + " (" + fileSize + " bytes)");
                imageName.add(fileName);
                // Nhận nội dung ảnh
                try (FileOutputStream fos = new FileOutputStream(saveDirectory + fileName)) {
                    byte[] buffer = new byte[4096];
                    long totalBytesRead = 0;
                    int bytesRead;

                    while (totalBytesRead < fileSize && (bytesRead = dis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                    }

                    if (totalBytesRead != fileSize) {
                        System.err.println("Ảnh bị thiếu: " + fileName +
                                " (" + totalBytesRead + "/" + fileSize + ")");
                    } else {
                        System.out.println("Đã lưu ảnh: " + fileName);
                    }
                }

                // Gửi ACK
                dos.writeUTF("ACK");
            }
            System.out.println("da luu xong " + imageName);
            String level = Integer.toString(Integer.parseInt(imageName.get(0).replaceAll("\\D+", "").trim()));

            System.out.println("level: " + level);
            ClientRun.gameViewNew = new GameViewNew(true, level);
            ClientRun.gameViewNew.showGameViewNew(true,level, imageName.get(0), imageName.get(1));
            // Kiểm tra key kết thúc
            String endKey = dis.readUTF();
            if (!"END_IMAGE_TRANSMISSION".equals(endKey)) {
                System.err.println("Lỗi: Không nhận được thông báo kết thúc.");
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi nhận ảnh: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void viewLeaderboard(){
        sendData("LEADERBOARD;");
    }

    private void onReceiveEndGame(String received) {
        String []s = received.split(";");
        System.out.println("Da nhan duoc endgame " + received);
        ClientRun.gameViewNew.setLossGame(s[1]);
    }

    private void onReceivePoint(String received) {
        System.out.println("da nhan duoc point " + received);
    }

    private void onReceiveLocation(String received) {
        String[] splitted = received.split(";");
        ClientRun.updateScreen(Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2]));
    }

    /***
     * Handle from client
     */
    public void login(String email, String password) {
        // prepare data
        String data = "LOGIN" + ";" + email + ";" + password;
        // send data
        sendData(data);
    }
    
    public void register(String email, String password) {
        // prepare data
        String data = "REGISTER" + ";" + email + ";" + password;
        // send data
        sendData(data);
    }
    
    public void logout () {
        this.loginUser = null;
        sendData("LOGOUT");
    }
    
    public void close () {
        sendData("CLOSE");
    }
    
    public void getListOnline() {
        sendData("GET_LIST_ONLINE");
    }
    
    public void getInfoUser(String username) {
        sendData("GET_INFO_USER;" + username);
    }
    
    public void checkStatusUser(String username) {
        sendData("CHECK_STATUS_USER;" + username);
    }
    
    // Chat
    public void inviteToChat (String userInvited) {
        sendData("INVITE_TO_CHAT;" + loginUser + ";" + userInvited);
    }
    
    public void leaveChat (String userInvited) {
        sendData("LEAVE_TO_CHAT;" + loginUser + ";" + userInvited);
    }
    
    public void sendMessage (String userInvited, String message) {
        String chat = "[" + loginUser + "] : " + message + "\n";
        ClientRun.messageView.setContentChat(chat);
            
        sendData("CHAT_MESSAGE;" + loginUser + ";" + userInvited  + ";" + message);
    }
    
    // Play game
    public void inviteToPlay (String userInvited) {
        sendData("INVITE_TO_PLAY;" + loginUser + ";" + userInvited);
    }
    
    public void leaveGame (String userInvited) { 
        sendData("LEAVE_TO_GAME;" + loginUser + ";" + userInvited + ";" + roomIdPresent);
    }
    
    public void startGame (String userInvited) { 
        sendData("START_GAME;" + loginUser + ";" + userInvited + ";" + roomIdPresent);
    }

    public void submitNewResult(String score, String time, String competitor, String status, int opponentScore) {
        System.out.println("Submit new result score: " + score + " time: " + time + " competitor: " + competitor + " status: " + status);
        String ans = ClientRun.gameViewNew.getFoundDifferences() + ";" + ClientRun.gameViewNew.getTimeTaken();

        sendData("SUBMIT_RESULT;" + loginUser + ";" + competitor + ";" + roomIdPresent + ";" + ans + ";" + status + ";" + opponentScore);
        ClientRun.gameView.afterSubmit();
    }


    public void sendLocation(int x, int y) {
        String data = "LOCATION;" + x + ";" + y;
        sendData(data);
    }

    public void submitResult (String competitor) { 
        String result1 = ClientRun.gameView.getSelectedButton1();
        String result2 = ClientRun.gameView.getSelectedButton2();
        String result3 = ClientRun.gameView.getSelectedButton3();
        String result4 = ClientRun.gameView.getSelectedButton4();
        if (result1 == null || result2 == null || result3 == null || result4 == null) {
            ClientRun.gameView.showMessage("Don't leave blank!");
        } else {
            ClientRun.gameView.pauseTime();
            // Handle calculate time
            String[] splitted = ClientRun.gameView.pbgTimer.getString().split(":");
            String countDownTime = splitted[1];
            int time = 30 - Integer.parseInt(countDownTime);
            
            String data = ClientRun.gameView.getA1() + ";" + ClientRun.gameView.getB1() + ";" + result1 + ";"
                        + ClientRun.gameView.getA2() + ";" + ClientRun.gameView.getB2() + ";" + result2 + ";"
                        + ClientRun.gameView.getA3() + ";" + ClientRun.gameView.getB3() + ";" + result3 + ";"
                        + ClientRun.gameView.getA4() + ";" + ClientRun.gameView.getB4() + ";" + result4 + ";"
                        + time;
            
            sendData("SUBMIT_RESULT;" + loginUser + ";" + competitor + ";" + roomIdPresent + ";" + data);
            ClientRun.gameView.afterSubmit();
        }
    }
    
    public void acceptPlayAgain() {
        sendData("ASK_PLAY_AGAIN;YES;" + loginUser);
    }
    
    public void notAcceptPlayAgain() {
        sendData("ASK_PLAY_AGAIN;NO;" + loginUser);
    }
    
    /***
     * Handle send data to server
     */
    public void sendData(String data) {
        try {

            dos.writeUTF(data);
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(SocketHandler.class
                .getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /***
     * Handle receive data from server
     */
    private void onReceiveLeaderboard(String received) {
        ClientRun.openScene(ClientRun.SceneName.LEADERBOARDVIEW);
        ClientRun.leaderboardView.processLeaderboardData(received);
        ClientRun.homeView.setVisible(false);
    }
    private void onReceiveLogin(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {
            // hiển thị lỗi
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(ClientRun.loginView, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            // lưu user login
            this.loginUser = splitted[2];
            this.score = Float.parseFloat(splitted[3]) ;
            // chuyển scene
            ClientRun.closeScene(ClientRun.SceneName.LOGIN);
            ClientRun.openScene(ClientRun.SceneName.HOMEVIEW);

            // auto set info user
            ClientRun.homeView.setUsername(loginUser);
            ClientRun.homeView.setUserScore(score);
        }
    }
    
    private void onReceiveRegister(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {
            // hiển thị lỗi
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(ClientRun.registerView, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            JOptionPane.showMessageDialog(ClientRun.registerView, "Register account successfully! Please login!");
            // chuyển scene
            ClientRun.closeScene(ClientRun.SceneName.REGISTER);
            ClientRun.openScene(ClientRun.SceneName.LOGIN);
        }
    }
    
    private void onReceiveGetListOnline(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            int userCount = Integer.parseInt(splitted[2]);

            // https://niithanoi.edu.vn/huong-dan-thao-tac-voi-jtable-lap-trinh-java-swing.html
            Vector vheader = new Vector();
            vheader.add("User");

            Vector vdata = new Vector();
            if (userCount > 1) {
                for (int i = 3; i < userCount + 3; i++) {
                    String user = splitted[i];
                    if (!user.equals(loginUser) && !user.equals("null")) {
                        Vector vrow = new Vector();
                        vrow.add(user);
                        vdata.add(vrow);
                    }
                }

                ClientRun.homeView.setListUser(vdata, vheader);
            } else {
                ClientRun.homeView.resetTblUser();
            }
            
        } else {
            JOptionPane.showMessageDialog(ClientRun.loginView, "Have some error!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onReceiveGetInfoUser(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userName = splitted[2];
            String userScore =  splitted[3];
            String userWin =  splitted[4];
            String userDraw =  splitted[5];
            String userLose =  splitted[6];
            String userAvgCompetitor =  splitted[7];
            String userAvgTime =  splitted[8];
            String userStatus = splitted[9];
            
            ClientRun.openScene(ClientRun.SceneName.INFOPLAYER);
            ClientRun.infoPlayerView.setInfoUser(userName, userScore, userWin, userDraw, userLose, userAvgCompetitor, userAvgTime, userStatus);
        }
    }
    
    private void onReceiveLogout(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            ClientRun.closeScene(ClientRun.SceneName.HOMEVIEW);
            ClientRun.openScene(ClientRun.SceneName.LOGIN);
        }
    }
    
    // chat
    private void onReceiveInviteToChat(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            if (JOptionPane.showConfirmDialog(ClientRun.homeView, userHost + " want to chat with you?", "Chat?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
                ClientRun.openScene(ClientRun.SceneName.MESSAGEVIEW);
                ClientRun.messageView.setInfoUserChat(userHost);
                sendData("ACCEPT_MESSAGE;" + userHost + ";" + userInvited);
            } else {
                sendData("NOT_ACCEPT_MESSAGE;" + userHost + ";" + userInvited);
            }
        }
    }
    
    private void onReceiveAcceptMessage(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
                
            ClientRun.openScene(ClientRun.SceneName.MESSAGEVIEW);
            ClientRun.messageView.setInfoUserChat(userInvited);
        }
    }
    
    private void onReceiveNotAcceptMessage(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
                
            JOptionPane.showMessageDialog(ClientRun.homeView, userInvited + " don't want to chat with you!");
        }
    }
    
    private void onReceiveLeaveChat(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            
            ClientRun.closeScene(ClientRun.SceneName.MESSAGEVIEW);   
            JOptionPane.showMessageDialog(ClientRun.homeView, userHost + " leave to chat!");
        }
    }
    
    private void onReceiveChatMessage(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            String message = splitted[4];
            
            String chat = "[" + userHost + "] : " + message + "\n";
            ClientRun.messageView.setContentChat(chat);
        }
    }
    
    // play game
    private void onReceiveInviteToPlay(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            String roomId = splitted[4];
            if (JOptionPane.showConfirmDialog(ClientRun.homeView, userHost + " want to play game with you?", "Game?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
                ClientRun.openScene(ClientRun.SceneName.GAMEVIEW);

                // TODO set thong tin nguoi choi
                ClientRun.gameView.setInfoPlayer(userHost);
                roomIdPresent = roomId;
                ClientRun.gameView.setStateUserInvited();

                ClientRun.gameViewNew.setInfoPlayer(userHost);
                roomIdPresent = roomId;
                System.out.println("da set info player newGame + UserHost: " + userHost);
                sendData("ACCEPT_PLAY;" + userHost + ";" + userInvited + ";" + roomId);
            } else {
                sendData("NOT_ACCEPT_PLAY;" + userHost + ";" + userInvited + ";" + roomId);
            }
        }
    }
    
    private void onReceiveAcceptPlay(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];
        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            roomIdPresent = splitted[4];
            ClientRun.openScene(ClientRun.SceneName.GAMEVIEW);
            ClientRun.gameView.setInfoPlayer(userInvited);
            ClientRun.gameViewNew.setInfoPlayer(userInvited);
            System.out.println("Khi chap nhan  + UserInvited: " + userInvited);
            sendData("IMAGE");
            ClientRun.gameView.setStateHostRoom();
        }
    }
    
    private void onReceiveNotAcceptPlay(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
                
            JOptionPane.showMessageDialog(ClientRun.homeView, userInvited + " don't want to play with you!");
        }
    }
    
    private void onReceiveLeaveGame(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String user1 = splitted[2];
            String user2 = splitted[3];

            roomIdPresent = null;
            ClientRun.closeScene(ClientRun.SceneName.GAMEVIEW);   
            JOptionPane.showMessageDialog(ClientRun.homeView, user1 + " leave to game!");
        }
    }
     
    private void onReceiveCheckStatusUser(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[2];
        ClientRun.homeView.setStatusCompetitor(status);
    }
    
    private void onReceiveStartGame(String received) {
        // get status from data
        String[] splitted = received.split( ";");
        String data = splitted[4];
//        ClientRun.gameViewNew.showGameViewNew(true, data);
    }
    
    private void onReceiveResultGame(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];
        String result = splitted[2];
        String user1 = splitted[3];
        String user2 = splitted[4];
        String roomId = splitted[5];
        
        if (status.equals("success")) {
            ClientRun.gameView.setWaitingRoom();
            if (result.equals("DRAW")) {
                ClientRun.gameView.showAskPlayAgain("The game is draw. Do you want to play continue?");
            } else if (result.equals(loginUser)) {
                ClientRun.gameView.showAskPlayAgain("You win. Do you want to play continue?");
            } else {
                ClientRun.gameView.showAskPlayAgain("You lose. Do you want to play continue?");
            }
        }
    }
    
    private void onReceiveAskPlayAgain(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];
        
        if (status.equals("NO")) {
            ClientRun.closeScene(ClientRun.SceneName.GAMEVIEW);
            JOptionPane.showMessageDialog(ClientRun.homeView, "End Game!");
        } else {
            if (loginUser.equals(splitted[2])) {
                ClientRun.gameView.setStateHostRoom();
            } else {
                ClientRun.gameView.setStateUserInvited();
            }
        }
    }  
    
    
    // get set
    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public Socket getS() {
        return s;
    }

    public void setS(Socket s) {
        this.s = s;
    }

    public String getRoomIdPresent() {
        return roomIdPresent;
    }

    public void setRoomIdPresent(String roomIdPresent) {
        this.roomIdPresent = roomIdPresent;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public ArrayList<String> getImageName() {
        return imageName;
    }
}
