package com.gamerbah.gamezonesmp.command;
/* Created by GamerBah on 4/24/20 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record PvpCommand(GameZoneSMP plugin) implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		if (player.getGameMode() != GameMode.SURVIVAL) {
			if (!plugin.getPvp().contains(player.getUniqueId())) {
				player.sendMessage(C.GRAY + "Player combat toggled " + C.GREEN + C.BOLD + "ON");
				plugin.getServer()
				      .getOnlinePlayers()
				      .stream()
				      .filter(p -> p != player)
				      .forEach(p -> p.sendMessage(C.YELLOW + player.getName() + C.GRAY + " turned PVP " + C.GREEN + "on"));
				plugin.getPvp().add(player.getUniqueId());
			} else {
				player.sendMessage(C.GRAY + "Player combat toggled " + C.RED + C.BOLD + "OFF");
				plugin.getServer()
				      .getOnlinePlayers()
				      .stream()
				      .filter(p -> p != player)
				      .forEach(p -> p.sendMessage(C.YELLOW + player.getName() + C.GRAY + " turned PVP " + C.RED + "off"));
				plugin.getPvp().remove(player.getUniqueId());
			}
			EventSound.playSound(player, EventSound.CLICK);
		} else {
			player.sendMessage(C.RED + "You can't toggle PVP in creative mode!");
		}
		return false;
	}

}
