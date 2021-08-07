package com.gamerbah.gamezonesmp.event;
/* Created by GamerBah on 4/23/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.ChunkLoader;
import com.gamerbah.gamezonesmp.util.DeathChest;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;

public record BlockListener(GameZoneSMP plugin) implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block  block  = event.getBlock();

		if (block.getType() == Material.RESPAWN_ANCHOR) {
			if (event.getItemInHand().hasItemMeta()) {
				assert event.getItemInHand().getItemMeta() != null;
				if (event.getItemInHand().getItemMeta().hasDisplayName()) {
					if (event.getItemInHand().getItemMeta().getDisplayName().equals(Hex.ORANGE + "Chunk Loader")) {
						if (plugin.getLoaderCount().containsKey(player.getUniqueId()) &&
						    plugin.getLoaderCount().get(player.getUniqueId()) == 10) {
							player.sendMessage(
									C.RED + " \u00bb  " + Hex.SLATE + "You can't place any more Chunk Loaders!");
							player.sendMessage(
									C.GRAY + " \u00bb  " + Hex.SLATE + "You can only place a maximum of 10.");
							EventSound.playSound(player, EventSound.ACTION_FAIL);
						} else {
							player.sendMessage(C.GREEN + " \u00bb  " + Hex.SLATE + "Chunk Loader created!");
							player.sendMessage(C.GRAY + " \u00bb  " + Hex.SLATE +
							                   "Right-Click with a hoe to toggle the text display.");
							player.sendMessage(
									C.GRAY + " \u00bb  " + Hex.SLATE + "Break the block to remove the Chunk Loader.");
							EventSound.playSound(player, EventSound.ACTION_SUCCESS);
							ChunkLoader loader = new ChunkLoader(plugin, player.getUniqueId(), block.getLocation(),
							                                     block.getChunk());
							loader.create();
							plugin.getChunkLoaders().add(loader);
							plugin.getLoaderCount()
							      .put(player.getUniqueId(),
							           plugin.getLoaderCount().getOrDefault(player.getUniqueId(), 0) + 1);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (event.getBlock().getType() == Material.ENDER_CHEST) {
			Optional<DeathChest> optional = plugin.getDeathChests()
			                                      .stream()
			                                      .filter(deathChest -> deathChest.sameLocationAs(
					                                      event.getBlock().getLocation()))
			                                      .findFirst();
			if (optional.isPresent()) {
				event.setCancelled(true);
			}
		}

		if (event.getBlock().getType() == Material.RESPAWN_ANCHOR) {
			Optional<ChunkLoader> optional = plugin.getChunkLoaders()
			                                       .stream()
			                                       .filter(loader -> loader.sameLocationAs(
					                                       event.getBlock().getLocation()))
			                                       .findFirst();
			if (optional.isPresent()) {
				event.setDropItems(false);
				ChunkLoader loader = optional.get();
				if (loader.getOwner().equals(player.getUniqueId())) {
					player.sendMessage(C.GREEN + " \u00bb  " + Hex.SLATE + "Chunk Loader destroyed!");
					assert loader.getLocation().getWorld() != null;
					loader.remove();
					loader.getLocation().getWorld().dropItemNaturally(loader.getLocation(), ChunkLoader.getItemStack());
					plugin.getChunkLoaders().remove(loader);
					plugin.getLoaderCount()
					      .put(player.getUniqueId(), plugin.getLoaderCount().get(player.getUniqueId()) - 1);
				} else {
					event.setCancelled(true);
					player.sendMessage(
							C.RED + " \u00bb  " + Hex.SLATE + "You can't break someone else's Chunk Loader!");
					EventSound.playSound(player, EventSound.ACTION_FAIL);
				}
			}
		}
	}

}
