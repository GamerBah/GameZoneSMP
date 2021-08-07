package com.gamerbah.gamezonesmp.event.player;
/* Created by GamerBah on 4/23/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.ChunkLoader;
import com.gamerbah.gamezonesmp.util.DeathChest;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import java.util.Optional;

public record PlayerInteract(GameZoneSMP plugin) implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.RESPAWN_ANCHOR) {
				if (event.getItem() != null && event.getItem().getType().toString().endsWith("HOE")) {
					Optional<ChunkLoader> optional = plugin.getChunkLoaders()
					                                       .stream()
					                                       .filter(loader -> loader.sameLocationAs(
							                                       event.getClickedBlock().getLocation()))
					                                       .findFirst();
					if (optional.isPresent()) {
						event.setCancelled(true);
						ChunkLoader loader = optional.get();
						if (loader.getOwner().equals(player.getUniqueId())) {
							loader.setTagVisible(!loader.isTagVisible());
							loader.getHologram().setVisible(loader.isTagVisible());
						} else {
							player.sendMessage(C.RED + " \u00bb  " + Hex.SLATE +
							                   "You can't toggle visibility of that Chunk Loader!");
							EventSound.playSound(player, EventSound.ACTION_FAIL);
						}
					}
				}
			}
		}

		if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
				Optional<DeathChest> optional = plugin.getDeathChests()
				                                      .stream()
				                                      .filter(chest -> chest.sameLocationAs(
						                                      event.getClickedBlock().getLocation()))
				                                      .findFirst();
				if (optional.isPresent()) {
					event.setCancelled(true);
					if (optional.get().getUuid().equals(player.getUniqueId())) {
						optional.get().collect(player);
						plugin.getDeathChests().remove(optional.get());
						player.sendMessage(C.GREEN + " \u00bb " + Hex.SLATE + "Death Chest collected!");
						player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1F, 1F);
					} else {
						player.sendMessage(
								C.RED + " \u00bb " + Hex.SLATE + "You can't collect a death chest that isn't yours!");
						EventSound.playSound(player, EventSound.ACTION_FAIL);
					}
				}
			}
		}
	}

}
