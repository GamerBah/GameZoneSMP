package com.gamerbah.gamezonesmp.command;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.C;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {

	private GameZoneSMP plugin;

	public ReplyCommand(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		if (!plugin.getMessaging().containsKey(player.getUniqueId())) {
			player.sendMessage(C.RED + "Whoops! " + C.GRAY + "You haven't messaged anyone!");
			return true;
		}

		Player target = plugin.getServer().getPlayer(plugin.getMessaging().get(player.getUniqueId()));

		if (target == null) {
			player.sendMessage(C.RED + "Sorry! " + C.GRAY + "That player is no longer online!");
			return true;
		}

		plugin.getMessaging().put(player.getUniqueId(), target.getUniqueId());
		plugin.getMessaging().put(target.getUniqueId(), player.getUniqueId());

		String message = StringUtils.join(args, ' ', 0, args.length);

		player.sendMessage(
				C.GRAY + C.ITALIC + "To " + C.AQUA + C.ITALIC + target.getName()
				+ C.WHITE + C.ITALIC + ": " + C.DARK_AQUA + C.ITALIC
				+ message.trim());
		target.sendMessage(
				C.GRAY + C.ITALIC + "From " + C.AQUA + C.ITALIC + player.getName()
				+ C.WHITE + C.ITALIC + ": " + C.DARK_AQUA + C.ITALIC
				+ message.trim());

		target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.5F, 1.5F);

		return true;
	}
}
