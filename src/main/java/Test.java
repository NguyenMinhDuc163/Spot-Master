import java.sql.*;
import java.util.ArrayList;

class DIYdata1 {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/spot_master_db"; // URL kết nối CSDL
    private static final String DB_USER = "root"; // Tên người dùng CSDL
    private static final String DB_PASSWORD = "NguyenDuc@163"; // Mật khẩu CSDL

    public void get(int key, ArrayList<String> name1, ArrayList<String> name2, ArrayList<double[]> XY) {
        // Kết nối với cơ sở dữ liệu
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Truy vấn lấy thông tin từ bảng images
            String sqlImages = "SELECT id, image_path_1, image_path_2 FROM images";
            try (PreparedStatement stmtImages = conn.prepareStatement(sqlImages);
                 ResultSet rsImages = stmtImages.executeQuery()) {

                while (rsImages.next()) {
                    int imageId = rsImages.getInt("id");
                    String image1 = rsImages.getString("image_path_1");
                    String image2 = rsImages.getString("image_path_2");

                    name1.add(image1);
                    name2.add(image2);

                    // Lấy các điểm khác biệt cho mỗi imageId từ bảng differences
                    String sqlDiff = "SELECT x_coordinate, y_coordinate FROM differences WHERE image_id = ?";
                    try (PreparedStatement stmtDiff = conn.prepareStatement(sqlDiff)) {
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
        DIYdata1 diyData = new DIYdata1();
        diyData.get(0, name1, name2, pointXY);  // Lấy dữ liệu từ cơ sở dữ liệu

        // In ra kết quả để kiểm tra dữ liệu
        System.out.println("Dữ liệu kiểm thử từ cơ sở dữ liệu:");

        for (int i = 0; i < name1.size(); i++) {
            System.out.println("Image 1: " + name1.get(i));
            System.out.println("Image 2: " + name2.get(i));
            System.out.print("Difference Points (x, y): ");
            double[] points = pointXY.get(i);
            for (int j = 0; j < points.length; j += 2) {
                System.out.print("(" + points[j] + ", " + points[j + 1] + ") ");
            }
            System.out.println("\n-----------------------");
        }
    }
}
