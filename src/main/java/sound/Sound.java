package sound;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import sx.blah.discord.handle.audio.impl.AudioManager;

public class Sound {
	
	protected String name;
	protected URL url;
	protected AudioInputStream stream;
	
	public Sound(String name, URL url){
		this.name = name;
		this.url = url;
		reset();
	}
	
	public void reset(){
		try {
			stream = getPCMStream(AudioSystem.getAudioInputStream(url));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public boolean equals(Object o){
		if(o != null && o instanceof Sound){
			return name.equals(((Sound)o).name);
		}
		return false;
	}
	
	@Override
	public String toString(){
		return name;
	}

	public AudioInputStream getAudioInputStream(){
		return stream;
	}
	
	
	
	private static AudioInputStream getPCMStream(AudioInputStream stream) {
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
		AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioManager.OPUS_SAMPLE_RATE, 16, 2, 4, (float) 38.28125, true);
		return AudioSystem.getAudioInputStream(audioFormat, pcmStream);
	}
}
