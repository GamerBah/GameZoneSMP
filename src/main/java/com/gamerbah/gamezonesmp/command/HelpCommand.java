package com.gamerbah.gamezonesmp.command;
/* Created by GamerBah on 11/3/18 */

import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		player.sendMessage(C.WHITE + " \u00bb  " + Hex.TEAL + "Game Zone SMP - Command Help");
		player.sendMessage(Hex.LAVENDER + "/afk" + Hex.SLATE + " \u00bb Mark yourself as away-from-keyboard");
		player.sendMessage(Hex.LAVENDER + "/home" + Hex.SLATE + " \u00bb Teleport to your bed if you have full health");
		player.sendMessage(Hex.LAVENDER + "/message" + Hex.SLATE + " \u00bb Whisper to another player");
		player.sendMessage(Hex.LAVENDER + "/ping" + Hex.SLATE + " \u00bb Check your ping (slightly inaccurate)");
		player.sendMessage(Hex.LAVENDER + "/pvp" + Hex.SLATE + " \u00bb Toggle PVP for yourself");
		player.sendMessage(Hex.LAVENDER + "/reply" + Hex.SLATE + " \u00bb Reply to the most recent whisper");
		player.sendMessage(Hex.LAVENDER + "/rules" + Hex.SLATE + " \u00bb Show the server rules");
		player.sendMessage(Hex.LAVENDER + "/spawn" + Hex.SLATE + " \u00bb Teleport to the world's spawn location");
		//player.sendMessage(Hex.LAVENDER + "/tpr" + Hex.SLATE + " \u00bb Send and accept teleport requests");
		player.sendMessage(Hex.LAVENDER + "/twitch" + Hex.SLATE + " \u00bb Listen to Twitch chats (slightly buggy)");

		return false;
	}
}
