package com.gamerbah.gamezonesmp.event;
/* Created by GamerBah on 9/5/2017 */

import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;

public class EntityTeleport implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(EntityTeleportEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Enderman enderman) {
			if (enderman.getHealth() <= 20) {
				event.setCancelled(true);
			}
		}
	}

}
