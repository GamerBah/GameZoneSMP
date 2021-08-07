package com.gamerbah.gamezonesmp.command.administration;
/* Created by GamerBah on 1/31/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record FlySpeedCommand(GameZoneSMP plugin) implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		GameProfile gameProfile = plugin.getProfileManager().getCachedProfile(player.getUniqueId());

		if (gameProfile != null) {
			if (!gameProfile.hasRank(Rank.MODERATOR)) {
				M.noPermission(player);
				return true;
			}

			if (args.length != 1) {
				player.sendMessage(C.RED + "/flyspeed <speed>");
				EventSound.playSound(player, EventSound.ACTION_FAIL);
				return true;
			}

			if (!args[0].matches("[0-9]+")) {
				player.sendMessage(C.RED + "Please select a number from 1 to 10!");
				EventSound.playSound(player, EventSound.ACTION_FAIL);
				return true;
			}

			if (Integer.parseInt(args[0]) > 10) {
				player.sendMessage(C.RED + "Please select a number from 1 to 10!");
				EventSound.playSound(player, EventSound.ACTION_FAIL);
				return true;
			}

			float speed = switch (args[0]) {
				case "1" -> 0.1f;
				case "2" -> 0.2f;
				case "3" -> 0.3f;
				case "4" -> 0.4f;
				case "5" -> 0.5f;
				case "6" -> 0.6f;
				case "7" -> 0.7f;
				case "8" -> 0.8f;
				case "9" -> 0.9f;
				case "10" -> 1.0f;
				default -> 0f;
			};
			player.setFlySpeed(speed);
			player.sendMessage(M.ARROW_SUCCESS + C.GRAY + "Your fly speed was set to " + C.GREEN + C.BOLD + args[0]);
			EventSound.playSound(player, EventSound.ACTION_SUCCESS);
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("flyspeed")) {
			if (args.length == 1) {
				ArrayList<String> options = new ArrayList<>();
				for (int x = 1; x <= 10; x++) {
					options.add(String.valueOf(x));
				}
				return options;
			}
		}
		return null;
	}


}
