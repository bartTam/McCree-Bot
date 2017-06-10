package jesse.mccree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import sound.Sound;
import sound.SoundPlayer;

public class McCreeBot extends JPanel {

	private static final int SEC = 1000;
	private static final int MIN = SEC * 60;
	private static final int HOUR = MIN * 60;
	
	private static final int HIGH_NOON_FREQUENCY = HOUR;
	
	private static final String HIGH_NOON_NAME = "High Noon";
	private static final URL HIGH_NOON_URL_JAR = Interface.class.getResource("/resources/HighNoon.mp3");
	private static final URL HIGH_NOON_URL = Interface.class.getResource("/HighNoon.mp3");
	private Sound highNoon;
	
	private static final String SOMEWHERE_IN_WORLD_NAME = "Somewhere in the World";
	private static final URL SOMEWHERE_IN_WORLD_URL_JAR = Interface.class.getResource("/resources/SomewhereInWorld.mp3");
	private static final URL SOMEWHERE_IN_WORLD_URL = Interface.class.getResource("/SomewhereInWorld.mp3");
	private Sound somewhereInWorld;
	
	private SoundPlayer soundPlayer;
	
	

	public McCreeBot(SoundPlayer player){
		super();
		
		if(BotLauncher.IN_JAR){
			somewhereInWorld = new Sound(SOMEWHERE_IN_WORLD_NAME, SOMEWHERE_IN_WORLD_URL_JAR);
			highNoon = new Sound(HIGH_NOON_NAME, HIGH_NOON_URL_JAR);
		} else {
			somewhereInWorld = new Sound(SOMEWHERE_IN_WORLD_NAME, SOMEWHERE_IN_WORLD_URL);
			highNoon = new Sound(HIGH_NOON_NAME, HIGH_NOON_URL);
		}
		
		soundPlayer = player;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JButton playHighNoonButton = new JButton("Play High Noon");
		playHighNoonButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				playHighNoon();
			}
		});
		add(playHighNoonButton);
		
		JButton playSomewhereInWorldButton = new JButton("Play Somewhere in the World");
		playSomewhereInWorldButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				playSomewhereInTheWorld();
			}
		});
		add(playSomewhereInWorldButton);
		
		
		startHighNoon();
		
	}
	

	private void playHighNoon(){
		soundPlayer.playSound(highNoon);
	}
	
	private void playSomewhereInTheWorld(){
		soundPlayer.playSound(somewhereInWorld);
	}

	
	private Thread startHighNoon() {
		Thread thread = new Thread(){

			public void run() {
				do {
					try {
						sleep(HIGH_NOON_FREQUENCY - (System.currentTimeMillis() % HIGH_NOON_FREQUENCY));
						
						if(getCurrentTimeStamp() == 12){
							playHighNoon();
						}
						else {
							playSomewhereInTheWorld();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
						
					
				}while(true);
			}
			
		};
		thread.start();
		return thread;
	}
	
	public static int getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("HH");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return Integer.parseInt(strDate);
	}
}
