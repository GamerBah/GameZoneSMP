package com.gamerbah.gamezonesmp.event;
/* Created by GamerBah on 7/25/2018 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public record InventoryClose(GameZoneSMP plugin) implements Listener {

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		plugin.getInventoryCheck().remove(player.getUniqueId());

	}

}
