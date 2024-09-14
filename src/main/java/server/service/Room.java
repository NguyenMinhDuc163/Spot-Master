package server.service;

import io.github.cdimascio.dotenv.Dotenv;
import server.ServerRun;
import server.controller.UserController;
import server.helper.CountDownTimer;
import server.helper.CustumDateTimeFormatter;
import server.helper.LoggerHandler;
import server.model.UserModel;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Room {
    String id;
    String time = "00:00";
    Client client1 = null, client2 = null;
    ArrayList<Client> clients = new ArrayList<>();
    boolean gameStarted = false;
    CountDownTimer matchTimer;
    CountDownTimer waitingTimer;

    String resultClient1;
    String resultClient2;
    String playAgainC1;
    String playAgainC2;
    String waitingTime = "00:00";

    public LocalDateTime startedTime;

    private final String dbType;  // Biến lưu loại cơ sở dữ liệu
    private static Dotenv dotenv = Dotenv.load();
    public Room(String id) {
        this.id = id;
        this.dbType = dotenv.get("DB_TYPE");  // Nhận loại cơ sở dữ liệu khi khởi tạo Room
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        gameStarted = true;

        matchTimer = new CountDownTimer(31);
        matchTimer.setTimerCallBack(
                null,
                (Callable) () -> {
                    time = "" + CustumDateTimeFormatter.secondsToMinutes(matchTimer.getCurrentTick());
                    System.out.println(time);
                    LoggerHandler.getInstance().info(time);

                    if (time.equals("00:00")) {
                        waitingClientTimer();
                        if (resultClient1 == null && resultClient2 == null) {
                            draw();
                            broadcast("RESULT_GAME;success;DRAW;" + client1.getLoginUser() + ";" + client2.getLoginUser() + ";" + id);
                        }
                    }
                    return null;
                },
                1
        );
    }

    public void waitingClientTimer() {
        waitingTimer = new CountDownTimer(12);
        waitingTimer.setTimerCallBack(
                null,
                (Callable) () -> {
                    waitingTime = "" + CustumDateTimeFormatter.secondsToMinutes(waitingTimer.getCurrentTick());
                    System.out.println("WARNING: " + waitingTime);
                    LoggerHandler.getInstance().warn(waitingTime);


                    if (waitingTime.equals("00:00")) {
                        if (playAgainC1 == null && playAgainC2 == null) {
                            broadcast("ASK_PLAY_AGAIN;NO");
                            deleteRoom();
                        }
                    }
                    return null;
                },
                1
        );
    }

    public void deleteRoom() {
        client1.setJoinedRoom(null);
        client1.setcCompetitor(null);
        client2.setJoinedRoom(null);
        client2.setcCompetitor(null);
        ServerRun.roomManager.remove(this);
    }

    public void resetRoom() {
        gameStarted = false;
        resultClient1 = null;
        resultClient2 = null;
        playAgainC1 = null;
        playAgainC2 = null;
        time = "00:00";
        waitingTime = "00:00";
    }

    public String handleResultClient() throws SQLException {
        int timeClient1 = 0;
        int timeClient2 = 0;

        if (resultClient1 != null) {
            String[] splitted1 = resultClient1.split(";");
            timeClient1 = Integer.parseInt(splitted1[16]);
        }
        if (resultClient2 != null) {
            String[] splitted2 = resultClient2.split(";");
            timeClient2 = Integer.parseInt(splitted2[16]);
        }

        if (resultClient1 == null & resultClient2 == null) {
            draw();
            return "DRAW";
        } else if (resultClient1 != null && resultClient2 == null) {
            if (calculateResult(resultClient1) > 0) {
                client1Win(timeClient1);
                return client1.getLoginUser();
            } else {
                draw();
                return "DRAW";
            }
        } else if (resultClient1 == null && resultClient2 != null) {
            if (calculateResult(resultClient2) > 0) {
                client2Win(timeClient2);
                return client2.getLoginUser();
            } else {
                draw();
                return "DRAW";
            }
        } else if (resultClient1 != null && resultClient2 != null) {
            int pointClient1 = calculateResult(resultClient1);
            int pointClient2 = calculateResult(resultClient2);

            if (pointClient1 > pointClient2) {
                client1Win(timeClient1);
                return client1.getLoginUser();
            } else if (pointClient1 < pointClient2) {
                client2Win(timeClient2);
                return client2.getLoginUser();
            } else {
                draw();
                return "DRAW";
            }
        }
        return null;
    }

    public int calculateResult(String received) {
        String[] splitted = received.split(";");

        String a1 = splitted[4];
        String b1 = splitted[5];
        String r1 = splitted[6];
        String a2 = splitted[7];
        String b2 = splitted[8];
        String r2 = splitted[9];
        String a3 = splitted[10];
        String b3 = splitted[11];
        String r3 = splitted[12];
        String a4 = splitted[13];
        String b4 = splitted[14];
        String r4 = splitted[15];

        int i = 0;
        int c1 = Integer.parseInt(a1) + Integer.parseInt(b1);
        int c2 = Integer.parseInt(a2) + Integer.parseInt(b2);
        int c3 = Integer.parseInt(a3) + Integer.parseInt(b3);
        int c4 = Integer.parseInt(a4) + Integer.parseInt(b4);

        if (c1 == Integer.parseInt(r1)) {
            i++;
        }
        if (c2 == Integer.parseInt(r2)) {
            i++;
        }
        if (c3 == Integer.parseInt(r3)) {
            i++;
        }
        if (c4 == Integer.parseInt(r4)) {
            i++;
        }

        return i;
    }

    public void draw() throws SQLException {
        UserModel user1 = new UserController(dbType).getUser(client1.getLoginUser());  // Sử dụng dbType
        UserModel user2 = new UserController(dbType).getUser(client2.getLoginUser());  // Sử dụng dbType

        user1.setDraw(user1.getDraw() + 1);
        user2.setDraw(user2.getDraw() + 1);

        user1.setScore(user1.getScore() + 0.5f);
        user2.setScore(user2.getScore() + 0.5f);

        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();

        float newAvgCompetitor1 = (totalMatchUser1 * user1.getAvgCompetitor() + user2.getScore()) / (totalMatchUser1 + 1);
        float newAvgCompetitor2 = (totalMatchUser2 * user2.getAvgCompetitor() + user1.getScore()) / (totalMatchUser2 + 1);

        user1.setAvgCompetitor(newAvgCompetitor1);
        user2.setAvgCompetitor(newAvgCompetitor2);

        new UserController(dbType).updateUser(user1);  // Sử dụng dbType
        new UserController(dbType).updateUser(user2);  // Sử dụng dbType
    }

    public void client1Win(int time) throws SQLException {
        UserModel user1 = new UserController(dbType).getUser(client1.getLoginUser());  // Sử dụng dbType
        UserModel user2 = new UserController(dbType).getUser(client2.getLoginUser());  // Sử dụng dbType

        user1.setWin(user1.getWin() + 1);
        user2.setLose(user2.getLose() + 1);

        user1.setScore(user1.getScore() + 1);

        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();

        float newAvgCompetitor1 = (totalMatchUser1 * user1.getAvgCompetitor() + user2.getScore()) / (totalMatchUser1 + 1);
        float newAvgCompetitor2 = (totalMatchUser2 * user1.getAvgCompetitor() + user1.getScore()) / (totalMatchUser2 + 1);

        user1.setAvgCompetitor(newAvgCompetitor1);
        user2.setAvgCompetitor(newAvgCompetitor2);

        float newAvgTime1 = (totalMatchUser1 * user1.getAvgTime() + time) / (totalMatchUser1 + 1);
        user1.setAvgTime(newAvgTime1);

        new UserController(dbType).updateUser(user1);  // Sử dụng dbType
        new UserController(dbType).updateUser(user2);  // Sử dụng dbType
    }

    public void client2Win(int time) throws SQLException {
        UserModel user1 = new UserController(dbType).getUser(client1.getLoginUser());  // Sử dụng dbType
        UserModel user2 = new UserController(dbType).getUser(client2.getLoginUser());  // Sử dụng dbType

        user2.setWin(user2.getWin() + 1);
        user1.setLose(user1.getLose() + 1);

        user2.setScore(user2.getScore() + 1);

        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();

        float newAvgCompetitor1 = (totalMatchUser1 * user1.getAvgCompetitor() + user2.getScore()) / (totalMatchUser1 + 1);
        float newAvgCompetitor2 = (totalMatchUser2 * user1.getAvgCompetitor() + user1.getScore()) / (totalMatchUser2 + 1);

        user1.setAvgCompetitor(newAvgCompetitor1);
        user2.setAvgCompetitor(newAvgCompetitor2);

        float newAvgTime2 = (totalMatchUser2 * user2.getAvgTime() + time) / (totalMatchUser2 + 1);
        user2.setAvgTime(newAvgTime2);

        new UserController(dbType).updateUser(user1);  // Sử dụng dbType
        new UserController(dbType).updateUser(user2);  // Sử dụng dbType
    }

    public void userLeaveGame(String username) throws SQLException {
        if (client1.getLoginUser().equals(username)) {
            client2Win(0);
        } else if (client2.getLoginUser().equals(username)) {
            client1Win(0);
        }
    }

    public String handlePlayAgain() {
        if (playAgainC1 == null || playAgainC2 == null) {
            return "NO";
        } else if (playAgainC1.equals("YES") && playAgainC2.equals("YES")) {
            return "YES";
        } else if (playAgainC1.equals("NO") || playAgainC2.equals("NO")) {
            return "NO";
        }
        return "NO";
    }

    // add/remove client
    public boolean addClient(Client c) {
        if (!clients.contains(c)) {
            clients.add(c);
            if (client1 == null) {
                client1 = c;
            } else if (client2 == null) {
                client2 = c;
            }
            return true;
        }
        return false;
    }

    public boolean removeClient(Client c) {
        if (clients.contains(c)) {
            clients.remove(c);
            return true;
        }
        return false;
    }

    // broadcast messages
    public void broadcast(String msg) {
        clients.forEach((c) -> c.sendData(msg));
    }

    public Client find(String username) {
        for (Client c : clients) {
            if (c.getLoginUser() != null && c.getLoginUser().equals(username)) {
                return c;
            }
        }
        return null;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient1() {
        return client1;
    }

    public void setClient1(Client client1) {
        this.client1 = client1;
    }

    public Client getClient2() {
        return client2;
    }

    public void setClient2(Client client2) {
        this.client2 = client2;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public int getSizeClient() {
        return clients.size();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResultClient1() {
        return resultClient1;
    }

    public void setResultClient1(String resultClient1) {
        this.resultClient1 = resultClient1;
    }

    public String getResultClient2() {
        return resultClient2;
    }

    public void setResultClient2(String resultClient2) {
        this.resultClient2 = resultClient2;
    }

    public String getPlayAgainC1() {
        return playAgainC1;
    }

    public void setPlayAgainC1(String playAgainC1) {
        this.playAgainC1 = playAgainC1;
    }

    public String getPlayAgainC2() {
        return playAgainC2;
    }

    public void setPlayAgainC2(String playAgainC2) {
        this.playAgainC2 = playAgainC2;
    }

    public String getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(String waitingTime) {
        this.waitingTime = waitingTime;
    }
}
