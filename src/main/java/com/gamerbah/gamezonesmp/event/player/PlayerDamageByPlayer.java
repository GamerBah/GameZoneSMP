package com.gamerbah.gamezonesmp.event.player;
/* Created by GamerBah on 4/24/20 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;

public record PlayerDamageByPlayer(GameZoneSMP plugin) implements Listener {

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player damaged && event.getDamager() instanceof Player damager) {

			if (!plugin.getPvp().containsAll(Arrays.asList(damaged.getUniqueId(), damager.getUniqueId()))) {
				event.setCancelled(true);
			}
		}
	}

}
