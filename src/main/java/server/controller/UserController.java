package server.controller;

import io.github.cdimascio.dotenv.Dotenv;
import server.connection.DatabaseConnectionFactory;
import server.connection.IDatabaseConnection;
import server.helper.LoggerHandler;
import server.model.UserModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserController {
    //  SQL queries
    private final String INSERT_USER = "INSERT INTO users (username, password, score, win, draw, lose, avgCompetitor, avgTime) VALUES (?, ?, 0, 0, 0, 0, 0, 0)";
    private final String CHECK_USER = "SELECT userId FROM users WHERE username = ? LIMIT 1";
    private final String LOGIN_USER = "SELECT username, password, score FROM users WHERE username=? AND password=?";
    private final String GET_INFO_USER = "SELECT username, password, score, win, draw, lose, avgCompetitor, avgTime FROM users WHERE username=?";
    private final String UPDATE_USER = "UPDATE users SET score = ?, win = ?, draw = ?, lose = ?, avgCompetitor = ?, avgTime = ? WHERE username=?";
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

    public static void main(String[] args) {
        // Khởi tạo các ArrayList để lưu dữ liệu
        ArrayList<String> name1 = new ArrayList<>();
        ArrayList<String> name2 = new ArrayList<>();
        ArrayList<double[]> pointXY = new ArrayList<>();

        // Tạo đối tượng DIYdata và gọi phương thức get
        new UserController().getPointData(0, name1, name2, pointXY);

        // In ra kết quả để kiểm tra dữ liệu
        System.out.println("Dữ liệu kiểm thử từ cơ sở dữ liệu:");

//        for (int i = 0; i < name1.size(); i++) {
//            System.out.println("Image 1: " + name1.get(i));
//            System.out.println("Image 2: " + name2.get(i));
//            System.out.print("Difference Points (x, y): ");
//            double[] points = pointXY.get(i);
//            for (int j = 0; j < points.length; j += 2) {
//                System.out.print("(" + points[j] + ", " + points[j + 1] + ") ");
//            }
//            System.out.println("\n-----------------------");
//        }

        StringBuilder dataToSend = new StringBuilder();
//        for (int i = 0; i < name1.size(); i++) {
            dataToSend.append(name1.get(2)).append(";")
                    .append(name2.get(2)).append(";");
            for (double coord : pointXY.get(2)) {
                dataToSend.append(coord).append(";");
            }
//        }
        System.out.println(dataToSend);
    }
}
