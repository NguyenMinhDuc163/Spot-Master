package server.controller;

import io.github.cdimascio.dotenv.Dotenv;
import server.connection.DatabaseConnectionFactory;
import server.connection.IDatabaseConnection;
import server.helper.LoggerHandler;
import server.model.UserModel;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserController {
    //  SQL queries
    private final String INSERT_USER = "INSERT INTO users (username, password, score, win, draw, lose, avgCompetitor, avgTime) VALUES (?, ?, 0, 0, 0, 0, 0, 0)";
    private final String CHECK_USER = "SELECT userId FROM users WHERE username = ? LIMIT 1";
    private final String LOGIN_USER = "SELECT username, password, score FROM users WHERE username=? AND password=?";
    private final String GET_INFO_USER = "SELECT username, password, score, win, draw, lose, avgCompetitor, avgTime FROM users WHERE username=?";
    private final String UPDATE_USER = "UPDATE users SET score = ?, win = ?, draw = ?, lose = ?, avgCompetitor = ?, avgTime = ? WHERE username=?";
    private final String LEADERBOARD = "SELECT * FROM users WHERE (win + draw + lose) > 0 ORDER BY score DESC";
    private final String dbType;  // Biến lưu loại cơ sở dữ liệu
    private static Dotenv dotenv = Dotenv.load();
    //  Connection instance
    private final Connection con;

    //  Constructor
    public UserController() {
        this.dbType = dotenv.get("DB_TYPE");
        // Sử dụng factory để chọn loại database kết nối
        IDatabaseConnection dbConnection = DatabaseConnectionFactory.getDatabaseConnection(dbType);
        this.con = dbConnection.getConnection();

    }

    public String register(String username, String password) {
        PreparedStatement p = null;
        ResultSet r = null;
        try {
            p = con.prepareStatement(CHECK_USER, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            p.setString(1, username);
            r = p.executeQuery();

            if (r.first()) {
                return "failed;User Already Exists";
            } else {
                r.close();
                p.close();

                p = con.prepareStatement(INSERT_USER);
                p.setString(1, username);
                p.setString(2, password);
                p.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LoggerHandler.getInstance().warn(String.valueOf(e));

            return "failed;" + e.getMessage();
        } finally {
            try {
                if (r != null) r.close();
                if (p != null) p.close();
            } catch (SQLException e) {
                LoggerHandler.getInstance().warn(String.valueOf(e));

                e.printStackTrace();
            }
        }
        return "success;";
    }

    public String login(String username, String password) {
        PreparedStatement p = null;
        ResultSet r = null;
        try {
            p = con.prepareStatement(LOGIN_USER, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            p.setString(1, username);
            p.setString(2, password);
            r = p.executeQuery();

            if (r.first()) {
                float score = r.getFloat("score");
                return "success;" + username + ";" + score;
            } else {
                return "failed;Please enter the correct account password!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LoggerHandler.getInstance().warn(String.valueOf(e));

            return "failed;" + e.getMessage();
        } finally {
            try {
                if (r != null) r.close();
                if (p != null) p.close();
            } catch (SQLException e) {
                e.printStackTrace();
                LoggerHandler.getInstance().warn(String.valueOf(e));

            }
        }
    }

    public String getInfoUser(String username) {
        UserModel user = new UserModel();
        try {
            PreparedStatement p = con.prepareStatement(GET_INFO_USER);
            p.setString(1, username);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                user.setUserName(r.getString("username"));
                user.setScore(r.getFloat("score"));
                user.setWin(r.getInt("win"));
                user.setDraw(r.getInt("draw"));
                user.setLose(r.getInt("lose"));
                user.setAvgCompetitor(r.getFloat("avgCompetitor"));
                user.setAvgTime(r.getFloat("avgTime"));
            }
            return "success;" + user.getUserName() + ";" + user.getScore() + ";" + user.getWin() + ";" + user.getDraw() + ";" + user.getLose() + ";" + user.getAvgCompetitor() + ";" + user.getAvgTime();
        } catch (SQLException e) {
            LoggerHandler.getInstance().warn(String.valueOf(e));

            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(UserModel user) throws SQLException {
        boolean rowUpdated;
        PreparedStatement p = con.prepareStatement(UPDATE_USER);
        p.setFloat(1, user.getScore());
        p.setInt(2, user.getWin());
        p.setInt(3, user.getDraw());
        p.setInt(4, user.getLose());
        p.setFloat(5, user.getAvgCompetitor());
        p.setFloat(6, user.getAvgTime());
        p.setString(7, user.getUserName());

        rowUpdated = p.executeUpdate() > 0;
        return rowUpdated;
    }

    public UserModel getUser(String username) {
        UserModel user = new UserModel();
        try {
            PreparedStatement p = con.prepareStatement(GET_INFO_USER);
            p.setString(1, username);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                user.setUserName(r.getString("username"));
                user.setScore(r.getFloat("score"));
                user.setWin(r.getInt("win"));
                user.setDraw(r.getInt("draw"));
                user.setLose(r.getInt("lose"));
                user.setAvgCompetitor(r.getFloat("avgCompetitor"));
                user.setAvgTime(r.getFloat("avgTime"));
            }
            return user;
        } catch (SQLException e) {
            LoggerHandler.getInstance().warn(String.valueOf(e));

            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<UserModel> getLeaderboard(){
        try {
            ArrayList<UserModel> userList = new ArrayList<>();
            PreparedStatement p = con.prepareStatement(LEADERBOARD);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                UserModel user = new UserModel();
                user.setUserName(r.getString("username"));
                user.setScore(r.getFloat("score"));
                user.setWin(r.getInt("win"));
                user.setDraw(r.getInt("draw"));
                user.setLose(r.getInt("lose"));
                userList.add(user);
            }
            return userList;
        } catch (SQLException e) {
            LoggerHandler.getInstance().warn(String.valueOf(e));

            e.printStackTrace();
        }
        return null;
    }

    public void getPointData(int key, ArrayList<String> name1, ArrayList<String> name2, ArrayList<double[]> XY){
        try {



            // Truy vấn lấy thông tin từ bảng images
            String sqlImages = "SELECT id, image_path_1, image_path_2 FROM images";
            try (PreparedStatement stmtImages = this.con .prepareStatement(sqlImages);
                 ResultSet rsImages = stmtImages.executeQuery()) {

                while (rsImages.next()) {
                    int imageId = rsImages.getInt("id");
                    String image1 = rsImages.getString("image_path_1");
                    String image2 = rsImages.getString("image_path_2");

                    name1.add(image1);
                    name2.add(image2);

                    // Lấy các điểm khác biệt cho mỗi imageId từ bảng differences
                    String sqlDiff = "SELECT x_coordinate, y_coordinate FROM differences WHERE image_id = ?";
                    try (PreparedStatement stmtDiff = this.con .prepareStatement(sqlDiff)) {
                        stmtDiff.setInt(1, imageId);
                        try (ResultSet rsDiff = stmtDiff.executeQuery()) {
                            double[] points = new double[10];
                            int index = 0;
                            while (rsDiff.next() && index < 10) {
                                points[index] = rsDiff.getDouble("x_coordinate");
                                points[index + 1] = rsDiff.getDouble("y_coordinate");
                                index += 2;
                            }
                            XY.add(points);
                        }
                    }
                }
            }
            System.out.println("Get data successfully: " + XY.size() + " images, " + name1.size() + " differences");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveGame(String username, int score, int timeLimit, String statusUser, String opponentName, int opponentScore) throws SQLException {
        System.out.println("->>>>>> timeLimit: " + timeLimit);

        // Tìm thông tin người chơi chính
        String findUserIdQuery = "SELECT userId, score, win, lose FROM users WHERE username = ?";
        PreparedStatement findUserIdStmt = this.con.prepareStatement(findUserIdQuery);
        findUserIdStmt.setString(1, username);
        ResultSet rs = findUserIdStmt.executeQuery();

        // Tìm thông tin đối thủ
        String findOpponentIdQuery = "SELECT userId, score, win, lose FROM users WHERE username = ?";
        PreparedStatement findOpponentIdStmt = this.con.prepareStatement(findOpponentIdQuery);
        findOpponentIdStmt.setString(1, opponentName);
        ResultSet opponentRs = findOpponentIdStmt.executeQuery();

        if (rs.next() && opponentRs.next()) {
            int userId = rs.getInt("userId");
            int userScore = rs.getInt("score");
            int userWins = rs.getInt("win");
            int userLosses = rs.getInt("lose");

            int opponentId = opponentRs.getInt("userId");
            int opponentScoreDb = opponentRs.getInt("score");
            int opponentWins = opponentRs.getInt("win");
            int opponentLosses = opponentRs.getInt("lose");

            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = startTime.plusMinutes(timeLimit);

            // Chèn vào bảng games cho người chơi chính
            String insertGameQuery = "INSERT INTO games (player_id, start_time, end_time, score, time_limit) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertGameStmt = this.con.prepareStatement(insertGameQuery, Statement.RETURN_GENERATED_KEYS);
            insertGameStmt.setInt(1, userId);
            insertGameStmt.setTimestamp(2, Timestamp.valueOf(startTime));
            insertGameStmt.setTimestamp(3, Timestamp.valueOf(endTime));
            insertGameStmt.setInt(4, score);
            insertGameStmt.setInt(5, timeLimit);
            insertGameStmt.executeUpdate();

            // Lấy game_id của bản ghi mới chèn cho người chơi chính
            ResultSet generatedKeys = insertGameStmt.getGeneratedKeys();
            int userGameId = -1;
            if (generatedKeys.next()) {
                userGameId = generatedKeys.getInt(1);
            }

            // Chèn vào bảng games cho đối thủ
            insertGameStmt.setInt(1, opponentId);
            insertGameStmt.setInt(4, opponentScore); // Sử dụng score của đối thủ
            insertGameStmt.executeUpdate();

            // Lấy game_id của bản ghi mới chèn cho đối thủ
            generatedKeys = insertGameStmt.getGeneratedKeys();
            int opponentGameId = -1;
            if (generatedKeys.next()) {
                opponentGameId = generatedKeys.getInt(1);
            }

            // Lấy difference_id đầu tiên từ bảng differences
            String findDifferenceIdQuery = "SELECT id FROM differences LIMIT 1";
            PreparedStatement findDifferenceIdStmt = this.con.prepareStatement(findDifferenceIdQuery);
            ResultSet diffRs = findDifferenceIdStmt.executeQuery();

            if (diffRs.next()) {
                int differenceId = diffRs.getInt("id");

                // Chèn bản ghi mới vào bảng game_differences cho người chơi chính
                String insertGameDifferenceQuery = "INSERT INTO game_differences (game_id, difference_id, found_time) VALUES (?, ?, ?)";
                PreparedStatement insertGameDifferenceStmt = this.con.prepareStatement(insertGameDifferenceQuery);
                insertGameDifferenceStmt.setInt(1, userGameId);
                insertGameDifferenceStmt.setInt(2, differenceId);
                insertGameDifferenceStmt.setTimestamp(3, Timestamp.valueOf(startTime));
                insertGameDifferenceStmt.executeUpdate();

                // Chèn bản ghi mới vào bảng game_differences cho đối thủ
                insertGameDifferenceStmt.setInt(1, opponentGameId);
                insertGameDifferenceStmt.executeUpdate();
                System.out.println("Dữ liệu game_differences đã được lưu thành công cho cả hai người chơi!");
            } else {
                System.out.println("Không tìm thấy difference_id trong bảng differences.");
            }

            // Xử lý cập nhật điểm dựa trên trạng thái trò chơi
            if (statusUser.equals("win")) {
                userScore += 10;
                userWins += 1;
                opponentLosses += 1;
            } else if (statusUser.equals("loss")) {
                opponentScoreDb += 10;
                opponentWins += 1;
                userLosses += 1;
            } else if (statusUser.equals("same")) {
                userScore += 5;
                opponentScoreDb += 5;
            }

            // Tính avgCompetitor cho cả người chơi và đối thủ
            int userGamesPlayed = userWins + userLosses;
            float userAvgCompetitor = (userGamesPlayed > 0) ? (float) userScore / userGamesPlayed : 0;

            int opponentGamesPlayed = opponentWins + opponentLosses;
            float opponentAvgCompetitor = (opponentGamesPlayed > 0) ? (float) opponentScoreDb / opponentGamesPlayed : 0;

            // Cập nhật thông tin người dùng
            String updateUserQuery = "UPDATE users SET score = ?, win = ?, lose = ?, avgCompetitor = ?, avgTime = ? WHERE userId = ?";
            PreparedStatement updateUserStmt = this.con.prepareStatement(updateUserQuery);
            updateUserStmt.setFloat(1, userScore);
            updateUserStmt.setInt(2, userWins);
            updateUserStmt.setInt(3, userLosses);
            updateUserStmt.setFloat(4, userAvgCompetitor);
            updateUserStmt.setFloat(5, timeLimit);
            updateUserStmt.setInt(6, userId);
            updateUserStmt.executeUpdate();

            // Cập nhật thông tin đối thủ
            updateUserStmt.setFloat(1, opponentScoreDb);
            updateUserStmt.setInt(2, opponentWins);
            updateUserStmt.setInt(3, opponentLosses);
            updateUserStmt.setFloat(4, opponentAvgCompetitor);
            updateUserStmt.setFloat(5, timeLimit);
            updateUserStmt.setInt(6, opponentId);
            updateUserStmt.executeUpdate();

            System.out.println("Cập nhật thông tin người chơi và đối thủ thành công!");

            // Đóng các tài nguyên
            generatedKeys.close();
            insertGameStmt.close();
            updateUserStmt.close();
            diffRs.close();
            findDifferenceIdStmt.close();
        } else {
            System.out.println("Không tìm thấy người dùng với username hoặc đối thủ: " + username + " / " + opponentName);
        }

        rs.close();
        opponentRs.close();
        findUserIdStmt.close();
        findOpponentIdStmt.close();
    }
    public static void main(String[] args) throws SQLException {
        // Khởi tạo các ArrayList để lưu dữ liệu
        ArrayList<String> name1 = new ArrayList<>();
        ArrayList<String> name2 = new ArrayList<>();
        ArrayList<double[]> pointXY = new ArrayList<>();

        // Tạo đối tượng DIYdata và gọi phương thức get
//        new UserController().getPointData(0, name1, name2, pointXY);

//        new UserController().saveGame("a", 0, 30);



//        // In ra kết quả để kiểm tra dữ liệu
//        System.out.println("Dữ liệu kiểm thử từ cơ sở dữ liệu:");
//
////        for (int i = 0; i < name1.size(); i++) {
////            System.out.println("Image 1: " + name1.get(i));
////            System.out.println("Image 2: " + name2.get(i));
////            System.out.print("Difference Points (x, y): ");
////            double[] points = pointXY.get(i);
////            for (int j = 0; j < points.length; j += 2) {
////                System.out.print("(" + points[j] + ", " + points[j + 1] + ") ");
////            }
////            System.out.println("\n-----------------------");
////        }
//
//        StringBuilder dataToSend = new StringBuilder();
////        for (int i = 0; i < name1.size(); i++) {
//            dataToSend.append(name1.get(2)).append(";")
//                    .append(name2.get(2)).append(";");
//            for (double coord : pointXY.get(2)) {
//                dataToSend.append(coord).append(";");
//            }
////        }
//        System.out.println(dataToSend);
    }
}
