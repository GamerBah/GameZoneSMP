package com.gamerbah.gamezonesmp.event;
/* Created by GamerBah on 4/23/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public record EntityDamageEntity(GameZoneSMP plugin) implements Listener {

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (plugin.getUpdateManager().isUpdating()) {
			event.setCancelled(true);
		} else {
			if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
				Projectile projectile = (Projectile) event.getDamager();
				if (projectile.getShooter() instanceof Player && event.getEntity() instanceof Player) {
					boolean playerPvp  = plugin.getPvp().contains(event.getEntity().getUniqueId());
					boolean shooterPvp = plugin.getPvp().contains(((Player) projectile.getShooter()).getUniqueId());
					if (!playerPvp || !shooterPvp) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

}
