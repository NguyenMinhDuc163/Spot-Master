package client.view.game_view;

import client.helper.AssetHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SelectPanel extends JPanel implements ActionListener{
	private PlayFrame frame;
	private MyButton pre_button,enter_button,next_button,goback_button;
	private MyPanel bg_panel;
	private JPanel button_panel;
	ArrayList<String> name1,name2,nameA,nameB;
	ArrayList<double[]> XY,DXY;

	//Khởi tạo bảng điều khiển chọn cấp độ
	SelectPanel(PlayFrame frame)
	{
		super(new BorderLayout());
		this.frame=frame;

		//Lấy các cấp độ mà người chơi tự nhập và các cấp độ mặc định
		DIYdata diy_data=new DIYdata();
		name1=new ArrayList<String>();
		name2=new ArrayList<String>();
		XY=new ArrayList<double[]>();
		diy_data.get(1,name1, name2, XY);
		nameA=new ArrayList<String>();
		nameB=new ArrayList<String>();
		DXY=new ArrayList<double[]>();
		diy_data.get(0, nameA, nameB, DXY);

		bg_panel=new MyPanel(name1,nameA);
		super.add(bg_panel,BorderLayout.CENTER);

		pre_button=new MyButton("Cấp độ trước đó"); pre_button.addActionListener(this);
		enter_button=new MyButton("Vào trò chơi"); enter_button.addActionListener(this);
		next_button=new MyButton("Cấp độ tiếp theo"); next_button.addActionListener(this);
		goback_button=new MyButton("Trở lại"); goback_button.addActionListener(this);

		button_panel=new JPanel();
		button_panel.setBackground(new Color(167,133,95));
		button_panel.add(pre_button);
		button_panel.add(enter_button);
		button_panel.add(next_button);
		button_panel.add(goback_button);

		super.add(button_panel,BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent ae) {
		//Sự kiện nguồn là nút Cấp độ trước đó
		if(ae.getSource().equals(pre_button))
		{
			MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);//Khởi tạo một luồng phát âm thanh nhấp chuột
			mp.start(false);

			bg_panel.order--;//Giảm số thứ tự cấp độ
			//Đảm bảo số thứ tự cấp độ nằm trong phạm vi hợp lệ
			if(bg_panel.order<0)
			{
				bg_panel.order=(name1.size()+nameA.size())-1;
				bg_panel.repaint();
			}
			else
			{
				bg_panel.repaint();
			}
		}
		//Sự kiện nguồn là nút Bắt đầu trò chơi
		else if(ae.getSource().equals(enter_button))
		{
			MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);//Khởi tạo một luồng phát âm thanh nhấp chuột
			mp.start(false);

			//Cập nhật bảng điều khiển chọn cấp độ, vào cấp độ mà người chơi chọn và bắt đầu trò chơi
			frame.getContentPane().removeAll();
			((JPanel)(frame.getContentPane())).updateUI();
			int index=bg_panel.order;
			if(index<=(name1.size()-1))
				frame.getContentPane().add(new PlayingPanel(frame,name1.get(index),name2.get(index),XY.get(index)));
			else
			{
				index-=(name1.size());
				frame.getContentPane().add(new PlayingPanel(frame,nameA.get(index),nameB.get(index),DXY.get(index)));
			}
			frame.getContentPane().repaint();
		}
		//Sự kiện nguồn là nút Cấp độ tiếp theo
		else if(ae.getSource().equals(next_button))
		{
			MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);//Khởi tạo một luồng phát âm thanh nhấp chuột
			mp.start(false);

			//Tăng số thứ tự cấp độ
			bg_panel.order++;
			//Đảm bảo số thứ tự cấp độ nằm trong phạm vi hợp lệ
			if(bg_panel.order>(name1.size()+nameA.size()-1))
			{
				bg_panel.order=0;
				bg_panel.repaint();
			}
			else
			{
				bg_panel.repaint();
			}
		}
		//Sự kiện nguồn là nút Quay lại menu chính
		else if(ae.getSource().equals(goback_button))
		{
			MusicPlayer mp = new MusicPlayer(AssetHelper.MUSIC_CLICK);//Khởi tạo một luồng phát âm thanh nhấp chuột
			mp.start(false);

			//Cập nhật bảng điều khiển, quay lại bảng điều khiển menu chính
			this.frame.getContentPane().removeAll();
			((JPanel)(this.frame.getContentPane())).updateUI();
			this.frame.getContentPane().repaint();
			this.frame.initCover();
		}
	}
}

//Tùy chỉnh phong cách bảng điều khiển
class MyPanel extends JPanel{
	private ArrayList<String> diy_image,default_image;
	public int order;

	//Phương thức khởi tạo, gán giá trị cho các biến
	MyPanel(ArrayList<String> diy_image,ArrayList<String> default_image)
	{
		this.diy_image=diy_image;
		this.default_image=default_image;
		order=0;
	}

	@Override
	public void paint(Graphics g)
	{
		Image bg=Toolkit.getDefaultToolkit().getImage(AssetHelper.IMAGE_BG);
		g.drawImage(bg, 0, 0, this);

		//Vẽ hình ảnh người chơi tự nhập
		if(diy_image.size()!=0&&order<diy_image.size())
		{
			Image pic=Toolkit.getDefaultToolkit().getImage("D:/DIYdata/"+diy_image.get(order));

			//Vẽ hình ảnh ở giữa
			g.drawImage(pic, (this.getWidth())/2-pic.getWidth(this)/2, (this.getHeight())/2-pic.getHeight(this)/2, this);
		}
		//Vẽ hình ảnh mặc định
		else
		{
			Image pic=Toolkit.getDefaultToolkit().getImage(AssetHelper.DEFAULT_DATA_PATH + default_image.get(order-diy_image.size()));

			//Vẽ hình ảnh ở giữa
			g.drawImage(pic, (this.getWidth())/2-pic.getWidth(this)/2, (this.getHeight())/2-pic.getHeight(this)/2, this);
		}

	}
}
