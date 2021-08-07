package com.gamerbah.gamezonesmp.command.administration;
/* Created by GamerBah on 2/1/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record SetLocationCommand(GameZoneSMP plugin) implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		GameProfile gameProfile = plugin.getProfileManager().getProfile(player.getUniqueId());

		if (gameProfile != null) {
			if (!gameProfile.hasRank(Rank.OWNER)) {
				M.noPermission(player);
				return true;
			}

			if (args.length != 1) {
				M.incorrectUsage(player, "/setlocation <afk|spawn>");
				EventSound.playSound(player, EventSound.ACTION_FAIL);
				return true;
			}

			Location location = new Location(player.getWorld(), player.getLocation().getBlockX(),
			                                 player.getLocation().getBlockY(), player.getLocation().getBlockZ()).add(0.5, 0, 0.5);
			location.setYaw(player.getLocation().getYaw());
			location.setPitch(0);

			if (args[0].equalsIgnoreCase("spawn")) {
				plugin.getConfig().set("locations.spawn", location);
				plugin.saveConfig();
				player.sendMessage(M.ARROW_SUCCESS +  "Spawn location set");
				EventSound.playSound(player, EventSound.ACTION_SUCCESS);
				return true;
			} else if (args[0].equalsIgnoreCase("afk")) {
				plugin.getConfig().set("locations.afk", location);
				plugin.saveConfig();
				player.sendMessage(M.ARROW_SUCCESS +  "AFK location set");
				EventSound.playSound(player, EventSound.ACTION_SUCCESS);
				return true;
			} else {
				M.incorrectUsage(player, "/setlocation <afk|spawn>");
				EventSound.playSound(player, EventSound.ACTION_FAIL);
				return true;
			}
		}
		return false;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("setlocation")) {
			if (args.length == 1) {
				ArrayList<String> options = new ArrayList<>();
				options.add("afk");
				options.add("spawn");
				return options;
			}
		}
		return null;
	}

}
