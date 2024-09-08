package server;

import server.dao.PlayerDAO;

import java.io.*;
import java.net.*;

public class Server {
    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private PlayerDAO playerDAO = new PlayerDAO();

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("LOGIN")) {
                        String[] credentials = inputLine.split(" ");
                        if (credentials.length == 3) {
                            boolean authenticated = playerDAO.authenticate(credentials[1], credentials[2]);
                            if (authenticated) {
                                out.println("SUCCESS");
                            } else {
                                out.println("FAILURE");
                            }
                        }
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start(6666);  // Server lắng nghe trên cổng 6666
    }
}
