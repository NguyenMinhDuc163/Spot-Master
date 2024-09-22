package client.view.game_view;

import client.helper.AssetHelper;

import java.io.*;
import java.util.*;

//Dùng để đọc và lưu trữ dữ liệu cấp độ mà người chơi nhập vào (kèm theo một hàm để lấy dữ liệu cấp độ mặc định)
public class DIYdata {
	String source1_path,source1_name;
	String source2_path,source2_name;
	File message;
	double[] pointXY;
	static String data_path="D://DIYdata";
	ArrayList<String> data;

	//Lưu tệp vào đường dẫn chỉ định
	public void save(String source1_path,String source1_name,String source2_path,String source2_name,double[] pointXY)throws Exception
	{
		this.source1_path=source1_path;
		this.source1_name=source1_name;
		this.source2_path=source2_path;
		this.source2_name=source2_name;
		this.pointXY=pointXY;


		File dir=new File(data_path);
		if(!dir.exists()) dir.mkdirs();

		File source1=new File(source1_path);
		File copy1=new File(data_path+File.separator+source1_name);

		FileInputStream input=new FileInputStream(source1);
		FileOutputStream output=new FileOutputStream(copy1);


		//Sao chép hình ảnh 1 vào đường dẫn chỉ định
		byte[] b=new byte[1024*50];
		int k;
		while ((k = input.read(b)) != -1) { output.write(b, 0, k); }
		output.flush();
		output.close();
		input.close();

		File source2=new File(source2_path);
		File copy2=new File(data_path+File.separator+source2_name);

		input=new FileInputStream(source2);
		output=new FileOutputStream(copy2);

		//Sao chép hình ảnh 2 vào đường dẫn chỉ định
		while ((k = input.read(b)) != -1) { output.write(b, 0, k); }
		output.flush();
		output.close();
		input.close();

		//Lưu tên hình ảnh và tọa độ các điểm khác nhau vào message.txt
		message=new File(data_path+File.separator+"message.txt");
		if(!message.exists()) message.createNewFile();
		FileWriter fw=new FileWriter(message,true);
		fw.append(source1_name+' '+source2_name+' ');
		for(double xy:pointXY) fw.append(xy+" ");
		fw.append("\n");
		fw.close();
	}

	//Gán dữ liệu vào các tham số dựa trên giá trị key (0 là đọc cấp độ mặc định, 1 là đọc cấp độ mà người chơi nhập vào)
	public void get(int key,ArrayList<String> name1,ArrayList<String> name2,ArrayList<double[]> XY)
	{
		if(key==1) {
			return; //Không có cấp độ nào do người chơi nhập vào (tạm thời bỏ qua tính năng này)
			//message=new File(data_path+File.separator+"message.txt");
		}
		else message=new File(AssetHelper.DEFAULT_DATA_MESSAGE);

		try {
			BufferedReader br=new BufferedReader(new FileReader(message));
			String line;
			while((line=br.readLine())!=null)
			{
				String[] data=line.split(" ");
				name1.add(data[0]); name2.add(data[1]);

				double[] xy=new double[10];
				for(int i=2,j=0;i<12;i++,j++)
				{
					xy[j]=Double.valueOf(data[i]);
				}
				XY.add(xy);
			}
		} catch (Exception e) {e.printStackTrace();}
	}
}
