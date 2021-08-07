package com.gamerbah.gamezonesmp.util.integration.twitch;

/*
 *  Code from https://github.com/PabloPerezRodriguez/twitch-chat/
 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.Hex;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TwitchBot extends ListenerAdapter {

	private final GameZoneSMP             plugin;
	private final PircBotX                ircBot;
	private final String                  username;
	@Getter
	private       String                  channel;
	private       ExecutorService         myExecutor;
	private       HashMap<String, String> formattingColorCache;

	public TwitchBot(GameZoneSMP plugin, String username, String oauthKey, String channel) {
		this.plugin          = plugin;
		this.channel         = channel.toLowerCase();
		this.username        = username.toLowerCase();
		formattingColorCache = new HashMap<>();

		Configuration.Builder builder = new Configuration.Builder().setAutoNickChange(false) //Twitch doesn't support multiple users
		                                                           .setOnJoinWhoEnabled(false) //Twitch doesn't support WHO command
		                                                           .setEncoding(StandardCharsets.UTF_8) // Use UTF-8 on Windows.
		                                                           .setCapEnabled(true)
		                                                           .addCapHandler(new EnableCapHandler("twitch.tv/membership"))
		                                                           .addCapHandler(new EnableCapHandler("twitch.tv/tags"))
		                                                           .addCapHandler(new EnableCapHandler("twitch.tv/commands"))

		                                                           .addServer("irc.chat.twitch.tv", 6697)
		                                                           .setSocketFactory(SSLSocketFactory.getDefault())
		                                                           .setName(this.username)
		                                                           .setServerPassword(oauthKey);

		if (!channel.equals("")) {
			builder.addAutoJoinChannel("#" + this.channel);
		}

		Configuration config = builder.addListener(this).setAutoSplitMessage(false).buildConfiguration();

		this.ircBot     = new PircBotX(config);
		this.myExecutor = Executors.newCachedThreadPool();
	}

	public void start() {
		System.out.println("TWITCH BOT STARTED");
		myExecutor.execute(() -> {
			try {
				ircBot.startBot();
			} catch (IOException | IrcException e) {
				e.printStackTrace();
			}
		});
	}

	public void stop() {
		ircBot.stopBotReconnect();
		ircBot.close();
	}

	public boolean isConnected() {
		return ircBot.isConnected();
	}

	@Override
	public void onMessage(MessageEvent event) {
		String message = event.getMessage();
		//System.out.println("TWITCH MESSAGE: " + message);
		User user = event.getUser();
		if (user != null) {
			ImmutableMap<String, String> v3Tags = event.getV3Tags();
			if (v3Tags != null) {
				String nick     = user.getNick();
				String colorTag = v3Tags.get("color");
				HashMap<String, String> colors = plugin.getTwitchIntegration().getCachedColor();
				if (colors.containsKey(nick)) {
					if (!colors.get(nick).equalsIgnoreCase(colorTag)) {
						colors.put(nick, colorTag);
					}
				} else {
					if (colorTag == null || colorTag.isEmpty()) {
						colorTag = "#C8C8C8";
					}
					colors.put(nick, colorTag);
				}
				plugin.getTwitchIntegration().sendTwitchMessage(channel, nick, message, colors.get(nick), false);
			} else {
				System.out.println("Message with no v3tags: " + event.getMessage());
			}
		} else {
			System.out.println("NON-USER MESSAGE" + event.getMessage());
		}
	}

	@Override
	public void onAction(ActionEvent event) {
		String message = event.getMessage();
		System.out.println("ME MESSAGE: " + message);
		User user = event.getUser();
		if (user != null) {
			String nick = user.getNick();
			HashMap<String, String> colors = plugin.getTwitchIntegration().getCachedColor();
			if (!colors.containsKey(nick)) {
				colors.put(nick, Hex.TWITCH);
			}
			plugin.getTwitchIntegration().sendTwitchMessage(channel, nick, message, "", true);
		} else {
			System.out.println("NON-USER ME MESSAGE" + event.getMessage());
		}
	}

	public void joinChannel(String channel) {
		String oldChannel = this.channel;
		this.channel = channel.toLowerCase();
		if (ircBot.isConnected()) {
			myExecutor.execute(() -> {
				ircBot.sendRaw().rawLine("PART #" + oldChannel); // Leave the channel
				ircBot.sendIRC().joinChannel("#" + this.channel); // Join the new channel
				ircBot.sendCAP().request("twitch.tv/membership", "twitch.tv/tags", "twitch.tv/commands"); // Ask for capabilities
			});
		}
	}

}
