package com.gamerbah.gamezonesmp.util.integration.twitch;
/* Created by GamerBah on 4/24/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.C;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class TwitchIntegration {

	private final GameZoneSMP plugin;

	@Getter
	@Setter
	private ArrayList<TwitchBot> bots = new ArrayList<>();

	@Getter
	@Setter
	private HashMap<String, String> cachedColor = new HashMap<>();

	public TwitchIntegration(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	public void sendTwitchMessage(String channel, String username, String message, String textColor, boolean isMeMessage) {
		if (isMeMessage) {
			username = "* " + username;
		}

		String finalUsername = username;
		plugin.getTwitchWatchers().forEach((uuid, s) -> {
			System.out.println(uuid + ", " + s);
			if (s.equalsIgnoreCase(channel)) {
				System.out.println("TWITCH [" + s + "] \"" + textColor + " " + finalUsername + " \u00BB " + message + "\"");
				Player self = plugin.getServer().getPlayer(uuid);
				if (self != null && self.isOnline()) {
					self.sendMessage(C.hex("#9147FF") + "[Twitch] " + C.hex(textColor) + finalUsername + C.GRAY + " \u00BB " + C.GRAY + message);
				} else {
					if (self == null) {
						System.out.println("Error: Self == NULL");
						System.out.println(uuid);
					} else if (!self.isOnline()) {
						System.out.println("Error: Not Online");
						System.out.println(uuid);
					}
				}
			}
		});
	}

}
