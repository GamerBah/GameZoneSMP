package com.gamerbah.gamezonesmp.event.player;
/* Created by GamerBah on 7/23/2018 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.C;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

public record PlayerSleep(GameZoneSMP plugin) implements Listener {

	@EventHandler
	public void onSleep(PlayerBedEnterEvent event) {
		if (!event.isCancelled() && event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
			plugin.getServer()
			      .getOnlinePlayers()
			      .stream()
			      .filter(player -> player != event.getPlayer())
			      .forEach(player -> player.sendMessage(C.PINK + event.getPlayer().getName() + " is sleeping..."));
			plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.setSleeping(plugin.getSleeping() + 1), 60L);

		}
	}

	@EventHandler
	public void onSleep(PlayerBedLeaveEvent event) {
		if (!event.isCancelled()) {
			plugin.setSleeping(plugin.getSleeping() - 1);
		}
	}

}
