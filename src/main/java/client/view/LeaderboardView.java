package client.view;

import client.ClientRun;
import client.controller.SocketHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public class LeaderboardView extends JFrame {
    private JTable tblLeaderboard;
    private JTextField txtSearch;
    private JComboBox<String> cmbSortBy;
    private JButton btnClose, btnViewDetails;
    private ArrayList<String> data;
    public LeaderboardView() {
        initComponents();
    }

    private void initComponents() {
        // Thiết lập cho JFrame
        setTitle("Player Leaderboard");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Tạo tiêu đề cho bảng xếp hạng
        JLabel lblTitle = new JLabel("Player Leaderboard", JLabel.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 26));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Thanh tìm kiếm và sắp xếp
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlTop.setBorder(new EmptyBorder(5, 10, 5, 10));

        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtSearch.addActionListener(e -> performSearch());

        cmbSortBy = new JComboBox<>(new String[]{"Điểm", "Thắng", "Số trận", "Tỉ lệ thắng"});
        cmbSortBy.setFont(new Font("Tahoma", Font.PLAIN, 14));
        cmbSortBy.addActionListener(e -> performSort());

        pnlTop.add(new JLabel("Search by Name:"));
        pnlTop.add(txtSearch);
        pnlTop.add(new JLabel("Sort by:"));
        pnlTop.add(cmbSortBy);

        // Tạo bảng với các cột Thứ hạng, Tên, Điểm, Thắng, Hòa, Thua
        tblLeaderboard = new JTable();
        tblLeaderboard.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Thứ hạng", "Tên", "Điểm", "Thắng", "Hòa", "Thua"}
        ));

        // Căn giữa các giá trị trong bảng
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tblLeaderboard.getColumnCount(); i++) {
            tblLeaderboard.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Trang trí cho bảng xếp hạng
        tblLeaderboard.setFillsViewportHeight(true);
        tblLeaderboard.setRowHeight(30);
        tblLeaderboard.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        tblLeaderboard.getTableHeader().setBackground(new Color(204, 229, 255));
        tblLeaderboard.setFont(new Font("Tahoma", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(tblLeaderboard);

        // Nút xem chi tiết và nút đóng


        btnClose = new JButton("Close");
        btnClose.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnClose.setBackground(new Color(204, 0, 0));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(evt -> {
            dispose();
            ClientRun.homeView.setVisible(true);
        });

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        pnlBottom.add(btnClose);

        // Thiết lập layout cho JFrame
        setLayout(new BorderLayout());
        add(lblTitle, BorderLayout.NORTH);
        add(pnlTop, BorderLayout.BEFORE_FIRST_LINE);
        add(scrollPane, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }


    // Phương thức để thiết lập dữ liệu cho bảng xếp hạng
    public void setLeaderboardData(Vector<Vector<Object>> data) {
        DefaultTableModel model = (DefaultTableModel) tblLeaderboard.getModel();
        model.setRowCount(0); // Xóa các hàng cũ
        for (Vector<Object> row : data) {
            model.addRow(row);
        }
    }

    // Phương thức xử lý dữ liệu bảng xếp hạng nhận từ server
    public void processLeaderboardData(String data) {
        Vector<Vector<Object>> leaderboardData = new Vector<>();
        String[] players = data.split(";");
        this.data = new ArrayList<>();
        // Bỏ qua phần đầu "LEADERBOARD"
        for (int i = 1; i < players.length; i++) {
            this.data.add(players[i]);
            String[] playerInfo = players[i].split(",");
            String userName = playerInfo[0];
            float score = Float.parseFloat(playerInfo[1]);
            int wins = Integer.parseInt(playerInfo[2]);
            int draws = Integer.parseInt(playerInfo[3]);
            int losses = Integer.parseInt(playerInfo[4]);

            // Thêm thông tin vào bảng
            Vector<Object> row = new Vector<>();
            row.add(i); // Thứ hạng
            row.add(userName);
            row.add(score);
            row.add(wins);
            row.add(draws);
            row.add(losses);
            leaderboardData.add(row);
        }

        // Cập nhật bảng xếp hạng
        setLeaderboardData(leaderboardData);
    }

    // Tim theo ten
    private void performSearch() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        Vector<Vector<Object>> filteredData = new Vector<>();

        for (String player : data) {
            String[] playerInfo = player.split(",");
            String userName = playerInfo[0].toLowerCase();


            if (userName.contains(searchText)) {
                Vector<Object> row = new Vector<>();
                row.add(filteredData.size() + 1); // Thứ hạng dựa trên thứ tự tìm kiếm
                row.add(playerInfo[0]); // Tên
                row.add(Float.parseFloat(playerInfo[1])); // Điểm
                row.add(Integer.parseInt(playerInfo[2])); // Thắng
                row.add(Integer.parseInt(playerInfo[3])); // Hòa
                row.add(Integer.parseInt(playerInfo[4])); // Thua
                filteredData.add(row);
            }
        }

        // Cập nhật bảng với dữ liệu đã lọc
        setLeaderboardData(filteredData);
    }



// sắp xếp
    private void performSort() {
        String sortBy = (String) cmbSortBy.getSelectedItem();

        // Sắp xếp `data` theo tiêu chí đã chọn
        data.sort((player1, player2) -> {
            String[] playerInfo1 = player1.split(",");
            String[] playerInfo2 = player2.split(",");

            int result;
            switch (sortBy) {
                case "Điểm":
                    result = Float.compare(Float.parseFloat(playerInfo2[1]), Float.parseFloat(playerInfo1[1]));
                    break;
                case "Thắng":
                    result = Integer.compare(Integer.parseInt(playerInfo2[2]), Integer.parseInt(playerInfo1[2]));
                    break;
                case "Số trận":
                    int totalMatches1 = Integer.parseInt(playerInfo1[2]) + Integer.parseInt(playerInfo1[3]) + Integer.parseInt(playerInfo1[4]);
                    int totalMatches2 = Integer.parseInt(playerInfo2[2]) + Integer.parseInt(playerInfo2[3]) + Integer.parseInt(playerInfo2[4]);
                    result = Integer.compare(totalMatches2, totalMatches1);
                    break;
                case "Tỉ lệ thắng":
                    int wins1 = Integer.parseInt(playerInfo1[2]);
                    int wins2 = Integer.parseInt(playerInfo2[2]);
                    int matches1 = Integer.parseInt(playerInfo1[2]) + Integer.parseInt(playerInfo1[3]) + Integer.parseInt(playerInfo1[4]);
                    int matches2 = Integer.parseInt(playerInfo2[2]) + Integer.parseInt(playerInfo2[3]) + Integer.parseInt(playerInfo2[4]);

                    float winRate1 = matches1 > 0 ? (float) wins1 / matches1 : 0;
                    float winRate2 = matches2 > 0 ? (float) wins2 / matches2 : 0;

                    result = Float.compare(winRate2, winRate1); // Sắp xếp giảm dần
                    break;
                default:
                    result = 0;
            }

            // Nếu các giá trị cần so sánh bằng nhau, sắp xếp theo tên (bảng chữ cái)
            if (result == 0) {
                result = playerInfo1[0].compareTo(playerInfo2[0]);
            }

            return result;
        });

        // Chuyển đổi dữ liệu đã sắp xếp thành định dạng bảng và cập nhật bảng
        Vector<Vector<Object>> sortedData = new Vector<>();
        for (int i = 0; i < data.size(); i++) {
            String[] playerInfo = data.get(i).split(",");
            Vector<Object> row = new Vector<>();
            row.add(i + 1); // Thứ hạng sau khi sắp xếp
            row.add(playerInfo[0]); // Tên
            row.add(Float.parseFloat(playerInfo[1])); // Điểm
            row.add(Integer.parseInt(playerInfo[2])); // Thắng
            row.add(Integer.parseInt(playerInfo[3])); // Hòa
            row.add(Integer.parseInt(playerInfo[4])); // Thua
            sortedData.add(row);
        }

        // Cập nhật bảng xếp hạng với dữ liệu đã sắp xếp
        setLeaderboardData(sortedData);
    }




    // Phương thức xem chi tiết người chơi
    private void viewPlayerDetails() {
        int selectedRow = tblLeaderboard.getSelectedRow();
        if (selectedRow != -1) {
            String username = (String) tblLeaderboard.getValueAt(selectedRow, 1);
            // Hiển thị cửa sổ thông tin chi tiết người chơi bằng `username`.
            JOptionPane.showMessageDialog(this,
                    "Detail for " + username + "\nScore: " + tblLeaderboard.getValueAt(selectedRow, 2) +
                            "\nWins: " + tblLeaderboard.getValueAt(selectedRow, 3) +
                            "\nDraws: " + tblLeaderboard.getValueAt(selectedRow, 4) +
                            "\nLosses: " + tblLeaderboard.getValueAt(selectedRow, 5),
                    "Player Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a player to view details.");
        }
    }

    public static void main(String[] args) {
        LeaderboardView leaderboardView = new LeaderboardView();
        SocketHandler socketHandler = new SocketHandler();
        socketHandler.connect("127.0.0.1", 2000);
        socketHandler.viewLeaderboard();

        // Dữ liệu giả để thử nghiệm
        String receivedData = "LEADERBOARD;anhtu,10.0,1,0,5;anhtu1,20.0,2,0,4";
        leaderboardView.processLeaderboardData(receivedData);
        leaderboardView.setVisible(true);
    }
}
