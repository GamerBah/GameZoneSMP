package com.gamerbah.gamezonesmp.event.player;
/* Created by GamerBah on 5/16/19 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public record PlayerDamageEvent(GameZoneSMP plugin) implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player player) {
			if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
				if (player.getGameMode() == GameMode.SPECTATOR) {
					event.setCancelled(true);
				}
			}
			if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
				if (plugin.getRespawning().contains(player.getUniqueId())) {
					event.setCancelled(true);
				}
			}
		}
	}

}
