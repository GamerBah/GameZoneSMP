package com.gamerbah.gamezonesmp.command;
/* Created by GamerBah on 8/7/2016 */

import com.gamerbah.gamezonesmp.util.message.C;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		player.sendMessage(" ");
		player.sendMessage("            \u00AB  [ " + C.RED + "SERVER RULES " + C.WHITE + "]  \u00BB             ");
		player.sendMessage(C.GOLD + " \u00BB " + C.YELLOW + "Be respectful to everyone.");
		player.sendMessage(C.GOLD + " \u00BB " + C.YELLOW + "Play fair (no hacked clients or use of glitches).");
		player.sendMessage(C.GRAY + "    - Ask about specific Minecraft bugs before using them!");
		player.sendMessage(C.GOLD + " \u00BB " + C.YELLOW + "No racist, sexual, or other offensive remarks.");
		player.sendMessage(C.GOLD + " \u00BB " + C.YELLOW + "No griefing, stealing, or trolling.");
		player.sendMessage(C.GRAY + "    - Even if PVP is disabled, don't set player traps!");
		player.sendMessage(C.GOLD + " \u00BB " + C.YELLOW + "Ask before helping someone or building near them.");
		player.sendMessage(C.GOLD + " \u00BB " + C.YELLOW + "No spamming / advertising.");
		player.sendMessage(" ");

		return false;
	}

}
