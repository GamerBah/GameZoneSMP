package com.gamerbah.gamezonesmp.command;
/* Created by GamerBah on 2/2/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.EventSound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record SpawnCommand(GameZoneSMP plugin) implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		plugin.respawn(player, false);
		EventSound.playSound(player, EventSound.COMMAND_NEEDS_CONFIRMATION);

		return true;
	}

}
