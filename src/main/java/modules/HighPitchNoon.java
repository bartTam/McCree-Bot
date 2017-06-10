package modules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import jesse.mccree.BotLauncher;
import sound.Sound;
import sound.SoundCallback;
import sound.SoundPlayer;
import sx.blah.discord.handle.audio.impl.AudioManager;

public class HighPitchNoon extends JButton implements Modular {

	private static final String INN_NAME = "Stormy Inn";
	private static final URL HIGH_PITCH_JAR = RolePlayers.class.getResource("/resources/MonoHighNoon.mp3");
	private static final URL HIGH_PITCH = RolePlayers.class.getResource("/MonoHighNoon.mp3");
	
	private SoundPlayer player;
	private Sound soundFile;
	
	private boolean inSetup = true;
	
	public HighPitchNoon(){
		super("High Pitch Noon");
		
		if(BotLauncher.IN_JAR){
			soundFile = new HighPitchSound(INN_NAME, HIGH_PITCH_JAR);
		} else {
			soundFile = new HighPitchSound(INN_NAME, HIGH_PITCH);
		}
				
		
	}


	public void setUpModule(final SoundPlayer player) {
		if(inSetup){
			this.player = player;
			addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt) {
					player.playSound(soundFile);
				}
			});
			
		}
		inSetup = false;
	}
	
	private class HighPitchSound extends Sound {
		public HighPitchSound(String name, URL url) {
			super(name, url);
		}
		
		public void reset(){
			try {
				super.stream = getPCMStream(AudioSystem.getAudioInputStream(url));
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private AudioInputStream getPCMStream(AudioInputStream stream){
			AudioFormat baseFormat = stream.getFormat();

			//Converts first to PCM data. If the data is already PCM data, this will not change anything.
			AudioFormat toPCM = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
					//AudioConnection.OPUS_SAMPLE_RATE,
					baseFormat.getSampleSizeInBits() != -1 ? baseFormat.getSampleSizeInBits() : 16,
					baseFormat.getChannels(),
					//If we are given a frame size, use it. Otherwise, assume 16 bits (2 8bit shorts) per channel.
					baseFormat.getFrameSize() != -1 ? baseFormat.getFrameSize() : 2 * baseFormat.getChannels(),
					baseFormat.getFrameRate() != -1 ? baseFormat.getFrameRate() : baseFormat.getSampleRate(),
					baseFormat.isBigEndian());
			AudioInputStream pcmStream = AudioSystem.getAudioInputStream(toPCM, stream);

			// Resample to standardized form
			AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioManager.OPUS_SAMPLE_RATE, 16, 
					baseFormat.getFrameSize() != -1 ? baseFormat.getFrameSize() : 2 * baseFormat.getChannels(), 4, (float) 38.28125, true);
			return AudioSystem.getAudioInputStream(audioFormat, pcmStream);
		}
	}
	
}
