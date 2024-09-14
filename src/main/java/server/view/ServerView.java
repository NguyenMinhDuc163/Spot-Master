package server.view;

import server.helper.LoggerHandler;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ServerView extends javax.swing.JFrame {

    public ServerView() {
        initComponents();
        LoggerHandler.getInstance().setServerView(this);
    }

    // Phương thức để ghi log vào jTextPane1 với màu sắc cho từ khóa
    public void logToTextPane(String level, String message, SimpleAttributeSet levelStyle) {
        try {
            StyledDocument doc = jTextPane1.getStyledDocument();

            // Thêm phần level (INFO, WARNING, ERROR) với màu sắc
            doc.insertString(doc.getLength(), level, levelStyle);

            // Thêm phần tin nhắn chính với màu đen
            SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(defaultStyle, java.awt.Color.BLACK);
            doc.insertString(doc.getLength(), " " + message + "\n", defaultStyle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Phương thức để xóa log và ghi lại dòng "Server đã khởi động."
    private void clearLog() {
        try {
            // Xóa toàn bộ nội dung của jTextPane1
            jTextPane1.setText("");

            // Ghi lại dòng log "Server đã khởi động."
            LoggerHandler.getInstance().info("Server đã khởi động.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();  // Thay đổi JTextArea thành JTextPane
        jLabel1 = new javax.swing.JLabel();
        jButtonClearLog = new javax.swing.JButton(); // Thêm nút Clear Log

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextPane1.setEditable(false);
        jScrollPane1.setViewportView(jTextPane1);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Server Running...");

        jButtonClearLog.setText("Clear Log"); // Thiết lập nút Clear Log
        jButtonClearLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearLog(); // Gọi phương thức clearLog khi nút được nhấn
            }
        });

        // Cập nhật layout
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(39, 39, 39)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(201, 201, 201)
                                                .addComponent(jLabel1))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(200, 200, 200)
                                                .addComponent(jButtonClearLog))) // Thêm nút Clear Log vào layout
                                .addContainerGap(63, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonClearLog) // Thêm nút Clear Log vào cuối
                                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
            LoggerHandler.getInstance().error(ex);
            java.util.logging.Logger.getLogger(ServerView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ServerView serverView = new ServerView();
                serverView.setVisible(true);
                LoggerHandler.getInstance().info("Server đã khởi động.");
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1; // Thay đổi JTextArea thành JTextPane
    private javax.swing.JButton jButtonClearLog; // Khai báo nút Clear Log
    // End of variables declaration//GEN-END:variables
}
