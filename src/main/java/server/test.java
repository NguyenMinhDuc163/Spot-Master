//package server.service;
//
//import server.ServerRun;
//import server.controller.UserController;
//import server.helper.Question;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.sql.SQLException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class Client implements Runnable {
//    Socket s;
//    DataInputStream dis;
//    DataOutputStream dos;
//
//    String loginUser;
//    Client cCompetitor;
//
//    Room joinedRoom; // if == null => chua vao phong nao het
//
//    private final String dbType;  // Biến để lưu loại cơ sở dữ liệu
//
//    public Client(Socket s, String dbType) throws IOException {
//        this.s = s;
//        this.dbType = dbType;  // Nhận loại cơ sở dữ liệu từ tham số khi khởi tạo
//
//        // obtaining input and output streams
//        this.dis = new DataInputStream(s.getInputStream());
//        this.dos = new DataOutputStream(s.getOutputStream());
//    }
//
//    @Override
//    public void run() {
//
//        String received;
//        boolean running = true;
//
//        while (!ServerRun.isShutDown) {
//            try {
//                // receive the request from client
//                received = dis.readUTF();
//
//                System.out.println(received);
//                String type = received.split(";")[0];
//
//                switch (type) {
//                    case "LOGIN":
//                        onReceiveLogin(received);
//                        break;
//                    case "REGISTER":
//                        onReceiveRegister(received);
//                        break;
//                    case "GET_LIST_ONLINE":
//                        onReceiveGetListOnline();
//                        break;
//                    case "GET_INFO_USER":
//                        onReceiveGetInfoUser(received);
//                        break;
//                    case "LOGOUT":
//                        onReceiveLogout();
//                        break;
//                    case "CLOSE":
//                        onReceiveClose();
//                        break;
//                    // chat
//                    case "INVITE_TO_CHAT":
//                        onReceiveInviteToChat(received);
//                        break;
//                    case "ACCEPT_MESSAGE":
//                        onReceiveAcceptMessage(received);
//                        break;
//                    case "NOT_ACCEPT_MESSAGE":
//                        onReceiveNotAcceptMessage(received);
//                        break;
//                    case "LEAVE_TO_CHAT":
//                        onReceiveLeaveChat(received);
//                        break;
//                    case "CHAT_MESSAGE":
//                        onReceiveChatMessage(received);
//                        break;
//                    // play
//                    case "INVITE_TO_PLAY":
//                        onReceiveInviteToPlay(received);
//                        break;
//                    case "ACCEPT_PLAY":
//                        onReceiveAcceptPlay(received);
//                        break;
//                    case "NOT_ACCEPT_PLAY":
//                        onReceiveNotAcceptPlay(received);
//                        break;
//                    case "LEAVE_TO_GAME":
//                        onReceiveLeaveGame(received);
//                        break;
//                    case "CHECK_STATUS_USER":
//                        onReceiveCheckStatusUser(received);
//                        break;
//                    case "START_GAME":
//                        onReceiveStartGame(received);
//                        break;
//                    case "SUBMIT_RESULT":
//                        onReceiveSubmitResult(received);
//                        break;
//                    case "ASK_PLAY_AGAIN":
//                        onReceiveAskPlayAgain(received);
//                        break;
//
//                    case "EXIT":
//                        running = false;
//                }
//
//            } catch (IOException ex) {
//                ex.printStackTrace();
//                break;
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//        try {
//            // closing resources
//            this.s.close();
//            this.dis.close();
//            this.dos.close();
//            System.out.println("- Client disconnected: " + s);
//
//            // remove from clientManager
//            ServerRun.clientManager.remove(this);
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    // send data functions
//    public String sendData(String data) {
//        try {
//            this.dos.writeUTF(data);
//            return "success";
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Send data failed!");
//            return "failed;" + e.getMessage();
//        }
//    }
//
//    private void onReceiveLogin(String received) {
//        // get email / password from data
//        String[] splitted = received.split(";");
//        String username = splitted[1];
//        String password = splitted[2];
//
//        // check login
//        String result = new UserController(dbType).login(username, password);  // Truyền loại cơ sở dữ liệu khi khởi tạo
//
//        if (result != null && result.split(";")[0].equals("success")) {
//            // set login user
//            this.loginUser = username;
//        }
//
//        // send result
//        sendData("LOGIN" + ";" + result);
//        onReceiveGetListOnline();
//    }
//
//    private void onReceiveRegister(String received) {
//        // get email / password from data
//        String[] splitted = received.split(";");
//        String username = splitted[1];
//        String password = splitted[2];
//
//        // register
//        String result = new UserController(dbType).register(username, password);  // Truyền loại cơ sở dữ liệu khi khởi tạo
//
//        // send result
//        sendData("REGISTER" + ";" + result);
//    }
//
//    private void onReceiveGetListOnline() {
//        String result = ServerRun.clientManager.getListUseOnline();
//
//        // send result
//        String msg = "GET_LIST_ONLINE" + ";" + result;
//        ServerRun.clientManager.broadcast(msg);
//    }
//
//    private void onReceiveGetInfoUser(String received) {
//        String[] splitted = received.split(";");
//        String username = splitted[1];
//        // get info user
//        String result = new UserController(dbType).getInfoUser(username);  // Truyền loại cơ sở dữ liệu khi khởi tạo
//
//        String status = "";
//        Client c = ServerRun.clientManager.find(username);
//        if (c == null) {
//            status = "Offline";
//        } else {
//            if (c.getJoinedRoom() == null) {
//                status = "Online";
//            } else {
//                status = "In Game";
//            }
//        }
//
//        // send result
//        sendData("GET_INFO_USER" + ";" + result + ";" + status);
//    }
//
//    private void onReceiveLogout() {
//        this.loginUser = null;
//        // send result
//        sendData("LOGOUT" + ";" + "success");
//        onReceiveGetListOnline();
//    }
//
//    // Các phương thức còn lại không cần thay đổi gì
//}
