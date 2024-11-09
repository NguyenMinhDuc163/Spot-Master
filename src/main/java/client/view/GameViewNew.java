package client.view;

import client.ClientRun;
import client.helper.AssetHelper;
import client.view.game_view.DIYdata;
import client.view.game_view.MusicPlayer;
import client.view.game_view.PlayFrame;
import client.view.widget.ButtonWidget;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;


public class GameViewNew extends JPanel implements ActionListener,MouseListener{
    private PlayFrame frame;//Bảng điều khiển trò chơi hiển thị trên cửa sổ này
    private PlayFrame PF;//Dùng trong luồng đếm ngược
    private String name1,name2;
    double[] pointXY;//Tọa độ điểm khác nhau của ảnh, mỗi hai giá trị là một tọa độ
    private JPanel south_panel;
    CenterPanel cl_panel,cr_panel;
    private ButtonWidget home_button;
    private JProgressBar pbar;
    private Thread countdown;
    private int timeRemaining;
    private int foundDifferences = 0;  // Số điểm tìm được
    private int timeTaken = 0;
//    private GameClient gameClient;// Thời gian hoàn thành
    String competitor = "";
    ArrayList<String> listImage1 = new ArrayList<>();
    ArrayList<String> listImage2 = new ArrayList<>();
    ArrayList<double[]> pointImage = new ArrayList<>();
    DIYdata diy_data = new DIYdata();

    //Khởi tạo bảng điều khiển trò chơi
    public GameViewNew()
    {



        PlayFrame frame = new PlayFrame();  // Nếu bạn vẫn cần sử dụng PlayFrame
        frame.setSize(1024, 680);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Phát âm thanh khi bắt đầu trò chơi
        MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);
        mp.start(false);

        // Chọn ngẫu nhiên một màn chơi từ dữ liệu

        diy_data.get(0, listImage1, listImage2, pointImage);  // Lấy dữ liệu trò chơi

        int index = (int) (Math.random() * 5);  // Chọn ngẫu nhiên màn chơi

        // Xóa nội dung hiện tại của JFrame
        frame.getContentPane().removeAll();

        // Tạo và thêm GameViewNew
//        GameViewNew panel = new GameViewNew(frame, name1.get(index), name2.get(index), pointXY.get(index));

        /// TODO Cố dịnh 1 ảnh

//        System.out.println("name1: " + name1.get(2));
//        System.out.println("name2: " + name2.get(2));
//        System.out.println("pointXY: " + pointXY.get(2));





        PF=frame;
        this.frame=frame;
        this.name1=listImage1.get(2);
        this.name2=listImage2.get(2);
        this.pointXY=pointImage.get(2);
        this.addMouseListener(this);
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(56, 152, 248));

        JPanel center_panel=new JPanel();
        center_panel.setLayout(new GridLayout(1,2));
        this.add(center_panel,BorderLayout.CENTER);

        south_panel=new JPanel(new GridLayout(1,3));
        south_panel.setBorder(new EmptyBorder(20,0,20,0));
        south_panel.setBackground(new Color(56, 152, 248));

        cl_panel=new CenterPanel(listImage1.get(2),pointImage.get(2)); cl_panel.addMouseListener(this);
        cr_panel=new CenterPanel(listImage2.get(2),pointImage.get(2)); cr_panel.addMouseListener(this);
        center_panel.add(cl_panel);center_panel.add(cr_panel);

        JTextArea tips=new JTextArea();
        tips.setEditable(false);
        tips.setFont(new Font("Tahoma",0,20)); // Giữ nguyên font chữ
        tips.append("- Có năm điểm khác biệt \n- Hãy tìm ra chúng\n- Giới hạn thời gian 30 giây");
        tips.setOpaque(false);
        tips.setBorder(new EmptyBorder(0,10,0,10));
        south_panel.add(tips);

        pbar=new JProgressBar(0,30);
        pbar.setBackground(new Color(56, 152, 248));
        pbar.setBorder(new EmptyBorder(5,0,5,0));
        pbar.setValue(30);
        pbar.setForeground(new Color(150, 133, 236));
        south_panel.add(pbar);



        this.frame.getContentPane().add(this);

        // Cập nhật giao diện để hiển thị màn hình mới
        this.frame.revalidate();
        this.frame.repaint();

        // Hiển thị frame
//        this.frame.setVisible(true);


        countdown=new Thread(new Runnable(){

            public void run()
            {
                int count=30;
                PlayFrame play_frame=PF;
                while(count>=0)
                {
                    try{Thread.sleep(1000);}
                    catch(Exception e){}
                    pbar.setValue(count--);
                    String str= new String().format("Thời gian còn lại %d s", pbar.getValue());
                    pbar.setString(str);
                    pbar.setStringPainted(true);
                    timeRemaining = count + 1;
                }
                foundDifferences = cl_panel.getFoundDifferences();
                timeTaken = 30 - timeRemaining;
                System.out.println("------------------ found loss" +foundDifferences + " " +  timeTaken + " " + timeRemaining + " competitor " + competitor);
                //Hiển thị hộp thoại khi hết giờ, sau đó quay lại menu chính
                JOptionPane.showMessageDialog(play_frame, "Hết giờ rồi. Hãy thử lại xem","Hết giờ",JOptionPane.PLAIN_MESSAGE);
                ClientRun.socketHandler.submitNewResult(String.valueOf(foundDifferences), String.valueOf(timeTaken), competitor);
                /// TODO dung khi ket thuc

                play_frame.setVisible(false);

// Xóa toàn bộ nội dung hiện tại
                play_frame.getContentPane().removeAll();
                play_frame.getContentPane().revalidate();
                play_frame.getContentPane().repaint();


//                play_frame.getContentPane().removeAll();
//                ((JPanel)(play_frame.getContentPane())).updateUI();
//                play_frame.getContentPane().repaint();
//                play_frame.initCover();

            }
        });
        countdown.start();//Bắt đầu luồng đếm ngược
        // TODO Man hinh home
        home_button=new ButtonWidget("Menu chính");
        home_button.addActionListener(this);
        JPanel button_panel=new JPanel();
        button_panel.setOpaque(false);
        button_panel.add(home_button);
        south_panel.add(button_panel);

        this.add(south_panel,BorderLayout.SOUTH);

        ImageIcon icon=new ImageIcon(AssetHelper.IMAGE_TITLE);
        JLabel label=new JLabel(icon);
        label.setBorder(new EmptyBorder(30,0,30,0));
        this.add(label,BorderLayout.NORTH);

        frame.setSize(1024,640);
    }




    public void setInfoPlayer(String username) {
        // Đặt tên người chơi
        this.competitor = username;
        System.out.println("day la Competitor: " + competitor + " " + username);
        // Hiển thị tên người chơi trên giao diện (nếu cần)
//        JLabel infoPlayerLabel = new JLabel("Play game with: " + competitor);
//        infoPlayerLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
//        south_panel.add(infoPlayerLabel, BorderLayout.NORTH);
//        south_panel.revalidate(); // Cập nhật giao diện để hiển thị thông tin người chơi
//        south_panel.repaint();
    }





    public void actionPerformed(ActionEvent ae) {
        MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);//Khởi tạo luồng phát âm thanh nhấp chuột
        mp.start(false);

        //Nếu nhấp nút quay lại menu chính trong khi chơi, thì dừng luồng đếm ngược
        countdown.interrupt();
        this.frame.getContentPane().removeAll();
        ((JPanel)(this.frame.getContentPane())).updateUI();
        this.frame.getContentPane().repaint();
        this.frame.initCover();
    }

    public void mouseClicked(MouseEvent me) {

        MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);//Khởi tạo luồng phát âm thanh nhấp chuột
        mp.start(false);


        int x = me.getX();
        int y = me.getY();
//        gameClient.sendMessage(x + "," + y);
        ClientRun.socketHandler.sendLocation(x, y);
        //Truyền tọa độ khi nhấp và vẽ lại bảng điều khiển
        cl_panel.userXY[0]=(int)(me.getX());cl_panel.userXY[1]=(int)(me.getY());
        cr_panel.userXY[0]=(int)(me.getX());cr_panel.userXY[1]=(int)(me.getY());

        cl_panel.judge();
        cr_panel.judge();
        cl_panel.repaint();
        cr_panel.repaint();

        //Sau khi tìm được tất cả năm điểm khác biệt, hiển thị hộp thoại hoàn thành và quay lại menu chính
        if(cl_panel.user_correct.size()==10)
        {
            //Dừng luồng đếm ngược, hiển thị hộp thoại hoàn thành và quay lại menu chính
            countdown.interrupt();
            JOptionPane.showMessageDialog(frame, "Bạn thực sự là người kỳ cựu, đã tìm ra hết các điểm khác biệt","Hoàn thành",JOptionPane.PLAIN_MESSAGE,new ImageIcon("look.png"));

            foundDifferences = cl_panel.getFoundDifferences();
            timeTaken = 30 - timeRemaining;

            // Gửi dữ liệu đến server qua SocketHandler
            ClientRun.socketHandler.submitNewResult(String.valueOf(foundDifferences), String.valueOf(timeTaken), competitor);

            System.out.println("------------------ found win" +foundDifferences + " " +  timeTaken + " " + timeRemaining);

            /// TODO dung khi hoan thanh
            frame.setVisible(false);  // Ẩn `PlayFrame`

// Xóa nội dung, cập nhật giao diện
            frame.getContentPane().removeAll();
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();

//            frame.getContentPane().removeAll();
//            ((JPanel)(frame.getContentPane())).updateUI();
//
//            frame.getContentPane().repaint();
//            frame.initCover();
        }

    }
    public void showGameViewNew(boolean isShow, int index) {
        // Tạo một cửa sổ (JFrame)
//        PlayFrame frame = new PlayFrame();  // Nếu bạn vẫn cần sử dụng PlayFrame
//        frame.setSize(1024, 680);
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        // Phát âm thanh khi bắt đầu trò chơi
//        MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);
//        mp.start(false);
//
//        // Chọn ngẫu nhiên một màn chơi từ dữ liệu
//        ArrayList<String> name1 = new ArrayList<>();
//        ArrayList<String> name2 = new ArrayList<>();
//        ArrayList<double[]> pointXY = new ArrayList<>();
//        DIYdata diy_data = new DIYdata();
//        diy_data.get(0, name1, name2, pointXY);  // Lấy dữ liệu trò chơi
//
//        int index = (int) (Math.random() * 5);  // Chọn ngẫu nhiên màn chơi
//
//        // Xóa nội dung hiện tại của JFrame
//        frame.getContentPane().removeAll();
//
//        // Tạo và thêm GameViewNew
////        GameViewNew panel = new GameViewNew(frame, name1.get(index), name2.get(index), pointXY.get(index));
//
//        /// TODO Cố dịnh 1 ảnh
//
//        System.out.println("name1: " + name1.get(2));
//        System.out.println("name2: " + name2.get(2));
//        System.out.println("pointXY: " + pointXY.get(2));
//        GameViewNew panel = new GameViewNew(frame, name1.get(2), name2.get(2), pointXY.get(2));
//
//        frame.getContentPane().add(panel);
//
//        // Cập nhật giao diện để hiển thị màn hình mới
//        frame.revalidate();
//        frame.repaint();
//
//        // Hiển thị frame


        this.name1 = listImage1.get(index);
        this.name2 = listImage2.get(index);
        this.pointXY = pointImage.get(index);

        // Cập nhật lại cl_panel và cr_panel với dữ liệu mới
        cl_panel.updateData(this.name1, this.pointXY);
        cr_panel.updateData(this.name2, this.pointXY);

        // Làm mới lại giao diện
        this.revalidate();
        this.repaint();
        this.frame.setVisible(isShow);
    }
    //Thay đổi hình dạng con trỏ chuột thành mũi tên màu đỏ
    public void mouseEntered(MouseEvent arg0) {
        Image mouse_image=Toolkit.getDefaultToolkit().getImage(AssetHelper.IMAGE_RED_MOUSE);
        Image mouse=mouse_image.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
        Cursor cursor=Toolkit.getDefaultToolkit().createCustomCursor(mouse, new java.awt.Point(0,0), "mouse");
        this.setCursor(cursor);
    }

    public int getFoundDifferences() {
        return foundDifferences;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void updateOtherPlayerClick(int x, int y) {
        // Giả sử bạn muốn cập nhật cả hai panel cho các điểm người chơi khác đã nhấp
        cl_panel.userXY[0] = x;
        cl_panel.userXY[1] = y;
        cr_panel.userXY[0] = x;
        cr_panel.userXY[1] = y;

        // Kiểm tra và thêm điểm khác biệt nếu đúng
        cl_panel.judge();
        cr_panel.judge();

        // Vẽ lại giao diện để hiển thị điểm đã click
        cl_panel.repaint();
        cr_panel.repaint();
    }

    public void mouseExited(MouseEvent arg0) {}

    public void mousePressed(MouseEvent arg0) {}

    public void mouseReleased(MouseEvent arg0) {}
}

//Tùy chỉnh bảng điều khiển
class CenterPanel extends JPanel
{
    String name;
    Image imageCopy;
    double[] XY,userXY;
    ArrayList<Double> user_correct;

    //Phương thức khởi tạo, gán giá trị cho các biến
    CenterPanel(String name,double[] XY)
    {
        this.name=name;
        Image image;
        image=Toolkit.getDefaultToolkit().getImage("D:/DIYdata/"+name);

        File file=new File("D:/DIYdata/"+name);
        if(!file.exists()) image=Toolkit.getDefaultToolkit().getImage(AssetHelper.DEFAULT_DATA_PATH + name);
        imageCopy=image.getScaledInstance(512, 360, Image.SCALE_DEFAULT);
        this.XY=XY;
        userXY=new double[2]; userXY[0]=-256; userXY[1]=-256;
        user_correct=new ArrayList<Double>();

    }


    public void updateData(String newName, double[] newXY) {
        this.name = newName;
        this.XY = newXY;
        this.userXY = new double[2];  // Đặt lại vị trí người dùng nếu cần
        this.user_correct.clear();    // Xóa dữ liệu điểm đúng

        // Làm mới lại hình ảnh nếu cần
        Image image = Toolkit.getDefaultToolkit().getImage("D:/DIYdata/" + name);
        File file = new File("D:/DIYdata/" + name);
        if (!file.exists()) image = Toolkit.getDefaultToolkit().getImage(AssetHelper.DEFAULT_DATA_PATH + name);
        imageCopy = image.getScaledInstance(512, 360, Image.SCALE_DEFAULT);

        // Làm mới lại giao diện
        this.revalidate();
        this.repaint();
    }

    public void judge() {
        Point p0=new Point((int)userXY[0],(int)userXY[1]);//Tọa độ điểm nhấp của người chơi

        //Kiểm tra xem có đúng hay không
        boolean isCorrect=false;
        int i;
        for(i=0;i<XY.length;i+=2)
        {
            Point p=new Point((int)XY[i],(int)XY[i+1]);
            if(p.distance(p0)<=13)
            {
                isCorrect=true;
                break;
            }
        }

        //Kiểm tra xem đã tồn tại chưa (trước đó đã nhấp đúng)
        boolean exist=false;
        for(int j=0;j<user_correct.size();j+=2)
        {
            Point p=new Point((int)(double)(user_correct.get(j)),(int)(double)(user_correct.get(j+1)));
            if(p.distance(p0)<=13)
            {
                exist=true;
                break;
            }
        }

        //Nếu người chơi nhấp vào điểm khác mới, thì thêm tọa độ vào user_correct
        if(isCorrect&&exist==false)
        {
            user_correct.add(XY[i]);
            user_correct.add(XY[i+1]);
        }
    }
    public int getFoundDifferences() {
        return user_correct.size() / 2; // Mỗi 2 giá trị đại diện cho 1 điểm khác biệt
    }

    //Vẽ bảng điều khiển
    @Override
    public void paint(Graphics g)
    {
        if(name!=null)
        {
            Graphics2D g2d=(Graphics2D) g.create();

            g2d.drawImage(imageCopy,0, 0, this);//Vẽ hình ảnh

            //Bút vẽ chống răng cưa và thiết lập độ dày, màu sắc của bút
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(new BasicStroke(5));
            g2d.setColor(Color.RED);

            //Vẽ các điểm đúng đã được người chơi nhấp (dùng vẽ hình tròn để biểu diễn)
            for(int j=0;j<user_correct.size();j+=2)
                g2d.drawArc((int)(double)(user_correct.get(j)-12), (int)(double)(user_correct.get(j+1)-12), 30, 30, 0, 360);

            //Vẽ đường phân cách giữa hai hình ảnh
            g2d.setStroke(new BasicStroke(5));
            g2d.setColor(Color.WHITE);
            g2d.drawLine(this.getSize().width, 0, this.getSize().width, this.getSize().height);

            g2d.dispose();
        }

    }
}
