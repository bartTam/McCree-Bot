package sound;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;

import sx.blah.discord.Discord4J;
import sx.blah.discord.handle.audio.IAudioProvider;
import sx.blah.discord.handle.audio.impl.AudioManager;
import sx.blah.discord.util.LogMarkers;
import util.Utils;

public class SoundBuffer implements IAudioProvider {

	private Vector<Sound> input;
	private Map<Sound, SoundCallback> callbacks;
	private static final AudioFormat FOUR_BYTE_STEREO = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioManager.OPUS_SAMPLE_RATE, 16, 2, 4, (float) 38.28125, true);
	
	
	public SoundBuffer(){
		callbacks = Collections.synchronizedMap(new HashMap<Sound, SoundCallback>());
		input = new Vector<Sound>();
	}
	

	
	public boolean isReady() {
		return input.size() > 0;
	}
	
	public void queue(Sound stream, SoundCallback finished){
		if(callbacks.containsKey(input)){
			callbacks.replace(stream, finished);
		} else {
			callbacks.put(stream, finished);
			queue(stream);
		}
	}
	
	public void queue(Sound stream){
		if(!input.contains(stream)){
			input.add(stream);
		}
	}
	
	

	
	private void checkIfRemove(Sound stream, int bytesRead, Vector<Sound> toBeRemoved) throws IOException{
		if(bytesRead == -1){
			stream.reset();
			toBeRemoved.add(stream);
		}
	}

	//TODO: Average sounds : http://math.stackexchange.com/questions/106700/incremental-averageing
	// http://stackoverflow.com/questions/35617388/pcm-byte-array-addition
	public byte[] provide() {
		byte[] audio = new byte[AudioManager.OPUS_FRAME_SIZE * FOUR_BYTE_STEREO.getFrameSize()];
		try {
			Vector<Sound> toBeRemoved = new Vector<Sound>();
			int maxAmountRead = 0;
			boolean firstIteration = true;
			int loopIndex = 1;
			for(Sound sounds : input){
				if(firstIteration){
					maxAmountRead = sounds.getAudioInputStream().read(audio, 0, audio.length);
					checkIfRemove(sounds, maxAmountRead, toBeRemoved);
					firstIteration = false;
				} else {
					byte[] streamAudio = new byte[AudioManager.OPUS_FRAME_SIZE * FOUR_BYTE_STEREO.getFrameSize()];
					int readReturn = sounds.getAudioInputStream().read(streamAudio, 0, streamAudio.length);
					maxAmountRead = maxAmountRead > readReturn ? maxAmountRead : readReturn;
					checkIfRemove(sounds, readReturn, toBeRemoved);
					for(int arrayTraversal = 0; arrayTraversal < streamAudio.length; arrayTraversal++){/*
						audio[arrayTraversal] = Utils.convertToSignedByte((Utils.convertToSignedInt(audio[arrayTraversal]) + 
								(Utils.convertToSignedInt(streamAudio[arrayTraversal]) - 
								Utils.convertToSignedInt(audio[arrayTraversal]))/ loopIndex));*/
						int temp = audio[arrayTraversal] + streamAudio[arrayTraversal];
						if(temp > 127 ){
							audio[arrayTraversal] = (byte) 127;
						} else if(temp < -128) {
							audio[arrayTraversal] = (byte) -128;
						} else {
							audio[arrayTraversal] = (byte) temp;
						}
					}
				}
				loopIndex++;
			}
			
			for(Sound sound : toBeRemoved){
				input.remove(sound);
				if(callbacks.containsKey(sound)){
					SoundCallback currentCallback = callbacks.get(sound);
					callbacks.remove(sound);
					currentCallback.soundEnded();
				}
				sound.reset();
			}
			
			if (maxAmountRead > -1) {
				return audio;
			}
		} catch (IOException e) {
			Discord4J.LOGGER.error(LogMarkers.VOICE, "Discord4J Internal Exception", e);
		}
		return new byte[0];
	}

	public int getChannels(){
		return 2;
	}
	
	public AudioEncodingType getAudioEncodingType(){
		return AudioEncodingType.PCM;
	}

	public void remove(Sound sound) {
		if(input.contains(sound)){
			input.remove(sound);
			sound.reset();
		}
		if(callbacks.containsKey(sound)){
			callbacks.remove(sound);
		}
	}

}
