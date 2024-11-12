/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.service;
import io.github.cdimascio.dotenv.Dotenv;
import server.ServerRun;
import server.controller.UserController;
import server.helper.LoggerHandler;
import server.helper.Question;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client implements Runnable {
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;

    String loginUser;
    Client cCompetitor;

    Room joinedRoom; // if == null => chua vao phong nao het

    private  String dbType;  // Biến để lưu loại cơ sở dữ liệu
    private static Dotenv dotenv = Dotenv.load();
    public Client(Socket s) throws IOException {
        this.s = s;
        this.dbType = dotenv.get("DB_TYPE");;  // Nhận loại cơ sở dữ liệu từ tham số khi khởi tạo

        // obtaining input and output streams
        this.dis = new DataInputStream(s.getInputStream());
        this.dos = new DataOutputStream(s.getOutputStream());
    }

    @Override
    public void run() {

        String received;
        boolean running = true;

        while (!ServerRun.isShutDown) {
            try {
                // receive the request from client
                received = dis.readUTF();

                System.out.println(received);
                LoggerHandler.getInstance().info(received);

                System.out.println("server da nhan duoc: " + received);
                String type = received.split(";")[0];

                switch (type) {
                    case "LOGIN":
                        onReceiveLogin(received);
                        break;
                    case "REGISTER":
                        onReceiveRegister(received);
                        break;
                    case "GET_LIST_ONLINE":
                        onReceiveGetListOnline();
                        break;
                    case "GET_INFO_USER":
                        onReceiveGetInfoUser(received);
                        break;
                    case "LOGOUT":
                        onReceiveLogout();
                        break;
                    case "CLOSE":
                        onReceiveClose();
                        break;
                    // chat
                    case "INVITE_TO_CHAT":
                        onReceiveInviteToChat(received);
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
                    // play
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
                    case "SUBMIT_RESULT":
                        onReceiveSubmitResult(received);
                        break;
                    case "ASK_PLAY_AGAIN":
                        onReceiveAskPlayAgain(received);
                        break;
                    case "LOCATION":
                        onReceiveLocation(received);
                        break;
                    case "SENDPOINT":
                        onSendPoint();
                        break;
                    case "EXIT":
                        running = false;
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                LoggerHandler.getInstance().error(ex);

                break;
            } catch (SQLException ex) {
                ex.printStackTrace();
                LoggerHandler.getInstance().error(ex);

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            // closing resources
            this.s.close();
            this.dis.close();
            this.dos.close();
            System.out.println("- Client disconnected: " + s);
            LoggerHandler.getInstance().info("- Client disconnected: " + s);



            // remove from clientManager
            ServerRun.clientManager.remove(this);

        } catch (IOException ex) {
            ex.printStackTrace();
            LoggerHandler.getInstance().error(ex);

            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void onSendPoint() {

        ArrayList<String> name1 = new ArrayList<>();
        ArrayList<String> name2 = new ArrayList<>();
        ArrayList<double[]> pointXY = new ArrayList<>();

        // Tạo đối tượng DIYdata và gọi phương thức get
        new UserController().getPointData(0, name1, name2, pointXY);

        // In ra kết quả để kiểm tra dữ liệu
        System.out.println("Dữ liệu kiểm thử từ cơ sở dữ liệu:");
        StringBuilder dataToSend = new StringBuilder();
//        for (int i = 0; i < name1.size(); i++) {
        dataToSend.append(name1.get(2)).append(";")
                .append(name2.get(2)).append(";");
        for (double coord : pointXY.get(2)) {
            dataToSend.append(coord).append(";");
        }
//        }
        ServerRun.clientManager.broadcast(dataToSend.toString());
        System.out.println("da gui dataToSend " + dataToSend);
    }

    private void onReceiveLocation(String received) {
        System.out.println("da vao location 2"  + received);

        ServerRun.clientManager.broadcast( received);
    }

    // send data functions
    public String sendData(String data) {
        try {
            this.dos.writeUTF(data);
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            LoggerHandler.getInstance().error(e);

            System.err.println("Send data failed!");
            LoggerHandler.getInstance().error(e);
            return "failed;" + e.getMessage();
        }
    }

    private void onReceiveLogin(String received) {
        // get email / password from data
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];

        // check login
        String result = new UserController().login(username, password);  // Truyền loại cơ sở dữ liệu khi khởi tạo

        if (result != null && result.split(";")[0].equals("success")) {
            // set login user
            this.loginUser = username;
        }

        // send result
        sendData("LOGIN" + ";" + result);
        onReceiveGetListOnline();
    }

    private void onReceiveRegister(String received) {
        // get email / password from data
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];

        // register
        String result = new UserController().register(username, password);  // Truyền loại cơ sở dữ liệu khi khởi tạo

        // send result
        sendData("REGISTER" + ";" + result);
    }

    private void onReceiveGetListOnline() {
        String result = ServerRun.clientManager.getListUseOnline();

        // send result
        String msg = "GET_LIST_ONLINE" + ";" + result;
        ServerRun.clientManager.broadcast(msg);
    }

    private void onReceiveGetInfoUser(String received) {
        String[] splitted = received.split(";");
        String username = splitted[1];
        // get info user
        String result = new UserController().getInfoUser(username);  // Truyền loại cơ sở dữ liệu khi khởi tạo

        String status = "";
        Client c = ServerRun.clientManager.find(username);
        if (c == null) {
            status = "Offline";
        } else {
            if (c.getJoinedRoom() == null) {
                status = "Online";
            } else {
                status = "In Game";
            }
        }

        // send result
        sendData("GET_INFO_USER" + ";" + result + ";" + status);
    }

    private void onReceiveLogout() {
        this.loginUser = null;
        // send result
        sendData("LOGOUT" + ";" + "success");
        onReceiveGetListOnline();
    }
    
    private void onReceiveInviteToChat(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        
        // send result
        String msg = "INVITE_TO_CHAT;" + "success;" + userHost + ";" + userInvited;
        ServerRun.clientManager.sendToAClient(userInvited, msg);
    }
    
    private void onReceiveAcceptMessage(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        
        // send result
        String msg = "ACCEPT_MESSAGE;" + "success;" + userHost + ";" + userInvited;
        ServerRun.clientManager.sendToAClient(userHost, msg);
    }      
      
    private void onReceiveNotAcceptMessage(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        
        // send result
        String msg = "NOT_ACCEPT_MESSAGE;" + "success;" + userHost + ";" + userInvited;
        ServerRun.clientManager.sendToAClient(userHost, msg);
    }      
    
    private void onReceiveLeaveChat(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        
        // send result
        String msg = "LEAVE_TO_CHAT;" + "success;" + userHost + ";" + userInvited;
        ServerRun.clientManager.sendToAClient(userInvited, msg);
    }      
    
    private void onReceiveChatMessage(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        String message = splitted[3];
        
        // send result
        String msg = "CHAT_MESSAGE;" + "success;" + userHost + ";" + userInvited + ";" + message;
        ServerRun.clientManager.sendToAClient(userInvited, msg);
    }    
    
    private void onReceiveInviteToPlay(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        
        // create new room
        joinedRoom = ServerRun.roomManager.createRoom();
        // add client
        Client c = ServerRun.clientManager.find(loginUser);
        joinedRoom.addClient(this);
        cCompetitor = ServerRun.clientManager.find(userInvited);
        
        // send result
        String msg = "INVITE_TO_PLAY;" + "success;" + userHost + ";" + userInvited + ";" + joinedRoom.getId();
        ServerRun.clientManager.sendToAClient(userInvited, msg);
    }
    
    private void onReceiveAcceptPlay(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        String roomId = splitted[3];
        
        Room room = ServerRun.roomManager.find(roomId);
        joinedRoom = room;
        joinedRoom.addClient(this);
        
        cCompetitor = ServerRun.clientManager.find(userHost);
        
        // send result
        String msg = "ACCEPT_PLAY;" + "success;" + userHost + ";" + userInvited + ";" + joinedRoom.getId() ;
        ServerRun.clientManager.sendToAClient(userHost, msg);
        onSendPoint();
        
    }      
      
    private void onReceiveNotAcceptPlay(String received) {
        String[] splitted = received.split(";");
        String userHost = splitted[1];
        String userInvited = splitted[2];
        String roomId = splitted[3];
        
        // userHost out room
        ServerRun.clientManager.find(userHost).setJoinedRoom(null);
        // Delete competitor of userhost
        ServerRun.clientManager.find(userHost).setcCompetitor(null);
        
        // delete room
        Room room = ServerRun.roomManager.find(roomId);
        ServerRun.roomManager.remove(room);
        
        // send result
        String msg = "NOT_ACCEPT_PLAY;" + "success;" + userHost + ";" + userInvited + ";" + room.getId();
        ServerRun.clientManager.sendToAClient(userHost, msg);
    }      
    
    private void onReceiveLeaveGame(String received) throws SQLException {
        String[] splitted = received.split(";");
        String user1 = splitted[1];
        String user2 = splitted[2];
        String roomId = splitted[3];
        
        joinedRoom.userLeaveGame(user1);
        
        this.cCompetitor = null;
        this.joinedRoom = null;
        
        // delete room
        Room room = ServerRun.roomManager.find(roomId);
        ServerRun.roomManager.remove(room);
        
        // userHost out room
        Client c = ServerRun.clientManager.find(user2);
        c.setJoinedRoom(null);
        // Delete competitor of userhost
        c.setcCompetitor(null);
        
        // send result
        String msg = "LEAVE_TO_GAME;" + "success;" + user1 + ";" + user2;
        ServerRun.clientManager.sendToAClient(user2, msg);
    }      
    
    private void onReceiveCheckStatusUser(String received) {
        String[] splitted = received.split(";");
        String username = splitted[1];
        
        String status = "";
        Client c = ServerRun.clientManager.find(username);
        if (c == null) {
            status = "OFFLINE";
        } else {
            if (c.getJoinedRoom() == null) {
                status = "ONLINE";
            } else {
                status = "INGAME";
            }
        }
        // send result
        sendData("CHECK_STATUS_USER" + ";" + username + ";" + status);
    }
            
    private void onReceiveStartGame(String received) {

        System.out.println("day là received room :" + received);
        String[] splitted = received.split(";");
        String user1 = splitted[1];
        String user2 = splitted[2];
        String roomId = splitted[3];

        String question1 = Question.renQuestion();
        String question2 = Question.renQuestion();
        String question3 = Question.renQuestion();
        String question4 = Question.renQuestion();
        
        String data = "START_GAME;success;" + roomId + ";" + question1 + question2 + question3 + question4;
        // Send question here
        joinedRoom.resetRoom();
        joinedRoom.broadcast(data);
        joinedRoom.startGame();
    } 
    
    private void onReceiveSubmitResult(String received) throws SQLException {
        System.out.println("da nhan duoc result: " + received);
        String []s = received.split(";");
        Arrays.stream(s).toList().forEach(System.out::println);
        new UserController().saveGame(s[1], Integer.parseInt(s[4]), Integer.parseInt(s[5]) );
        System.out.println("da save game");
//        String[] splitted = received.split(";");
//        String user1 = splitted[1];
//        String user2 = splitted[2];
//        String roomId = splitted[3];
//
//        if (user1.equals(joinedRoom.getClient1().getLoginUser())) {
//            joinedRoom.setResultClient1(received);
//        } else if (user1.equals(joinedRoom.getClient2().getLoginUser())) {
//            joinedRoom.setResultClient2(received);
//        }
//
//        while (!joinedRoom.getTime().equals("00:00") && joinedRoom.getTime() != null) {
//            System.out.println(joinedRoom.getTime());
//            LoggerHandler.getInstance().info(joinedRoom.getTime());
//
//
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//                LoggerHandler.getInstance().error(ex);
//
//                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//        String data = "RESULT_GAME;success;" + joinedRoom.handleResultClient()
//                + ";" + joinedRoom.getClient1().getLoginUser() + ";" + joinedRoom.getClient2().getLoginUser() + ";" + joinedRoom.getId();
//        System.out.println(data);
//        LoggerHandler.getInstance().info(data);
//
//
//        joinedRoom.broadcast(data);
    } 
    
    private void onReceiveAskPlayAgain(String received) throws SQLException {
        String[] splitted = received.split(";");
        String reply = splitted[1];
        String user1 = splitted[2];
        
        System.out.println("client1: " + joinedRoom.getClient1().getLoginUser());
        System.out.println("client2: " + joinedRoom.getClient2().getLoginUser());
        LoggerHandler.getInstance().info("client1: " + joinedRoom.getClient1().getLoginUser());

        LoggerHandler.getInstance().info("client2: " + joinedRoom.getClient2().getLoginUser());


        
        if (user1.equals(joinedRoom.getClient1().getLoginUser())) {
            joinedRoom.setPlayAgainC1(reply);
        } else if (user1.equals(joinedRoom.getClient2().getLoginUser())) {
            joinedRoom.setPlayAgainC2(reply);
        }
        
        while (!joinedRoom.getWaitingTime().equals("00:00")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                LoggerHandler.getInstance().error(ex);

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        
        String result = this.joinedRoom.handlePlayAgain();
        if (result.equals("YES")) {
            joinedRoom.broadcast("ASK_PLAY_AGAIN;YES;" + joinedRoom.getClient1().loginUser + ";" + joinedRoom.getClient2().loginUser);
        } else if (result.equals("NO")) {
            joinedRoom.broadcast("ASK_PLAY_AGAIN;NO;");
            
            Room room = ServerRun.roomManager.find(joinedRoom.getId());
            // delete room            
            ServerRun.roomManager.remove(room);
            this.joinedRoom = null;
            this.cCompetitor = null;
        } else if (result == null) {
            System.out.println("[ERROR] Đã xảy ra lỗi khi xử lý yêu cầu:");
            LoggerHandler.getInstance().error(new Exception());

        }
    }
        
    
    // Close app
    private void onReceiveClose() {
        this.loginUser = null;
        ServerRun.clientManager.remove(this);
        onReceiveGetListOnline();
    }
    
    // Get set
    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public Client getcCompetitor() {
        return cCompetitor;
    }

    public void setcCompetitor(Client cCompetitor) {
        this.cCompetitor = cCompetitor;
    }

    public Room getJoinedRoom() {
        return joinedRoom;
    }

    public void setJoinedRoom(Room joinedRoom) {
        this.joinedRoom = joinedRoom;
    }
    
    
}
