package com.gamerbah.gamezonesmp.command;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {

	private GameZoneSMP plugin;

	public TeleportCommand(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			M.incorrectUsage(player, C.RED + "/tpr <[player]/accept/deny>");
			return true;
		}

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("accept")) {

			} else if (args[0].equalsIgnoreCase("deny")) {
				if (!plugin.getTeleportRequests().containsValue(player.getUniqueId())) {
					player.sendMessage(C.RED + "Sorry! " + C.GRAY + "You don't have any pending requests!");
					EventSound.playSound(player, EventSound.ACTION_FAIL);
					return true;
				}


			} else {
				Player target = plugin.getServer().getPlayer(args[0]);
				if (target == null) {
					player.sendMessage(C.RED + "Sorry! " + C.GRAY + "That player isn't online!");
					return true;
				}

				if (target == player) {
					player.sendMessage(C.RED + "Sorry! " + C.GRAY + "You can't request to teleport to yourself!");
					EventSound.playSound(player, EventSound.ACTION_FAIL);
					return true;
				}

				if (plugin.getTeleportRequests().containsKey(player.getUniqueId())) {
					player.sendMessage(C.RED + "Sorry! " + C.GRAY + "You already have a pending request!");
					EventSound.playSound(player, EventSound.ACTION_FAIL);
					return true;
				}

				plugin.getTeleportRequests().put(player.getUniqueId(), target.getUniqueId());
			}
		}

		return true;
	}

}
