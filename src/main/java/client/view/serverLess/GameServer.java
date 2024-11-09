//package client.view.serverLess;
//
//import java.io.*;
//import java.net.*;
//import java.util.*;
//
//public class GameServer {
//    private static final int PORT = 12345;
//    private static List<Socket> clientSockets = new ArrayList<>();
//
//    public static void main(String[] args) {
//        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//            System.out.println("Server đang lắng nghe trên cổng " + PORT);
//
//            while (true) {
//                Socket clientSocket = serverSocket.accept();
//                synchronized (clientSockets) {
//                    clientSockets.add(clientSocket);
//                }
//                new Thread(new ClientHandler(clientSocket)).start();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static class ClientHandler implements Runnable {
//        private Socket socket;
//
//        public ClientHandler(Socket socket) {
//            this.socket = socket;
//        }
//
//        @Override
//        public void run() {
//            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
//
//                String message;
//                while ((message = in.readLine()) != null) {
//                    broadcastMessage(message);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                synchronized (clientSockets) {
//                    clientSockets.remove(socket);
//                }
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        private void broadcastMessage(String message) {
//            synchronized (clientSockets) {
//                for (Socket client : clientSockets) {
//                    try {
//                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
//                        out.println(message);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//}
