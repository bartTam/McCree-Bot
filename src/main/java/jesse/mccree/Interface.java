package jesse.mccree;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import modules.Modular;
import sound.SoundPlayer;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;

public class Interface extends JFrame {

	private SoundPlayer soundPlayer;
	private ArrayList<JComponent> modules = new ArrayList<JComponent>();

	public Interface(IDiscordClient client, IMessage message, Options options){
		super("Jesse McCree");
		
		
		// get the sound player
		soundPlayer = new SoundPlayer(client);
		
		
		modules.add(new McCreeBot(soundPlayer));
		loadOptionalModules(options);
		addModules();
		
		
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
	}
	
	private void addModules() {
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		for(JComponent module : modules){
			pane.add(module);
		}
		add(pane);
	}

	private void loadOptionalModules(Options options) {
		for(String option : options){
			try {
				Class<?> c = Class.forName(option);
				Object component = c.newInstance();
				((Modular)component).setUpModule(soundPlayer);
				modules.add((JComponent)component);
				
			} catch (ClassNotFoundException e) {
				System.err.println("Loading " + option + " failed.");
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void dispose(){
		super.dispose();
		try {
			soundPlayer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	


	
}
