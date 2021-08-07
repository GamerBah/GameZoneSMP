package com.gamerbah.gamezonesmp.command;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class MessageCommand implements CommandExecutor {

	private GameZoneSMP plugin;

	public MessageCommand(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		if (args.length <= 1) {
			M.incorrectUsage(player, C.RED + "/" + label + " <player> <message>");
			return true;
		}

		Player target = plugin.getServer().getPlayer(args[0]);

		if (target == null) {
			player.sendMessage(C.RED + "Sorry! " + C.GRAY + "That player isn't online!");
			return true;
		}

		if (target == player) {
			String[] messages = {
					"What's the point in messaging yourself?", "Trying to talk to yourself again, eh?",
					"I don't think that's how that works...", "You're supposed to message other players!"
			};
			player.sendMessage(C.RED + messages[new Random().nextInt(4)]);
			EventSound.playSound(player, EventSound.ACTION_FAIL);
			return true;
		}

		plugin.getMessaging().put(player.getUniqueId(), target.getUniqueId());
		plugin.getMessaging().put(target.getUniqueId(), player.getUniqueId());

		String message = StringUtils.join(args, ' ', 1, args.length);

		player.sendMessage(
				C.GRAY + C.ITALIC + "To " + C.AQUA + C.ITALIC
				+ target.getName() + C.WHITE + C.ITALIC + ": " + C.DARK_AQUA + C.ITALIC + message.trim());
		target.sendMessage(
				C.GRAY + C.ITALIC + "From " + C.AQUA + C.ITALIC
				+ player.getName() + C.WHITE + C.ITALIC + ": " + C.DARK_AQUA + C.ITALIC + message.trim());

		target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.5F, 1.5F);

		return true;
	}

}
