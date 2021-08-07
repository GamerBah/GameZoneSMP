package com.gamerbah.gamezonesmp.command.administration;
/* Created by GamerBah on 8/7/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record RankCommand(GameZoneSMP plugin) implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		if (sender instanceof Player player) {
			GameProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
			if (profile != null) {
				if (!profile.hasRank(Rank.ADMIN)) {
					M.noPermission(player);
					return true;
				}
			}
		}

		if (args.length != 2) {
			if (sender instanceof Player) {
				M.incorrectUsage(sender, M.ARROW_ERROR + C.GRAY + "/rank <player> <rank>");
			} else {
				sender.sendMessage("Incorrect usage: /rank <player> <rank>");
			}
			return true;
		}


		OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);

		for (Rank rank : Rank.values()) {
			if (rank.getName().equalsIgnoreCase(args[1])) {
				GameProfile profile = plugin.getProfileManager().getProfile(target.getUniqueId());
				if (profile != null) {
					if (profile.getRank() == rank) {
						sender.sendMessage(
								M.ARROW_ERROR + ChatColor.GOLD + profile.getName() + C.GRAY + " already has that rank");
						if (sender instanceof Player) {
							EventSound.playSound((Player) sender, EventSound.ACTION_FAIL);
						}
						return true;
					}
					profile.setRank(rank);
					if (target.isOnline()) {
						((Player) target).setPlayerListName(profile.displayName());
						((Player) target).sendMessage(M.ARROW_INFO + "Your rank was changed to " + rank +
						                              (sender instanceof Player ? ChatColor.GRAY + " by " +
						                                                          sender.getName() : ""));
						EventSound.playSound((Player) target, EventSound.ACTION_SUCCESS);
						plugin.getScoreboardManager().assign((Player) target);
					}

					if (!profile.hasRank(Rank.MODERATOR)) {
						profile.setSilent(false);
					}
					if (sender != target) {
						sender.sendMessage(M.ARROW_SUCCESS + "Changed " + target.getName() + "'s rank to " + rank);
					}
					if (sender instanceof Player) {
						EventSound.playSound((Player) sender, EventSound.ACTION_SUCCESS);
					}
				} else {
					if (sender instanceof Player) {
						M.error(sender, "find data for that player");
					} else {
						sender.sendMessage(ChatColor.RED + "That player doesn't exist!");
					}
				}
				return true;
			}
		}

		sender.sendMessage(M.ARROW_ERROR + "That rank doesn't exist");

		if (sender instanceof Player) {
			EventSound.playSound((Player) sender, EventSound.ACTION_FAIL);
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
		if (command.getName().equalsIgnoreCase("rank")) {
			if (args.length == 2) {
				ArrayList<String> ranks = new ArrayList<>();
				if (!args[1].equals("")) {
					for (Rank rank : Rank.values()) {
						if (rank.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
							ranks.add(rank.getName());
						}
					}
				} else {
					for (Rank rank : Rank.values()) {
						ranks.add(rank.getName());
					}
				}
				Collections.sort(ranks);
				return ranks;
			}
		}
		return null;
	}

}
