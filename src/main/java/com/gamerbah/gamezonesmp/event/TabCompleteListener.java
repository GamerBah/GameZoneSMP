package com.gamerbah.gamezonesmp.event;
/* Created by GamerBah on 4/23/21 */

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteListener implements Listener {

	@EventHandler
	public void onTabComplete(TabCompleteEvent event) {
		String buffer = event.getBuffer();
		if (event.getSender() instanceof Player) {
			if (buffer.startsWith("/reload")) {
				List<String> completions = new ArrayList<>(2);
				completions.add("config");
				completions.add("plugin");
				event.setCompletions(completions);
			}
			if (buffer.startsWith("/gamemode")) {
				List<String> completions = new ArrayList<>(3);
				completions.add("creative");
				completions.add("survival");
				completions.add("spectator");
				event.setCompletions(completions);
			}
		}
	}

}
