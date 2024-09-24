package client.view.game_view;

import client.helper.AssetHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PlayFrame extends JFrame implements ActionListener,MouseListener{
	private MyPanel bg_panel;
	private MyButton play_button,select_button,diy_button,exit_button;
	private JPanel button_panel;

	//Cấu tạo cửa sổ trò chơi
	public PlayFrame()
	{
		super("Tìm điểm khác biệt");
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		super.setSize(1024, 680);
		super.setLocationRelativeTo(null);
		super.addMouseListener(this);
		super.getContentPane().setLayout(new BorderLayout());
	}

	//Khởi tạo menu chính
	public void initCover()
	{
		bg_panel=new MyPanel();

		button_panel=new JPanel(new FlowLayout());
		button_panel.setBackground(new Color(56, 152, 248));

		super.getContentPane().add(bg_panel,BorderLayout.CENTER);
		super.getContentPane().add(button_panel,BorderLayout.SOUTH);

		play_button=new MyButton("Bắt đầu ngẫu nhiên");
		play_button.addActionListener(this);

		select_button=new MyButton("Chọn màn chơi");
		select_button.addActionListener(this);

		diy_button=new MyButton("Tải màn chơi");
		diy_button.addActionListener(this);

		exit_button=new MyButton("Thoát trò chơi");
		exit_button.addActionListener(this);

		button_panel.add(play_button);
		button_panel.add(select_button);
		button_panel.add(diy_button);
		button_panel.add(exit_button);

		super.setSize(1024, 670);
		super.setVisible(true);
		super.setResizable(false);
	}

	public class MyPanel extends JPanel
	{
		//Vẽ bìa menu chính
		@Override
		public void paint(Graphics g)
		{
			Image bg=Toolkit.getDefaultToolkit().getImage(AssetHelper.IMAGE_BG);
			g.drawImage(bg, 0, 0, this);

			Image title=Toolkit.getDefaultToolkit().getImage(AssetHelper.IMAGE_TITLE);
			g.drawImage(title, 350, 80, this);

			Image pika=Toolkit.getDefaultToolkit().getImage(AssetHelper.IMAGE_PIKA);
			g.drawImage(pika, 180, 200, this);

			Image look=Toolkit.getDefaultToolkit().getImage(AssetHelper.IMAGE_LOOK);
			g.drawImage(look, 500, 200, this);

		}
	}



	public void actionPerformed(ActionEvent ae)
	{
		//Nguồn sự kiện là nút chơi ngẫu nhiên
		if(ae.getSource().equals(play_button))
		{
			MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);//Khởi tạo luồng phát âm thanh nhấp chuột
			mp.start(false);


			//Chọn ngẫu nhiên một màn trong các màn mặc định, sau đó cập nhật bảng điều khiển và bắt đầu trò chơi
			ArrayList<String> name1=new ArrayList<String>(),name2=new ArrayList<String>();
			ArrayList<double[]> pointXY=new ArrayList<double[]>();
			DIYdata diy_data=new DIYdata();
			diy_data.get(0, name1, name2, pointXY);

			int index=(int)(Math.random()*5);
			this.getContentPane().removeAll();
			((JPanel)(this.getContentPane())).updateUI();
			this.getContentPane().add(new PlayingPanel(this,name1.get(index),name2.get(index),pointXY.get(index)));
			this.getContentPane().repaint();
		}
		//Nguồn sự kiện là nút chọn màn chơi
		else if(ae.getSource().equals(select_button))
		{
			MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);//Khởi tạo luồng phát âm thanh nhấp chuột
			mp.start(false);

			//Cập nhật bảng điều khiển và vào giao diện chọn màn chơi
			super.getContentPane().removeAll();
			((JPanel)(super.getContentPane())).updateUI();
			super.getContentPane().add(new SelectPanel(this));
			super.getContentPane().repaint();
		}
		//Nguồn sự kiện là nút tải màn chơi
		else if(ae.getSource().equals(diy_button))
		{
			MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);//Khởi tạo luồng phát âm thanh nhấp chuột
			mp.start(false);

			new DIYFrame();//Tạo cửa sổ mới để tải màn chơi
		}
		//Nguồn sự kiện là nút thoát trò chơi
		else if(ae.getSource().equals(exit_button))
		{
			this.dispose();//Đóng cửa sổ
		}
	}

	//Phát âm thanh nhấp chuột khi nhấp vào menu chính
	public void mouseClicked(MouseEvent arg0) {
		MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);//Khởi tạo luồng phát âm thanh nhấp chuột
		mp.start(false);
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

//Tùy chỉnh kiểu nút bấm
class MyButton extends JButton
{
	MyButton(String text)
	{
		super.setBackground(Color.DARK_GRAY);
		super.setForeground(Color.ORANGE);
		super.setFont(new Font("微软雅黑",0,20)); // Giữ nguyên font chữ
		super.setText(text);
		super.setFocusPainted(false);
	}
}
