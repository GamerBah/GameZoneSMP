package com.gamerbah.gamezonesmp.command.administration;
/* Created by GamerBah on 2/1/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MaintenanceCommand implements CommandExecutor {

	private final GameZoneSMP plugin;

	public MaintenanceCommand(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

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
				player.sendMessage(M.ARROW_ERROR + C.GRAY + "Code required.");
				EventSound.playSound(player, EventSound.ACTION_FAIL);
				return true;
			}

			if (!args[0].equals(plugin.getConfig().getString("maintenancePassword"))) {
				player.sendMessage(M.ARROW_ERROR + C.GRAY + "Incorrect code.");
				return true;
			}

			if (!plugin.getConfig().getBoolean("developmentMode")) {
				plugin.getUpdateManager().enterMaintenance();
				plugin.getConfig().set("developmentMode", true);
				plugin.saveConfig();
			} else {
				plugin.getUpdateManager().setDevelopmentMode(false);
				plugin.getConfig().set("developmentMode", false);
				plugin.saveConfig();
				for (Player players : plugin.getServer().getOnlinePlayers()) {
					players.sendMessage(C.RED + C.BOLD + "\nSERVER IS NO LONGER IN " + C.GOLD + C.BOLD + "MAINTENANCE MODE\n ");
					EventSound.playSound(players, EventSound.ACTION_SUCCESS);
				}
			}
			return true;
		}
		return false;
	}

}
