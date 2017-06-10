package sound;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.audio.IAudioProvider;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.audio.AudioPlayer;

public class SoundPlayer implements Closeable{
	
	private static final int SEC = 1000;
	
	private ArrayList<Sound> sounds;
	private SoundBuffer buffer;
	
	private IVoiceChannel voiceChannel;
	private AudioPlayer player;	
	private IDiscordClient client;
	
	public SoundPlayer(IDiscordClient client){	
		this.client = client;
		sounds = new ArrayList<Sound>();
		buffer = new SoundBuffer();
		voiceChannel = getHighestPopVC();
		player = AudioPlayer.getAudioPlayerForGuild(voiceChannel.getGuild());
		checkVCThread();
	}

	private Thread checkVCThread() {
		Thread checkVCThread = new Thread(){
			@Override
			public void run(){
				while(true){
					try {
						sleep(10 * SEC);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(player.getPlaylistSize() == 0 && voiceChannel.isConnected() && !canPlay()){
						voiceChannel.leave();
					} 
				}
			}
		};
		checkVCThread.start();
		return checkVCThread;
	}
	

	private void returnToChannel() throws MissingPermissionsException{
		if(!voiceChannel.isConnected()){
			voiceChannel.join();
		}
		if(player.getPlaylistSize() == 0){
			player.queue(buffer);
		}
	}
	
	private IVoiceChannel getHighestPopVC(){
		Collection<IVoiceChannel> voiceChannels = client.getVoiceChannels();
		IVoiceChannel currentChannel = null;
		for(IVoiceChannel channel : voiceChannels){
			if(currentChannel == null){
				currentChannel = channel;
			}
			else {
				if(channel.getConnectedUsers().size() > currentChannel.getConnectedUsers().size()){
					currentChannel = channel;
				}
			}
		}
		return currentChannel;
	}
	
	
	public SoundBuffer playSound(Sound sound){
		if(!sounds.contains(sound)){
			sounds.add(sound);
		} 
		try {
			returnToChannel();
			buffer.queue(sound);

			return buffer;
		} catch (MissingPermissionsException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public SoundBuffer playSound(Sound sound, SoundCallback callback){
		if(!sounds.contains(sound)){
			sounds.add(sound);
		} 
		try {
			returnToChannel();
			buffer.queue(sound, callback);

			return buffer;
		} catch (MissingPermissionsException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void stop(Sound soundFile) {
		buffer.remove(soundFile);
	}


	public boolean canPlay() {
		return buffer.isReady();
	}

	public void close() throws IOException {
		if(player != null && player.getPlaylistSize() > 0){
			while(player.getPlaylistSize() > 0){
				player.skip();
			}
		}
		if(voiceChannel != null){
			voiceChannel.leave();
		}
	}
}
