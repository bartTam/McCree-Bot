package sound;

import javax.security.auth.callback.Callback;

public interface SoundCallback extends Callback {
	public void soundEnded();
}
