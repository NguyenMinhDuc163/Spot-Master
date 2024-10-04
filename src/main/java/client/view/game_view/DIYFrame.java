package client.view.game_view;

import client.helper.AssetHelper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class DIYFrame extends JFrame implements ActionListener{
	private JButton button_left,button_right,goback,finish;
	private JPanel panel,panel_south;
	private static MyPanel panel_left,panel_right;
	private static JTextArea tips,message;
	private Image imageCopy;
	private String source1_path,source1_name,source2_path,source2_name;

	//Khởi tạo cửa sổ để người chơi nhập cấp độ
	DIYFrame()
	{
		super("Nhập hình ảnh");
		this.setSize(1024, 450);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new BorderLayout());

		panel=new JPanel(new GridLayout(1,2));
		this.getContentPane().add(panel,BorderLayout.CENTER);

		panel_left=new MyPanel(); panel_right=new MyPanel();


		button_left=new JButton("Chọn hình 1"); button_right=new JButton("Chọn hình 2");
		panel_left.add(button_left); panel_right.add(button_right);
		button_left.addActionListener(this); button_right.addActionListener(this);

		panel.add(panel_left);
		panel.add(panel_right);

		panel_south=new JPanel(new GridLayout(1,3));

		tips=new JTextArea();
		panel_south.add(tips);
		tips.setEditable(false);
		tips.setFont(new Font("Tahoma",0,18)); // Giữ nguyên font chữ
		tips.append("- Vui lòng nhập hai hình lần lượt\n- Tỉ lệ dài rộng của hình là khoảng 3:2\n- Trong hình có 5 điểm khác nhau");

		message=new JTextArea();
		message.setEditable(false);
		message.setFont(new Font("Tahoma",0,18)); // Giữ nguyên font chữ
		panel_south.add(message);


		goback=new JButton("Hoàn tác"); finish=new JButton("Hoàn thành");
		goback.addActionListener(this); finish.addActionListener(this);
		JPanel buttonPanel=new JPanel(); buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(goback); buttonPanel.add(finish);
		panel_south.add(buttonPanel);

		this.getContentPane().add(panel_south,BorderLayout.SOUTH);
		this.setResizable(false);
		this.validate();
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent ae) {
		//Nếu nguồn sự kiện là nút chọn hình ảnh
		if(ae.getSource().equals(button_left)||ae.getSource().equals(button_right))
		{
			JFileChooser chooser=new JFileChooser();
			FileNameExtensionFilter filter=new FileNameExtensionFilter("JPG & PNG Images","jpg","png");
			chooser.setFileFilter(filter);//Giới hạn định dạng tệp người chơi có thể chọn

			//Nếu người chơi không chọn tệp, quay lại
			if(chooser.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) return;

			//Lấy hình ảnh người chơi đã chọn
			Image image=Toolkit.getDefaultToolkit().createImage(chooser.getSelectedFile().getAbsolutePath());
			imageCopy=image.getScaledInstance(512, 360, Image.SCALE_DEFAULT);

			//Vẽ hình ảnh mà người chơi đã chọn
			if(ae.getSource().equals(button_left))
			{
				source1_path=chooser.getSelectedFile().getAbsolutePath();
				source1_name=chooser.getSelectedFile().getName();

				panel_left.remove(button_left);
				panel_left.image=imageCopy;
				panel_left.picsShowed=true;
				panel_left.repaint();
			}
			else if(ae.getSource().equals(button_right))
			{
				source2_path=chooser.getSelectedFile().getAbsolutePath();
				source2_name=chooser.getSelectedFile().getName();

				panel_right.remove(button_right);
				panel_right.image=imageCopy;
				panel_right.picsShowed=true;
				panel_right.repaint();
			}

			//Hiển thị thông báo cho người chơi bắt đầu nhấp vào điểm khác nhau
			if(panel_left.picsShowed&&panel_right.picsShowed)
			{
				message.setText("Bắt đầu nhấp vào điểm khác nhau...");
			}
		}
		//Nếu nguồn sự kiện là nút Hoàn tác
		else if(ae.getSource().equals(goback))
		{
			int num=MyPanel.pList.size();
			if(num>0)
			{
				MyPanel.pList.remove(num-1);//Xóa tọa độ điểm khác đã nhập trước đó

				panel_left.repaint();
				panel_right.repaint();
			}
		}
		//Nếu nguồn sự kiện là nút Hoàn thành
		else if(ae.getSource().equals(finish))
		{
			int num=MyPanel.pList.size();
			if(num!=5)//Nếu số điểm khác không phải là năm
			{
				JOptionPane.showMessageDialog(this, "Số điểm khác không phải là năm","Thông báo",JOptionPane.WARNING_MESSAGE);
				return;
			}

			//Nếu số điểm khác là năm, lưu hình ảnh và tọa độ điểm khác vào thư mục
			double[] pointXY=new double[10]; int count=0;
			for(Point p:MyPanel.pList)
			{
				pointXY[count++]=p.getX();
				pointXY[count++]=p.getY();
			}

			DIYdata cas=new DIYdata();
			try { cas.save(source1_path,source1_name,source2_path,source2_name,pointXY);}
			catch (Exception e) { e.printStackTrace(); }

			JOptionPane.showMessageDialog(this, "Nhập hình ảnh hoàn thành!\nĐã lưu vào thư mục DIYdata trong ổ D","Thông báo",JOptionPane.PLAIN_MESSAGE);
			this.dispose();
		}

	}

	//Tùy chỉnh bảng điều khiển
	public static class MyPanel extends JPanel implements MouseListener{
		private Image image;
		private static ArrayList<Point> pList=new ArrayList<Point>();//Lưu trữ tọa độ của điểm khác nhau
		public boolean picsShowed=false;//Đánh dấu hình ảnh đã được hiển thị

		MyPanel()
		{
			super();
			super.addMouseListener(this);
			this.image=null;
			pList=new ArrayList<Point>();
		}

		@Override
		public void paint(Graphics g)
		{
			Graphics2D g2d=(Graphics2D)g.create();


			g2d.drawImage(this.image, 0, 0, this);//Vẽ hình ảnh

			//Cài đặt chống răng cưa và thiết lập độ dày, màu sắc của bút
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(new BasicStroke(5));
			g2d.setColor(Color.RED);

			//Vẽ vòng tròn tại tọa độ điểm khác, bán kính là 12
			for(Point p:pList)
				g2d.drawArc((int)(p.getX()-12), (int)(p.getY()-12), 30, 30, 0, 360);

			//Vẽ đường phân cách giữa hai hình ảnh
			g2d.setStroke(new BasicStroke(5));
			g2d.setColor(Color.WHITE);
			g2d.drawLine(this.getSize().width, 0, this.getSize().width, this.getSize().height);

			g2d.dispose();
		}

		public void mouseClicked(MouseEvent e)
		{
			MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);//Khởi tạo một luồng phát âm thanh nhấp chuột
			mp.start(false);

			//Nếu cả hai hình ảnh đã hiển thị (người chơi bắt đầu nhấp vào điểm khác nhau), lưu lại tọa độ
			if(panel_left.picsShowed&&panel_right.picsShowed)
			{
				Point p=new Point(e.getX(),e.getY());
				pList.add(p);
			}

			//Vẽ lại tọa độ mới lưu
			if(panel_left.getComponentCount()==0) panel_left.repaint();
			if(panel_right.getComponentCount()==0) panel_right.repaint();
		}

		//Thay đổi hình dạng con trỏ chuột thành mũi tên màu đỏ
		public void mouseEntered(MouseEvent arg0)
		{
			Image mouse_image=Toolkit.getDefaultToolkit().getImage(AssetHelper.IMAGE_RED_MOUSE);
			Image mouse=mouse_image.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
			Cursor cursor=Toolkit.getDefaultToolkit().createCustomCursor(mouse, new java.awt.Point(0,0), "mouse");
			this.setCursor(cursor);
		}

		public void mouseExited(MouseEvent arg0) {}

		public void mousePressed(MouseEvent arg0) {}

		public void mouseReleased(MouseEvent arg0) {}
	}
}
