package com.gamerbah.gamezonesmp.util.task.server;
/* Created by GamerBah on 6/7/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public record ChunkRandomTick(GameZoneSMP plugin) implements Runnable {

	@Override
	public void run() {
		plugin.getChunkLoaders().forEach(chunkLoader -> {
			Chunk         chunk          = chunkLoader.getChunk();
			AtomicBoolean chunkIsInRange = new AtomicBoolean(false);
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				if (player.getLocation().getWorld().equals(chunk.getWorld())) {
					Location chunkLoc  = chunk.getBlock(7, 64, 7).getLocation();
					Location playerLoc = player.getLocation().clone();
					playerLoc.setY(64);
					if (playerLoc.distanceSquared(chunkLoc) < 16384) {
						chunkIsInRange.set(true);
						break;
					}
				}
			}
			if (!chunkIsInRange.get()) {
				for (int subChunk = 0; subChunk < 16; subChunk++) {
					for (int b = 0; b < 3; b++) {
						int x = ThreadLocalRandom.current().nextInt(0, 16);
						int y = ThreadLocalRandom.current().nextInt(16 * subChunk, 16 * (subChunk + 1));
						int z = ThreadLocalRandom.current().nextInt(0, 16);

						Block block = chunk.getBlock(x, y, z);
						if (block.getBlockData() instanceof Ageable ageData) {
							if (ageData.getAge() < ageData.getMaximumAge()) {
								ageData.setAge(ageData.getAge() + 1);
								plugin.getServer()
								      .getScheduler()
								      .scheduleSyncDelayedTask(plugin, () -> block.setBlockData(ageData));
							}
						}
					}
				}
			}
		});
	}

}
