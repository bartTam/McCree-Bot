package modules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import jesse.mccree.BotLauncher;
import sound.Sound;
import sound.SoundCallback;
import sound.SoundPlayer;

public class StreamingChan extends JToggleButton implements Modular{

	private static final String INN_NAME = "Stormy Inn";
	private static final URL INN_URL_JAR = RolePlayers.class.getResource("/resources/Go Go Streaming Chan.mp3");
	private static final URL INN_URL = RolePlayers.class.getResource("/Go Go Streaming Chan.mp3");
	
	private SoundPlayer player;
	private Sound soundFile;
	
	private boolean enabled = false;
	private SoundCallback repeatCallback;
	
	private boolean inSetup = true;
	
	public StreamingChan(){
		super("Streaming Chan");
		
		if(BotLauncher.IN_JAR){
			soundFile = new Sound(INN_NAME, INN_URL_JAR);
		} else {
			soundFile = new Sound(INN_NAME, INN_URL);
		}
		repeatCallback = new SoundCallback(){
			public void soundEnded() {
				System.out.println("Repeating streaming chan");
				checkRepeat();
			}
		};
				
		
	}

	private void checkRepeat(){
		System.out.println("enabled:" + enabled);
		if(enabled){
			System.out.println("playing sound");
			player.playSound(soundFile, repeatCallback);
		}
	}

	public void setUpModule(final SoundPlayer player) {
		if(inSetup){
			this.player = player;
			addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt) {
					enabled = ((JToggleButton)evt.getSource()).isSelected();
					if(enabled){
						player.playSound(soundFile, repeatCallback);
					} else {
						player.stop(soundFile);
					}
				}
			});
			
		}
		inSetup = false;
	}
	
	
}
