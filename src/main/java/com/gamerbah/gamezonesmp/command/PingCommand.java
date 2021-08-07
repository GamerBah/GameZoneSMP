package com.gamerbah.gamezonesmp.command;
/* Created by GamerBah on 8/15/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

	private GameZoneSMP plugin;

	public PingCommand(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		Player target = player;

		if (args.length == 1) {
			target = plugin.getServer().getPlayer(args[0]);

			if (target == null) {
				player.sendMessage(C.RED + "That player isn't online!");
				EventSound.playSound(player, EventSound.ACTION_FAIL);
				return true;
			}
		} else if (args.length > 2) {
			M.incorrectUsage(player, "/ping [player]");
			return true;
		}

		int ping = player.getPing();

		String status = "";
		if (ping <= 20) {
			status = C.PINK + "AWESOME! ";
		}
		if (ping > 20 && ping <= 50) {
			status = C.PURPLE + "GREAT! ";
		}
		if (ping > 50 && ping <= 80) {
			status = C.GREEN + "Good! ";
		}
		if (ping > 80 && ping <= 110) {
			status = C.DARK_GREEN + "Okay. ";
		}
		if (ping > 110 && ping <= 150) {
			status = C.YELLOW + "Eh... ";
		}
		if (ping > 150 && ping <= 300) {
			status = C.GOLD + "Bad. ";
		}
		if (ping > 300) {
			status = C.DARK_RED + "RIP. ";
		}

		if (player == target) {
			player.sendMessage(C.GRAY + "Your connection is " + status + C.GRAY + "(" + ping + "ms)");
		} else {
			player.sendMessage(
					C.RED + target.getName() + C.GRAY + "'s connection is " + status + C.GRAY
					+ "(" + ping + "ms)");
		}

		return true;
	}

}
