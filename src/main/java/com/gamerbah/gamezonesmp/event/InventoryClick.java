package com.gamerbah.gamezonesmp.event;
/* Created by GamerBah on 7/25/2018 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.EventSound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public record InventoryClick(GameZoneSMP plugin) implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (plugin.getInventoryCheck().contains(player.getUniqueId())) {
			event.setCancelled(true);
			EventSound.playSound(player, EventSound.ACTION_FAIL);
		}
	}

}
