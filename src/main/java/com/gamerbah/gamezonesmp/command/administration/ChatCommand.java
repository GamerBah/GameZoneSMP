package com.gamerbah.gamezonesmp.command.administration;
/* Created by GamerBah on 2/1/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ChatCommand(GameZoneSMP plugin) implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		GameProfile profile = plugin.getProfileManager().getCachedProfile(player.getUniqueId());

		if (profile.hasRank(Rank.MODERATOR)) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("clear")) {
					for (int i = 0; i <= 100; i++) {
						for (Player players : plugin.getServer().getOnlinePlayers()) {
							players.sendMessage(" ");
						}
					}
					for (Player p : Bukkit.getOnlinePlayers()) {
						M.info(player, "Chat was cleared by " + profile.displayName());
						p.sendMessage(" ");
						EventSound.playSound(p, EventSound.ACTION_SUCCESS);
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("toggle")) {
					if (!plugin.isSilenced()) {
						plugin.setSilenced(true);
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.sendMessage(" ");
							M.info(player, "Chat has been locked by " + profile.displayName());
							p.sendMessage(" ");
							EventSound.playSound(p, EventSound.ACTION_SUCCESS);
						}
					} else {
						plugin.setSilenced(false);
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.sendMessage(" ");
							M.info(player, "Chat is now enabled");
							p.sendMessage(" ");
							EventSound.playSound(p, EventSound.ACTION_SUCCESS);
						}
					}
					return true;
				}

				M.incorrectUsage(player, "/chat <clear/toggle>");
				return false;
			}
		}

		M.noPermission(player);
		return false;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		var options = new ArrayList<String>(2);
		if (args.length == 1) {
			options.add("clear");
			options.add("toggle");
		}
		return options;
	}

}
