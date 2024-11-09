//package client.view.serverLess;
//
//import client.view.GameViewNew;
//
//import java.io.*;
//import java.net.*;
//
//public class GameClient {
//    private static final String SERVER_ADDRESS = "localhost"; // Địa chỉ server (thay đổi nếu cần)
//    private static final int PORT = 12345; // Cổng server
//    private Socket socket;
//    private BufferedReader in;
//    private PrintWriter out;
//    private GameViewNew gameView; // Tham chiếu đến GameViewNew để cập nhật giao diện
//
//    // Constructor khởi tạo GameClient và kết nối đến server
//    public GameClient(GameViewNew gameView) {
//        this.gameView = gameView;
//        try {
//            socket = new Socket(SERVER_ADDRESS, PORT);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            out = new PrintWriter(socket.getOutputStream(), true);
//
//            // Tạo luồng để lắng nghe thông điệp từ server
//            new Thread(new IncomingMessagesHandler()).start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Phương thức gửi thông điệp (tọa độ) đến server
//    public void sendMessage(String message) {
//        out.println(message);
//    }
//
//    // Lớp xử lý thông điệp nhận được từ server
//    private class IncomingMessagesHandler implements Runnable {
//        @Override
//        public void run() {
//            try {
//                String message;
//                while ((message = in.readLine()) != null) {
//                    // Phân tích thông điệp nhận được (tọa độ x, y)
//                    String[] parts = message.split(",");
//                    int x = Integer.parseInt(parts[0]);
//                    int y = Integer.parseInt(parts[1]);
//
//                    // Cập nhật giao diện game với tọa độ nhận được
//                    gameView.updateOtherPlayerClick(x, y);
//                    System.out.println("da goi toi gameview");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    // Phương thức đóng kết nối khi không sử dụng
//    public void closeConnection() {
//        try {
//            if (socket != null) socket.close();
//            if (in != null) in.close();
//            if (out != null) out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
