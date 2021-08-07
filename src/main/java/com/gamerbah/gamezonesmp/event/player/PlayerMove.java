package com.gamerbah.gamezonesmp.event.player;
/* Created by GamerBah on 7/23/2018 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.task.player.AFKRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.Objects;

public record PlayerMove(GameZoneSMP plugin) implements Listener {

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (plugin.getAfk().contains(event.getPlayer().getUniqueId())) {
			if (event.getFrom().distance(Objects.requireNonNull(event.getTo())) > 0) {
				event.getPlayer().chat("/afk");
			}
		} else {
			AFKRunnable.reset(event.getPlayer());
		}
	}

	@EventHandler
	public void onFlightChange(PlayerToggleFlightEvent event) {
		if (plugin.getUpdateManager().isUpdating() || plugin.isFrozen() ||
		    plugin.getFrozenPlayers().contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}

}
