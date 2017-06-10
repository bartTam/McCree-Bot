package jesse.mccree;

import modules.RolePlayers;
import modules.SadViolin;
import modules.StreamingChan;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;

public class BotLauncher {
	
	private static String TOKEN = "MjE3NDU0MjUxNzM0MDczMzQ2.Cp03kQ.0W2abENLfgKWkM-JIVIDodAIMNg";
	private static Interface INTERFACE = null;
	
	public static final boolean IN_JAR = true;
	
	private static final boolean LOAD_ROLEPLAYER = true;
	private static final String ROLEPLAYER_MODULE = RolePlayers.class.getName();
	private static final boolean LOAD_STREAMING_CHAN = true;
	private static final String STREAMING_CHAN_MODULE = StreamingChan.class.getName();
	private static final boolean LOAD_SAD_VIOLIN = true;
	private static final String SAD_VIOLIN_MODULE = SadViolin.class.getName();
	
	public static void main(String[] args) {
		final Options modules = new Options();
		
		if(LOAD_ROLEPLAYER){
			modules.add(ROLEPLAYER_MODULE);
		}
		
		if(LOAD_STREAMING_CHAN){
			modules.add(STREAMING_CHAN_MODULE);
		}
		
		if(LOAD_SAD_VIOLIN){
			modules.add(SAD_VIOLIN_MODULE);
		}
		
		
		
		try {
			IDiscordClient client = getClient(TOKEN, true);
			EventDispatcher dispatcher = client.getDispatcher();
			dispatcher.registerListener(new IListener<MessageReceivedEvent>(){
				public void handle(MessageReceivedEvent evt) {
					if(INTERFACE == null){
						INTERFACE = new Interface(evt.getClient(), evt.getMessage(), modules);
					}
				}
			});
		} catch (DiscordException e) {
			e.printStackTrace();
		}
		
	}

	
	public static IDiscordClient getClient(String token, boolean login) throws DiscordException { //Returns an instance of the discord client
	    ClientBuilder clientBuilder = new ClientBuilder(); //Creates the ClientBuilder instance
	    clientBuilder.withToken(token); //Adds the login info to the builder
	    if (login) {
	      return clientBuilder.login(); //Creates the client instance and logs the client in
	    } else {
	      return clientBuilder.build(); //Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
	    }
	}
}
