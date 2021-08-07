package com.gamerbah.gamezonesmp.event;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.C;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record ServerListPingListener(GameZoneSMP plugin) implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onServerPing(ServerListPingEvent event) {
		var list   = plugin.getConfig().getStringList("motd");
		int random = ThreadLocalRandom.current().nextInt(list.size());
		event.setMotd(C.translateHex(list.get(random)));
	}

}