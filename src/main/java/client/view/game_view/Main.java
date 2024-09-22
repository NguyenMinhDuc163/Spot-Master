package client.view.game_view;

import client.helper.AssetHelper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Main {
	public static void main(String[] args)
	{
		final PlayFrame pf=new PlayFrame();
		final SelectPanel sp=new SelectPanel(pf);
		pf.initCover();
		final MusicPlayer bgm=new MusicPlayer(AssetHelper.MUSIC_BGM);
		bgm.start(true);

		pf.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent we)
			{
				bgm.stop();
			}
		});
	}
}
