package com.gamerbah.gamezonesmp.command;
/* Created by GamerBah on 11/3/18 */

import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		if (player.getBedSpawnLocation() == null) {
			player.sendMessage(C.RED + "You do not have a bed to go to!");
			EventSound.playSound(player, EventSound.ACTION_FAIL);
			return true;
		}

		if (player.getHealth() <= 19) {
			player.sendMessage(C.RED + "You need full health in order to do this!");
			EventSound.playSound(player, EventSound.ACTION_FAIL);
			return true;
		}

		player.getWorld()
		      .spawnParticle(Particle.SMOKE_LARGE, player.getLocation().add(0, 1, 0), 50, 0.2, 0.6, 0.2, 0.001);
		player.teleport(player.getBedSpawnLocation());
		player.playSound(player.getBedSpawnLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
		player.getWorld()
		      .spawnParticle(Particle.SMOKE_LARGE, player.getLocation().add(0, 1, 0), 50, 0.2, 0.6, 0.2, 0.001);

		return false;
	}

}
