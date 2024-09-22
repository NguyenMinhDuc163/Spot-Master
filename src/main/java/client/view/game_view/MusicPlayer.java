package client.view.game_view;

import client.helper.AssetHelper;

import javax.sound.sampled.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class MusicPlayer {
	private String musicPath; //Tệp âm thanh
	private volatile boolean run = true;  //Ghi lại xem âm thanh có đang phát hay không
	private Thread mainThread;   //Luồng tác vụ phát âm thanh

	private AudioInputStream audioStream;
	private AudioFormat audioFormat;
	private SourceDataLine sourceDataLine;

	public MusicPlayer(String musicPath) {
		this.musicPath = musicPath;
		prefetch();
	}

	//Chuẩn bị dữ liệu
	private void prefetch(){
		try{
			//Lấy luồng đầu vào âm thanh
			audioStream = AudioSystem.getAudioInputStream(new File(musicPath));
			//Lấy đối tượng mã hóa âm thanh
			audioFormat = audioStream.getFormat();
			//Đóng gói thông tin âm thanh
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
					audioFormat,AudioSystem.NOT_SPECIFIED);
			//Sử dụng lớp Info sau khi đóng gói thông tin âm thanh để tạo dòng dữ liệu nguồn, đóng vai trò là nguồn của bộ trộn
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);

			sourceDataLine.open(audioFormat);
			sourceDataLine.start();

		}catch(UnsupportedAudioFileException ex){
			ex.printStackTrace();
		}catch(LineUnavailableException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}

	}
	//Hàm hủy: Đóng luồng đọc âm thanh và dòng dữ liệu
//	protected void finalize() throws Throwable{
//		super.finalize();
//		sourceDataLine.drain();
//		sourceDataLine.close();
//		audioStream.close();
//	}
//
	//Phát âm thanh: thiết lập thông qua tham số loop để phát lặp lại hay không
	private void playMusic(boolean loop)throws InterruptedException {
		try{
			if(loop){
				while(true){
					playMusic();
				}
			}else{
				playMusic();
				//Xóa dòng dữ liệu và đóng nó
				sourceDataLine.drain();
				sourceDataLine.close();
				audioStream.close();
			}

		}catch(IOException ex){
			ex.printStackTrace();
		}


	}
	private void playMusic(){
		try{
			synchronized(this){
				run = true;
			}
			//Đọc luồng dữ liệu âm thanh qua dòng dữ liệu và gửi đến bộ trộn;
			//Quá trình truyền dữ liệu: AudioInputStream -> SourceDataLine;
			audioStream = AudioSystem.getAudioInputStream(new File(musicPath));
			int count;
			byte tempBuff[] = new byte[1024];

			while((count = audioStream.read(tempBuff,0,tempBuff.length)) != -1){
				synchronized(this){
					while(!run)
						wait();
				}
				sourceDataLine.write(tempBuff,0,count);

			}

		}catch(UnsupportedAudioFileException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}

	}


	//Tạm dừng phát âm thanh
	private void stopMusic(){
		synchronized(this){
			run = false;
			notifyAll();
		}
	}
	//Tiếp tục phát nhạc
	private void continueMusic(){
		synchronized(this){
			run = true;
			notifyAll();
		}
	}


	//Phương thức điều khiển bên ngoài: tạo luồng chính phát âm thanh;
	public void start(final boolean loop){
		mainThread = new Thread(new Runnable(){
			public void run(){
				try {
					playMusic(loop);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		mainThread.start();
	}

	//Phương thức điều khiển bên ngoài: tạm dừng luồng âm thanh
	public void stop(){
		new Thread(new Runnable(){
			public void run(){
				stopMusic();

			}
		}).start();
	}
	//Phương thức điều khiển bên ngoài: tiếp tục luồng âm thanh
	public void continues(){
		new Thread(new Runnable(){
			public void run(){
				continueMusic();
			}
		}).start();
	}

	//Kiểm tra
	public static void main(String[] args) throws InterruptedException{

		MusicPlayer player = new MusicPlayer(AssetHelper.MUSIC_BGM);   //Tạo trình phát nhạc

		player.start(true);                                        //Bắt đầu phát nhạc dưới dạng lặp lại, player(false) là phát không lặp lại

		TimeUnit.SECONDS.sleep(5);

		player.stop();                        //Tạm dừng phát âm thanh

		TimeUnit.SECONDS.sleep(4);

		player.continues();                //Tiếp tục phát âm thanh

	}

}
