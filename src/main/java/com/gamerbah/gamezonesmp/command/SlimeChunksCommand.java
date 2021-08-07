package com.gamerbah.gamezonesmp.command;
/* Created by GamerBah on 6/6/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record SlimeChunksCommand(GameZoneSMP plugin) implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		Chunk chunk = player.getLocation().getChunk();

		StringBuilder builder = new StringBuilder(
				"\n" + C.GREEN + "\u2588" + Hex.SLATE + " = Slime Chunk, Top is North\n");

		for (int x = -4; x < 5; x++) {
			for (int z = -4; z < 5; z++) {
				Chunk test = player.getWorld().getChunkAt(chunk.getX() - x, chunk.getZ() - z);
				if (x == 0 || z == 0) {
					builder.append(test.isSlimeChunk() ? C.GREEN + "\u2588" : Hex.DARK_GRAY + "\u2588");
				} else {
					builder.append(test.isSlimeChunk() ? C.GREEN + "\u2588" : Hex.LIGHT_GRAY + "\u2588");
				}
			}
			builder.append("\n");
		}

		player.sendMessage(builder.toString());
		player.sendMessage(" ");

		return true;
	}

}
